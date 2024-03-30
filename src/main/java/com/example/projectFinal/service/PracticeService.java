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
import java.time.LocalDateTime;
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


    public Map<String, Object> getPractice(String expression, String meaning, int level) throws IOException {
        //
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        String oAuthToken = credentials.refreshAccessToken().getTokenValue();
        String baseUrl_text = "https://asia-northeast3-aiplatform.googleapis.com/v1/projects/teampj-final/locations/asia-northeast3/publishers/google/models/text-bison-32k:predict";
        WebClient textClient = WebClient.create(baseUrl_text);
        //
        LocalDateTime now = LocalDateTime.now();
        int seconds = now.getSecond();
        int second = seconds % 10;

        PracticeAi ai = new PracticeAi();
        String[] topics = {ai.topic0, ai.topic1, ai.topic2, ai.topic3, ai.topic4, ai.topic5, ai.topic6, ai.topic7, ai.topic8, ai.topic9};
        String topic = topics[second - 1];

        String requestBody = "{\"instances\":[{\"content\":\"" +  ai.common_context1 + topic + ai.common_context2 + topic +  ai.common_context3 + expression + "\"}],\"parameters\":{\"maxOutputTokens\":8192,\"temperature\":0.1,\"topP\":1}}";
        // 이후 코드는 동일하게 진행
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
        System.out.println("????????????????????????????????????????????????" + result);
        String[] resultSplit = result.split(",, ");
        // {문장1(번역), 문장2(번역), 문장3(번역), 대화A(번역), 대화B(번역)}
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", "level" + level);
        responseBody.put("no", level);
        responseBody.put("sentence", "(레벨" + level + ")" + expression);
        responseBody.put("sentence_translation", meaning);

        //예문3개                                      //예문 세개와 번역 세개를 분리해서 각각 배열에 저장
        String[] similars = new String[3];
        String[] similars_trans = new String[3];
        for (int i = 0; i < 3; i++) {
            String[] temp = resultSplit[i].split("\\(");
            similars[i] = temp[0];
            similars_trans[i] = temp[1].substring(0, temp[1].length() - 1); // 제일 뒤에 '(' 뺌
        }
        responseBody.put("similar", similars);
        responseBody.put("similar_translation", similars_trans);
        //대화2개
        String[] dialogue = new String[2];
        String[] dialogue_translation = new String[2];

        String[] temp = resultSplit[3].split("\\(");
        dialogue[0] = temp[0];
        dialogue_translation[0] = "A: " + temp[1].substring(0, temp[1].length() - 1);
        temp = resultSplit[4].split("\\(");
        dialogue[1] = temp[0];
        dialogue_translation[1] = "B: " + temp[1].substring(0, temp[1].length() - 1);
        responseBody.put("dialogue", dialogue);
        responseBody.put("dialogue_translation", dialogue_translation);
        responseBody.put("used", false);
        return responseBody;
    }
}