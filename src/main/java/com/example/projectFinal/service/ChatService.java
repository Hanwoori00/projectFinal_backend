package com.example.projectFinal.service;

import com.example.projectFinal.dto.ChatDto;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class ChatService {
	String oAuthToken;
	WebClient chatAuthClient;
	WebClient textAuthClient;
	long lastTokenRefreshTime;

	@Autowired
	Pooh pooh;
	@Autowired
	CorrectAi correctAi;

	public ChatService() {
		try {
			createConnection(); //처음 서버 빌드되어 Bean 이 생성될때 Api 인증받기(createConnection) 먼저 실행
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createConnection() throws IOException {
		// 인증이 1시간에 한번씩 만료됨. 1시간이 지났는지 여부 확인. 만료됐으면 다시 갱신받음.
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastTokenRefreshTime > 3600000) {
			// 서버 코드
			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/home/ubuntu/.config/gcloud/application_default_credentials.json"));
			// 로컬 코드
			//GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
			oAuthToken = credentials.refreshAccessToken().getTokenValue();
			lastTokenRefreshTime = System.currentTimeMillis();
			String baseUrl_chat = "https://asia-northeast3-aiplatform.googleapis.com/v1/projects/teampj-final/locations/asia-northeast3/publishers/google/models/chat-bison:predict";
			String baseUrl_text = "https://asia-northeast3-aiplatform.googleapis.com/v1/projects/teampj-final/locations/asia-northeast3/publishers/google/models/text-bison-32k:predict";
			chatAuthClient = WebClient.create(baseUrl_chat);
			textAuthClient = WebClient.create(baseUrl_text);
		}
	}

	public ChatDto getAnswer(ChatDto chatDto) throws IOException { // 캐릭터의 대답 받는 서비스 로직
		String[] messages = chatDto.getMessages(); //여태까지 했던 대화들
		String msgQuery = makeMessagesQuery(messages); // requestBody 에서 요구하는 양식에 맞춤
		String requestBody = "{\"instances\": [{\"context\": \"" + pooh.validContext + "\",\"messages\": [" + msgQuery + "]}],\"parameters\": {\"temperature\": 0.3,\"maxOutputTokens\": 200,\"topP\": 0.8,\"topK\": 40}}";
		createConnection(); // 요청 보내기전 혹시나 만료됐으면 다시 갱신
		String response = getResponseByAuthClient(requestBody, "chat"); // API 로부터 응답 받음
		String content = extractContentOnly(response, "chat"); // 응답중에 ai 의 대답만 추출
		String splits[] = content.split(",, "); //감정과 대답 분할
		chatDto.setAiMsg(splits[0]);
		chatDto.setEmotion(splits[1]);
		return chatDto;
	}

	public String[] getCorrection(ChatDto chatDto) throws IOException {
		String[] messages = chatDto.getMessages(); // 대화 내역
		String stringifiedMessages = stringifyMessage(messages); // requestBody 에서 요구하는 양식에 맞춤
		String requestBody = "{\"instances\":[{\"content\":\"" + correctAi.context + stringifiedMessages + "\"}],\"parameters\":{\"maxOutputTokens\":8192,\"temperature\":0.5,\"topP\":1}}";
		createConnection();// 요청 보내기전 혹시나 만료됐으면 다시 갱신
		String response = getResponseByAuthClient(requestBody, "text");// API 로부터 응답 받음
		String content = extractContentOnly(response, "text"); // 응답중에 ai 의 대답만 추출
		String[] listifiedMessages = content.split("\\n");
		return listifiedMessages;
	}

	public String stringifyMessage(String[] messages) {
		StringBuilder result = new StringBuilder();
		for (String message : messages) {
			result.append(message).append("\n");
		}
		return result.toString();
	}

	public String extractContentOnly(String response, String responseForm) {
		String content;
		JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
		JsonArray predictions = jsonResponse.getAsJsonArray("predictions");
		JsonObject firstPrediction = predictions.get(0).getAsJsonObject();
		if (responseForm.equals("chat")) {
			JsonArray candidates = firstPrediction.getAsJsonArray("candidates");
			content = candidates.get(0).getAsJsonObject().get("content").getAsString();
		} else {
			content = firstPrediction.get("content").getAsString();
		}
		return content;
	}

	public String getResponseByAuthClient(String requestBody, String clientType) {
		WebClient client = (clientType.equals("chat")) ? chatAuthClient : textAuthClient;
		return client.post()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + oAuthToken) // OAuth 토큰을 헤더에 포함
				.body(BodyInserters.fromValue(requestBody)) // requestBody 도 포함됨
				.retrieve()
				.bodyToMono(String.class)
				.block();
	}

	private String makeMessagesQuery(String[] messages) {
		StringBuilder msgQuery = new StringBuilder();

		for (String message : messages) {

			char messageType = message.charAt(0); // 메시지 타입 ('p' 또는 'u')
			String content = message.substring(6).replace("\"", "_");; // 메시지 내용
			// 메시지 타입에 따라 적절한 문자열 생성 후 msgQuery에 추가
			if (messageType == 'p') {
				// 'p'로 시작하는 경우(pooh 인 경우)
				String poohMsg = "{\"author\": \"bot\",\"content\": \"" + content + "\"},";
				msgQuery.append(poohMsg);
			} else if (messageType == 'u') {
				// 'u'로 시작하는 경우(user 인 경우)
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

