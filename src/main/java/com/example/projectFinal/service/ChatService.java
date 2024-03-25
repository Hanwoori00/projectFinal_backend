package com.example.projectFinal.service;

import com.example.projectFinal.dto.ChatDto;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


@Service
public class ChatService {

	public String[] getCorrection(ChatDto chatDto) {
		String[] messages = chatDto.getMessages();
		CorrectAi ai = new CorrectAi();

		HttpURLConnection connection = null;
		try {
			// 서버 용 json 코드
			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/home/ubuntu/.config/gcloud/application_default_credentials.json"));
			// 로컬 용 테스트 코드
			//GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
			String oAuthToken = credentials.refreshAccessToken().getTokenValue();
			URL url = new URL("https://asia-northeast3-aiplatform.googleapis.com/v1/projects/teampj-final/locations/asia-northeast3/publishers/google/models/text-bison-32k:predict");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", "Bearer " + oAuthToken);
			connection.setDoOutput(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String stringifiedMessages = stringifyMessage(messages);
		try {
			String requestBody = "{\"instances\":[{\"content\":\"" + ai.context + stringifiedMessages + "\"}],\"parameters\":{\"maxOutputTokens\":8192,\"temperature\":0.1,\"topP\":1}}";
			if (connection != null) {
				DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
				outputStream.writeBytes(requestBody);
				outputStream.flush();
				outputStream.close();
				int responseCode = connection.getResponseCode();
				System.out.println("Response Code: " + responseCode);
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				// 응답(JSON)중 대답만 골라내기
				JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
				JsonArray predictions = jsonResponse.getAsJsonArray("predictions");
				JsonObject firstPrediction = predictions.get(0).getAsJsonObject();
				String content = firstPrediction.get("content").getAsString();
				String[] listifiedMessages = content.split("\\n");
				return listifiedMessages;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null; // 에러 발생 시 null 반환 혹은 다른 방식으로 처리
	}

	public String stringifyMessage(String[] messages) {
		StringBuilder result = new StringBuilder();
		for (String message : messages) {
			result.append(message).append("\n");
		}
		return result.toString();
	}

    public String getAnswer(ChatDto chatDto) { // chatDto 의 aiMsg 에 pooh 의 대답을 set 한다.
		String[] messages = chatDto.getMessages();
		Pooh pooh = new Pooh();
		String msgQuery = makeMessagesQuery(messages);
		HttpURLConnection connection = null;
		try {// Google Cloud의 기본 자격 증명을 사용하여 GoogleCredentials 객체 생성
			// 서버 용 json 코드
			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/home/ubuntu/.config/gcloud/application_default_credentials.json"));
			// 로컬 용 테스트 코드
			//GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();			// OAuth 2.0 토큰 얻기
			String oAuthToken = credentials.refreshAccessToken().getTokenValue();
			// API 엔드포인트 URL
			URL url = new URL("https://asia-northeast3-aiplatform.googleapis.com/v1/projects/teampj-final/locations/asia-northeast3/publishers/google/models/chat-bison:predict");
			// 연결 설정
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", "Bearer " + oAuthToken); // OAuth 2.0 토큰 설정
			connection.setDoOutput(true);
		} catch (IOException e) {
			e.printStackTrace();
		} // 여기까지 connection 을 건짐.

		try {
			// 요청 본문
			// 변수로부터 값을 가져와 문자열에 삽입
			String requestBody = "{\"instances\": [{\"context\": \"" + pooh.validContext + "\",\"messages\": [" + msgQuery + "]}],\"parameters\": {\"temperature\": 0.3,\"maxOutputTokens\": 200,\"topP\": 0.8,\"topK\": 40}}";
			//		System.out.println(requestBody);
			// 요청 보내기
			if (connection != null){
				DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
				outputStream.writeBytes(requestBody);
				outputStream.flush();
				outputStream.close();
				// 응답 읽기
				int responseCode = connection.getResponseCode();
				System.out.println("Response Code: " + responseCode);
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// 응답(JSON)중 대답만 골라내기
				JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
				JsonArray predictions = jsonResponse.getAsJsonArray("predictions");
				JsonObject firstPrediction = predictions.get(0).getAsJsonObject();
				JsonArray candidates = firstPrediction.getAsJsonArray("candidates");
				String content = candidates.get(0).getAsJsonObject().get("content").getAsString();
				// 응답 출력
				chatDto.setAiMsg(content); // 여기에 대답 넣음
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return chatDto.getAiMsg();

		}
	private String makeMessagesQuery(String[] messages) {
		StringBuilder msgQuery = new StringBuilder();

		for (String message : messages) {
			char messageType = message.charAt(0); // 메시지 타입 ('p' 또는 'u')
			String content = message.substring(6); // 메시지 내용

			// 메시지 타입에 따라 적절한 문자열 생성 후 msgQuery에 추가
			if (messageType == 'p') {
				// 'p'로 시작하는 경우
				String poohMsg = "{\"author\": \"bot\",\"content\": \"" + content + "\"},";
				msgQuery.append(poohMsg);
			} else if (messageType == 'u') {
				// 'u'로 시작하는 경우
				String userMsg = "{\"author\": \"user\",\"content\": \"" + content + "\"},";
				msgQuery.append(userMsg);
			}
		}

		// 마지막 쉼표 제거
		if (msgQuery.length() > 0) {
			msgQuery.deleteCharAt(msgQuery.length() - 1);
		}

		return msgQuery.toString();
	}
}

