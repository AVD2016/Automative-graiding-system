package uk.ac.rhul.cs2800.controller;

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

  @PostMapping("/send")
  public ResponseEntity<?> chat(@RequestBody Map<String, String> body) {

    try {

      String message = body.get("message");

      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(apiKey);

      // safer JSON construction
      String requestJson = String.format("""
          {
            "model": "gpt-4o-mini",
            "messages": [
              {
                "role": "system",
                "content": "You are helping a user navigate a university grade management system."
              },
              {
                "role": "user",
                "content": %s
              }
            ]
          }
          """, toJsonString(message));

      HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

      ResponseEntity<String> response = restTemplate.exchange(
          "https://api.openai.com/v1/chat/completions", HttpMethod.POST, entity, String.class);

      return ResponseEntity.ok(response.getBody());

    } catch (Exception e) {

      e.printStackTrace();

      return ResponseEntity.status(500)
          .body(Map.of("error", "Chat failed", "message", e.getMessage()));
    }
  }

  private String toJsonString(String input) {
    return "\"" + input.replace("\"", "\\\"") + "\"";
  }
}