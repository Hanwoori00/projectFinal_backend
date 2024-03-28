package com.example.projectFinal.service;

import com.example.projectFinal.dto.ChatDto;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PracticeService {
//    String oAuthToken;
//    WebClient textClient;
//    public void createConnection() throws IOException {
//        // 서버 코드
////        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/home/ubuntu/.config/gcloud/application_default_credentials.json"));
//        // 로컬 코드
//		GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
//        oAuthToken = credentials.refreshAccessToken().getTokenValue();
//        String baseUrl_text = "https://asia-northeast3-aiplatform.googleapis.com/v1/projects/teampj-final/locations/asia-northeast3/publishers/google/models/text-bison-32k:predict";
//        textClient = WebClient.create(baseUrl_text);
//    }


    public Map<String, Object> getPractice(String expression) throws IOException {
        //
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        String oAuthToken = credentials.refreshAccessToken().getTokenValue();
        String baseUrl_text = "https://asia-northeast3-aiplatform.googleapis.com/v1/projects/teampj-final/locations/asia-northeast3/publishers/google/models/text-bison-32k:predict";
        WebClient textClient = WebClient.create(baseUrl_text);
        //
        PracticeAi ai = new PracticeAi();
        String requestBody = "{\"instances\":[{\"content\":\"" + ai.context + expression + "\"}],\"parameters\":{\"maxOutputTokens\":8192,\"temperature\":0.1,\"topP\":1}}";
        String response = textClient.post()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + oAuthToken) // OAuth 토큰을 헤더에 포함
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        JsonArray predictions = jsonResponse.getAsJsonArray("predictions");
        JsonObject firstPrediction = predictions.get(0).getAsJsonObject();
        String result = firstPrediction.get("content").getAsString();
        System.out.println("????????????????????????????????????????????????"+result);
        String[] resultSplit = result.split(",, ");
        //***************************************************
        //여기부터 표현 넣어서 5개문장 result로 담아올때 스플릿하고 json으로 나눠서 보내주고 postman으로 검증해봐.
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("ex1", resultSplit[0]);
        responseBody.put("ex2", resultSplit[1]);
        responseBody.put("ex3", resultSplit[2]);
        responseBody.put("conversationA", resultSplit[3]);
        responseBody.put("conversationB", resultSplit[4]);
        return responseBody;
    }
}