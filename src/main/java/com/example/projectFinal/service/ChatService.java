package com.example.projectFinal.service;

import com.example.projectFinal.dto.ChatDto;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class ChatService {
	String oAuthToken;
	WebClient chatAuthClient;
	WebClient textAuthClient;

	@Autowired
	Pooh pooh;
	@Autowired
	CorrectAi correctAi;
	public ChatService() {
		try {
			createConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createConnection() throws IOException {
		// 서버 코드
//		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/home/ubuntu/.config/gcloud/application_default_credentials.json"));
		// 로컬 코드
		GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
		oAuthToken = credentials.refreshAccessToken().getTokenValue();
		String baseUrl_chat = "https://asia-northeast3-aiplatform.googleapis.com/v1/projects/teampj-final/locations/asia-northeast3/publishers/google/models/chat-bison:predict";
		String baseUrl_text = "https://asia-northeast3-aiplatform.googleapis.com/v1/projects/teampj-final/locations/asia-northeast3/publishers/google/models/text-bison-32k:predict";
		chatAuthClient = WebClient.create(baseUrl_chat);
		textAuthClient = WebClient.create(baseUrl_text);
	}

	public ChatDto getAnswer(ChatDto chatDto) {
		String[] messages = chatDto.getMessages();
		String msgQuery = makeMessagesQuery(messages);
		String requestBody = "{\"instances\": [{\"context\": \"" + pooh.validContext + "\",\"messages\": [" + msgQuery + "]}],\"parameters\": {\"temperature\": 0.3,\"maxOutputTokens\": 200,\"topP\": 0.8,\"topK\": 40}}";
		String response = getResponseByAuthClient(requestBody, "chat");
		String content = extractContentOnly(response, "chat");
		String splits[] = content.split(",, ");
		chatDto.setAiMsg(splits[0]); // Setting the response to chatDto
		chatDto.setEmotion(splits[1]);
		return chatDto;
	}

	public String[] getCorrection(ChatDto chatDto) {
		String[] messages = chatDto.getMessages();
		String stringifiedMessages = stringifyMessage(messages);
		String requestBody = "{\"instances\":[{\"content\":\"" + correctAi.context + stringifiedMessages + "\"}],\"parameters\":{\"maxOutputTokens\":8192,\"temperature\":0.5,\"topP\":1}}";
		String respone = getResponseByAuthClient(requestBody, "text");
		String content = extractContentOnly(respone, "text");
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
				.body(BodyInserters.fromValue(requestBody))
				.retrieve()
				.bodyToMono(String.class)
				.block(); // blocking call to wait for response, you might consider async handling instead
	}

	private String makeMessagesQuery(String[] messages) {
		StringBuilder msgQuery = new StringBuilder();

		for (String message : messages) {

			char messageType = message.charAt(0); // 메시지 타입 ('p' 또는 'u')
			String content = message.substring(6).replace("\"", "_");; // 메시지 내용

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
//	public Map<String, Object> missionCheck(ChatDto chatDto) {
//		String[] missionSamples = {
//				"I never thought I'd...",
//				"I may never...",
//				"You should try to ...",
//				"Maybe we should ...",
//				"How do I get to ...?"
//		};
//		String missionsContext = contextifyMissions(missionSamples);
////		String missionsContext = contextifyMissions(chatDto.getMissions());//실제론 미션 리스트 받음.
//		MissionAi ai = new MissionAi();
//		String sentence = chatDto.getUserMsg();
//		String requestBody = "{\"instances\":[{\"content\":\"" + missionsContext + ai.context + sentence + "\"}],\"parameters\":{\"maxOutputTokens\":8192,\"temperature\":0.1,\"topP\":1}}";
//		String response = textClient.post()
//				.header("Content-Type", "application/json")
//				.header("Authorization", "Bearer " + oAuthToken) // OAuth 토큰을 헤더에 포함
//				.body(BodyInserters.fromValue(requestBody))
//				.retrieve()
//				.bodyToMono(String.class)
//				.block(); // blocking call to wait for response, you might consider async handling instead
//		System.out.println(response);
//		JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
//		JsonArray predictions = jsonResponse.getAsJsonArray("predictions");
//		JsonObject firstPrediction = predictions.get(0).getAsJsonObject();
//		String result = firstPrediction.get("content").getAsString();
//		if (result.contains("T")) {
//			// response를 쉼표를 기준으로 분할
//			String[] parts = result.split(", ");
//			if (parts.length >= 2) {
//				// 첫 번째 인자를 boolean으로 변환하여 res1에 설정
//				chatDto.setMissionSuccess(Boolean.parseBoolean(parts[0].trim()));
//				// 두 번째 인자를 Integer로 변환하여 res2에 설정
//				chatDto.setSuccessNumber(Integer.parseInt(parts[1].trim()));
//			}
//		} else {
//			chatDto.setMissionSuccess(Boolean.FALSE);
//			chatDto.setSuccessNumber(0);
//		}
//
//		Map<String, Object> responseBody = new HashMap<>();
//		responseBody.put("isSuccess", chatDto.getMissionSuccess());
//		responseBody.put("successNumber", chatDto.getSuccessNumber());
//
//		return responseBody;
//	}
//
//	public String contextifyMissions(String[] missions) {
//		StringBuilder formattedMissions = new StringBuilder();
//		for (int i = 0; i < missions.length; i++) {
//			formattedMissions.append((i + 1) + ". " + missions[i] + "\n");
//		}
//		return formattedMissions.toString();
//	}

///////////////////////////////

//	public String[] getCorrection(ChatDto chatDto) {
//		String[] messages = chatDto.getMessages();
//
//		HttpURLConnection connection = null;
//		try {
//			// 서버 용 json 코드
//			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/home/ubuntu/.config/gcloud/application_default_credentials.json"));
//			// 로컬 용 테스트 코드
////			GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
//			String oAuthToken = credentials.refreshAccessToken().getTokenValue();
//			URL url = new URL("https://asia-northeast3-aiplatform.googleapis.com/v1/projects/teampj-final/locations/asia-northeast3/publishers/google/models/text-bison-32k:predict");
//			connection = (HttpURLConnection) url.openConnection();
//			connection.setRequestMethod("POST");
//			connection.setRequestProperty("Content-Type", "application/json");
//			connection.setRequestProperty("Authorization", "Bearer " + oAuthToken);
//			connection.setDoOutput(true);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		String stringifiedMessages = stringifyMessage(messages);
//		try {
//			String requestBody = "{\"instances\":[{\"content\":\"" + correctAi.context + stringifiedMessages + "\"}],\"parameters\":{\"maxOutputTokens\":8192,\"temperature\":0.5,\"topP\":1}}";
//			if (connection != null) {
//				DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
//				outputStream.writeBytes(requestBody);
//				outputStream.flush();
//				outputStream.close();
//				int responseCode = connection.getResponseCode();
//				System.out.println("Response Code: " + responseCode);
//				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//				String inputLine;
//				StringBuilder response = new StringBuilder();
//				while ((inputLine = in.readLine()) != null) {
//					response.append(inputLine);
//				}
//				in.close();
//				// 응답(JSON)중 대답만 골라내기
//				JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
//				JsonArray predictions = jsonResponse.getAsJsonArray("predictions");
//				JsonObject firstPrediction = predictions.get(0).getAsJsonObject();
//				String content = firstPrediction.get("content").getAsString();
//				String[] listifiedMessages = content.split("\\n");
//				return listifiedMessages;
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (connection != null) {
//				connection.disconnect();
//			}
//		}
//		return null; // 에러 발생 시 null 반환 혹은 다른 방식으로 처리
//	}
}

