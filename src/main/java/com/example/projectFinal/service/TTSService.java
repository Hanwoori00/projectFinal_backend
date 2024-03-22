package com.example.projectFinal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class TTSService {
    @Value("${tts.api.key}")
    private String xiApiKey;

    private static final int CHUNK_SIZE = 1024;
    private static final String OUTPUT_PATH = "/Users/jeongwon/projectFinal/src/main/resources/static/pooh.mp3"; // 파일 경로 예제 수정

    public void callExternalApi(String text) throws IOException, UnsupportedAudioFileException {
        String ttsUrl = "https://api.elevenlabs.io/v1/text-to-speech/xz0fqwiDAuKTI4bPzIKF"; // URL 수정

        // HttpClient 객체 생성
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(ttsUrl);

            // 헤더 설정
            request.setHeader("Accept", "application/json");
            request.setHeader("xi-api-key", xiApiKey);

            // 데이터 페이로드 설정
            Map<String, Object> data = new HashMap<>();
            data.put("text", text);
            data.put("model_id", "eleven_multilingual_v2");
            Map<String, Object> voiceSettings = new HashMap<>();
            voiceSettings.put("stability", 0.5);
            voiceSettings.put("similarity_boost", 0.8);
            voiceSettings.put("style", 0.0);
            voiceSettings.put("use_speaker_boost", true);
            data.put("voice_settings", voiceSettings);

            // JSON으로 변환
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);

            // 요청 바디 설정
            StringEntity entity = new StringEntity(json);
            request.setEntity(entity);
            request.setHeader("Content-type", "application/json");

            // POST 요청 실행
            try (CloseableHttpResponse response = httpClient.execute(request);
                 OutputStream outputStream = new FileOutputStream(OUTPUT_PATH)) {

                if (response.getStatusLine().getStatusCode() == 200) {
                    InputStream inputStream = response.getEntity().getContent();
                    byte[] buffer = new byte[CHUNK_SIZE];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    System.out.println("Audio stream saved successfully.");
                } else {
                    // 요청 실패시 응답 메세지 출력
                    System.out.println(EntityUtils.toString(response.getEntity()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
