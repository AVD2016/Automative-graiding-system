package uk.ac.rhul.cs2800.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(originPatterns = "https://*.vercel.app")
public class ChatController {

  @Value("${HelpChatAPIKey}")
  private String apiKey;

  private final Map<String, LinkedList<Map<String, String>>> chatHistories = new HashMap<>();

  @PostMapping("/send")
  public ResponseEntity<?> chat(@RequestBody Map<String, String> body) {

    try {

      String message = body.get("message");

      // TEMP session id
      // later you can use logged-in student/admin ID
      String sessionId = "default-user";

      // get or create history
      LinkedList<Map<String, String>> history =
          chatHistories.computeIfAbsent(sessionId, k -> new LinkedList<>());

      // add user message
      history.add(Map.of("role", "user", "content", message));

      // keep max 20 messages
      while (history.size() > 20) {
        history.removeFirst();
      }

      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();

      headers.setContentType(MediaType.APPLICATION_JSON);

      headers.setBearerAuth(apiKey);

      headers.set("HTTP-Referer", "https://your-site.vercel.app");

      headers.set("X-Title", "Grade System Chat");

      // build messages JSON
      StringBuilder messagesJson = new StringBuilder();

      messagesJson.append(
          """
              [
                {
                  "role": "system",
                  "content": "You are helping users navigate / use a university grading system. Respond only with plain text, 150 words limit."
                },
              """);

      for (Map<String, String> msg : history) {

        messagesJson.append("""
            {
              "role": "%s",
              "content": "%s"
            },
            """.formatted(msg.get("role"),
            msg.get("content").replace("\"", "\\\"").replace("\n", "\\n")));
      }

      // remove trailing comma
      if (messagesJson.lastIndexOf(",") != -1) {
        messagesJson.deleteCharAt(messagesJson.lastIndexOf(","));
      }

      messagesJson.append("]");

      String requestJson = """
          {
            "model": "openrouter/owl-alpha",
            "messages": %s
          }
          """.formatted(messagesJson);

      HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

      ResponseEntity<String> response = restTemplate.exchange(
          "https://openrouter.ai/api/v1/chat/completions", HttpMethod.POST, entity, String.class);

      // extract assistant reply
      String responseBody = response.getBody();

      String reply = responseBody;

      // VERY SIMPLE extraction
      int start = responseBody.indexOf("\"content\":\"");

      if (start != -1) {

        start += 11;

        int end = responseBody.indexOf("\"", start);

        if (end != -1) {
          reply = responseBody.substring(start, end);
        }
      }

      // add assistant reply to history
      history.add(Map.of("role", "assistant", "content", reply));

      // trim again
      while (history.size() > 20) {
        history.removeFirst();
      }

      return ResponseEntity.ok(Map.of("reply", reply));

    } catch (Exception e) {

      e.printStackTrace();

      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }
}