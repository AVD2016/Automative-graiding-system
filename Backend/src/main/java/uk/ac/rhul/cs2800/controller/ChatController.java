package uk.ac.rhul.cs2800.controller;

import java.util.Map;
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

  private final String API_KEY = "${HelpChatAPIKey}";

  @PostMapping("/send")
  public ResponseEntity<String> chat(@RequestBody Map<String, String> body) {

    String message = body.get("message");

    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(API_KEY);

    String requestJson =
        """
            {
              "model": "gpt-4o-mini",
              "messages": [
                { "role": "system", "content": "You are helping a user to navigate a grade managment webpage and assist with any questions and requests.

                " },


                { "role": "user", "content": "%s" }
              ]
            }
            """
            .formatted(message);

    HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        "https://api.openai.com/v1/chat/completions", HttpMethod.POST, entity, String.class);

    return ResponseEntity.ok(response.getBody());
  }
}