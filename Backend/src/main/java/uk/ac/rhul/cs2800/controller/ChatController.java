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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

      // 🔑 IMPORTANT: replace later with real userId from frontend
      String sessionId = body.getOrDefault("userId", "default-user");

      // get or create history
      LinkedList<Map<String, String>> history =
          chatHistories.computeIfAbsent(sessionId, k -> new LinkedList<>());

      // add user message
      history.add(Map.of("role", "user", "content", message));

      // keep last 20 messages
      while (history.size() > 20) {
        history.removeFirst();
      }

      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(apiKey);

      headers.set("HTTP-Referer", "https://your-site.vercel.app");
      headers.set("X-Title", "Grade System Chat");

      // -----------------------------
      // Build messages JSON manually
      // -----------------------------
      StringBuilder messages = new StringBuilder();
      messages.append("[");

      // system prompt
      messages.append(
          """
                {
                  "role": "system",
                  "content": "Instructions: You are helping users navigate a university grading system. Use only plain text, 100 words limit. the rest of the context are past messages (yours and users), continue the chat."
                }
              """);

      if (!history.isEmpty()) {
        messages.append(",");
      }

      for (int i = 0; i < history.size(); i++) {

        Map<String, String> msg = history.get(i);

        String role = msg.get("role");
        String content =
            msg.get("content").replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");

        messages.append("""
              {
                "role": "%s",
                "content": "%s"
              }
            """.formatted(role, content));

        if (i < history.size() - 1) {
          messages.append(",");
        }
      }

      messages.append("]");

      String requestJson = """
          {
            "model": "openrouter/owl-alpha",
            "messages": %s
          }
          """.formatted(messages);

      HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

      ResponseEntity<String> response = restTemplate.exchange(
          "https://openrouter.ai/api/v1/chat/completions", HttpMethod.POST, entity, String.class);

      String responseBody = response.getBody();

      System.out.println("OPENROUTER RESPONSE: " + responseBody);

      // JSON
      ObjectMapper mapper = new ObjectMapper();

      JsonNode root = mapper.readTree(responseBody);

      String reply = root.path("choices").get(0).path("message").path("content").asText();

      // store assistant reply
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