package com.example.projectFinal.service;

import com.example.projectFinal.dto.ChatDto;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class PracticeService {
    @Autowired
    ChatService chatService;
    @Autowired
    PracticeAi practiceAi;

    public Map<String, Object> getPractice(String expression, String meaning, int level) throws IOException {
        String topic = pickTopic(); // 랜덤으로 어떤 토픽의 예문을 받을지 고름
        String requestBody = "{\"instances\":[{\"content\":\"" +  practiceAi.common_context1 + topic + practiceAi.common_context2 + topic +  practiceAi.common_context3 + expression + "\"}],\"parameters\":{\"maxOutputTokens\":8192,\"temperature\":0.7,\"topP\":1}}";
        chatService.createConnection(); // 예문 받기 전에 api 인증 만료됐는지 확인, 만료시 갱신
        String response = chatService.getResponseByAuthClient(requestBody, "text"); // ai 의 답변 받음
        String result = chatService.extractContentOnly(response, "text"); // 답변중 필요한 내용만 추출
        return makeCustomizedJsonForm(result, expression, meaning, level); // 프론트로 반환해주기 전에 요청한 양식에 맞춰서 json 재구성
    }

    public Map<String, Object> makeCustomizedJsonForm(String result, String expression, String meaning, int level) {
        String[] resultSplit = result.split(",, ");
        // 구성 : {문장1(번역), 문장2(번역), 문장3(번역), 대화A(번역), 대화B(번역)}
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", "level" + level);
        responseBody.put("no", level);
        responseBody.put("sentence", "(레벨" + level + ")" + expression);
        responseBody.put("sentence_translation", meaning);
        //예시 문장3개 : 예문 세개와 번역 세개를 분리해서 각각 배열에 저장
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

    public String pickTopic() {
        String topic;
        Random random = new Random();
        int randomNumber = random.nextInt(10); // 0부터 9까지의 랜덤한 숫자 생성
        String[] topics = {practiceAi.topic0, practiceAi.topic1, practiceAi.topic2, practiceAi.topic3, practiceAi.topic4, practiceAi.topic5, practiceAi.topic6, practiceAi.topic7, practiceAi.topic8, practiceAi.topic9};
        topic = topics[randomNumber];
        return topic;
    }
}