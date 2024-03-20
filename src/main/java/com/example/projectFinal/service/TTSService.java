package com.example.projectFinal.service;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class TTSService {
    @Value("${tts.api.key}")
    private String xiApiKey;
    public void callExternalApi(String text) {
//
//        HttpResponse<String> response = Unirest.post("https://api.elevenlabs.io/v1/text-to-speech/xz0fqwiDAuKTI4bPzIKF")
//                .header("xi-api-key", xiApiKey)
//                .header("Content-Type", "application/json")
//                .body("{\n  \"text\": \"" + text + "\",\n  \"model_id\": \"eleven_monolingual_v1\",\n  \"voice_settings\": {\n    \"similarity_boost\": 0.7,\n    \"stability\": 0.3,\n    \"style\": 0.5\n  }\n}")
//                .asString();
//
//        // 응답 출력
//        System.out.println("TTS api 호출 " + response.);

        int chunkSize = 1024;
        String url = "https://api.elevenlabs.io/v1/text-to-speech/xz0fqwiDAuKTI4bPzIKF";

        String jsonData = "{\"text\": \"" + text + "\", \"model_id\": \"eleven_monolingual_v1\", \"voice_settings\": {\"stability\": 0.3, \"similarity_boost\": 0.7}}";

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "audio/mpeg");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("xi-api-key", xiApiKey);
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(jsonData.getBytes());
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();
            String responseMessage = con.getResponseMessage();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = con.getInputStream();
                FileOutputStream fos = new FileOutputStream("output.mp3");
                byte[] buffer = new byte[chunkSize];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.close();
                is.close();
            } else {
                System.out.println("POST request failed with response code: " + responseCode + responseMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
