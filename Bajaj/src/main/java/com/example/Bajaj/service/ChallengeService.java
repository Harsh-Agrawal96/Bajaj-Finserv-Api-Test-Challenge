package com.example.Bajaj.service;

import com.example.Bajaj.dto.SolutionRequest;
import com.example.Bajaj.dto.UserRequest;
import com.example.Bajaj.dto.WebhookResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ChallengeService implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    // URLs from the document
    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting the startup app");

        // Create the initial request body with your details [cite: 11, 12, 13, 14]
        UserRequest userRequest = new UserRequest("John Doe", "REG12347", "john@example.com");

        System.out.println("Step 1: Generating webhook with details: " + userRequest);
        try {
            // Use 'exchange' instead of 'postForObject' to get the full response
            ResponseEntity<WebhookResponse> responseEntity = restTemplate.exchange(
                GENERATE_WEBHOOK_URL,
                HttpMethod.POST,
                new HttpEntity<>(userRequest),
                WebhookResponse.class
            );

            WebhookResponse webhookResponse = responseEntity.getBody();

            if (webhookResponse == null || webhookResponse.getWebhookURL() == null) {
                System.err.println("The server responded, but the webhook data is missing. Response: " + webhookResponse);
                return;
            }
            
            System.out.println("Webhook URL received: " + webhookResponse.getWebhookURL());
            System.out.println("Access Token received.");

            
            System.out.println("Step 2: Solving the SQL problem...");
            String finalQuery = solveSqlProblem(userRequest.getRegNo());
            System.out.println("Final SQL Query: " + finalQuery);

            System.out.println("Step 3: Submitting the solution...");
            submitSolution(webhookResponse.getWebhookURL(), webhookResponse.getAccessToken(), finalQuery);
            
            System.out.println("Challenge process completed successfully!");

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            
            System.err.println("An HTTP error occurred while generating the webhook!");
            System.err.println("Status Code: " + e.getStatusCode());
            System.err.println("Error Body: " + e.getResponseBodyAsString());
            System.err.println("Check the 'Error Body' above for the reason.");
        } catch (Exception e) {
        
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private String solveSqlProblem(String regNo) {
        // Extract the last two digits of the registration number
        int lastTwoDigits = Integer.parseInt(regNo.substring(regNo.length() - 2));

        // IMPORTANT: You must replace the placeholder queries below with your actual solution
        // from the Google Drive links provided in the document. [cite: 21, 23]
        if (lastTwoDigits % 2 != 0) {
            // Logic for ODD numbers (Question 1) [cite: 20]
            // This is a placeholder. Get the actual question from the URL.
            return "SELECT user_id, COUNT(order_id) FROM orders WHERE order_date > '2025-01-01' GROUP BY user_id;";
        } else {
            // Logic for EVEN numbers (Question 2) [cite: 22]
            // This is a placeholder. Get the actual question from the URL.
            return "SELECT department, AVG(salary) FROM employees JOIN details ON employees.id = details.emp_id WHERE details.is_active = TRUE GROUP BY department;";
        }
    }

    private void submitSolution(String webhookUrl, String accessToken, String finalQuery) {
        // Set the required headers: Authorization and Content-Type [cite: 26, 27, 28]
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken); // Per document, not "Bearer " + accessToken

        // Create the request body [cite: 30, 31]
        SolutionRequest solutionRequest = new SolutionRequest(finalQuery);

        // Create the full HTTP request entity
        HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);

        // Send the final POST request [cite: 24, 25]
        String response = restTemplate.postForObject(webhookUrl, entity, String.class);
        System.out.println("Submission Response: " + response);
    }
}
