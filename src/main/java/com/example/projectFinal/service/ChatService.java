package com.example.projectFinal.service;

import com.example.projectFinal.dto.ChatDto;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


@Service
public class ChatService {
    public String getAnswer(ChatDto chatDto) { // chatDto 의 aiMsg 에 pooh 의 대답을 set 한다.
        String userMsg = chatDto.getUserMsg();
		Pooh pooh = new Pooh();
		HttpURLConnection connection = null;
		try {// Google Cloud의 기본 자격 증명을 사용하여 GoogleCredentials 객체 생성
			GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
			// OAuth 2.0 토큰 얻기
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
			String requestBody = "{\"instances\": [{\"context\": \"" + pooh.validContext + "\",\"messages\": [{\"author\": \"user\",\"content\": \"" + chatDto.getUserMsg() + "\"}]}],\"parameters\": {\"temperature\": 0.3,\"maxOutputTokens\": 200,\"topP\": 0.8,\"topK\": 40}}";
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

//		[{\"author\": \"user\",\"content\": \"Are my favorite movies based on a book series?\"},{\"author\": \"bot\",\"content\": \"Yes, your favorite movies, The Lord of the Rings and The Hobbit, are based on book series by J.R.R. Tolkien.\"},{\"author\": \"user\",\"content\": \"When were these books published?\"}]
//String messages = "[]";
//		Scanner scanner = new Scanner(System.in);
//		while (true){
//			System.out.println("무슨 말이든 해보세요");
//
//			String messages = scanner.nextLine();
////			String myMsg = scanner.next();
////			messages = addMyMsg(myMsg, messages);
//			if (messages.equals("exit")) break;
////			if (myMsg.equals("exit")) break;
//
//
//		}
//		scanner.close();
    }

}

//public static String addMyMsg (String myMsg, String messages) {
//	StringBuilder sb = new StringBuilder(messages);
//	//		[{\"author\": \"user\",\"content\": \"Are my favorite movies based on a book series?\"},{\"author\": \"bot\",\"content\": \"Yes, your favorite movies, The Lord of the Rings and The Hobbit, are based on book series by J.R.R. Tolkien.\"},{\"author\": \"user\",\"content\": \"When were these books published?\"}]
//	// messages = "[]"; 확인됨.
//	int point = messages.length();
//	String newMessages = "{\\\"author\\\": \\\"user\\\",\\\"content\\\": \\\"" + myMsg + "\\\"}" ;
//	sb.insert(point, newMessages);
//	//		System.out.print("----------------------------newMsg:");
//	//		System.out.println(newMessages);
//	return newMessages;
//}
