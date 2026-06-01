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
  public ResponseEntity<String> chat(@RequestBody Map<String, String> body) {

    try {

      String message = body.get("message");

      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(apiKey);

      // IMPORTANT OpenRouter headers (recommended)
      headers.set("HTTP-Referer", "http://localhost");
      headers.set("X-Title", "Grade System Chat");

      String requestJson = """
          {
            "model": "openrouter/owl-alpha",
            "messages": [
              {
                "role": "system",
                "content": "You are a helpful assistant for a university grading system."
              },
          {
                "role": "user",
                "content": "%s"
          }
            ]
          }
          """.formatted(message.replace("\"", "\\\""));

      HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

      ResponseEntity<String> response = restTemplate.exchange(
          "https://openrouter.ai/api/v1/chat/completions", HttpMethod.POST, entity, String.class);

      return ResponseEntity.ok(response.getBody());

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500)
          .body("{\"error\":\"chat failed\",\"message\":\"" + e.getMessage() + "\"}");
    }
  }
}