package com.example.projectFinal.service;

import com.google.api.client.util.Lists;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class STTService {

    public String SpeechToText(MultipartFile audioFile) throws IOException {

        // Google Cloud Speech-to-Text 사용 위한 사용자 인증
        // Google Cloud 가입, 프로젝트 생성, Speech-to-Text 사용 설정 필요, 서비스 계정 키 파일 생성 필요
        // https://cloud.google.com/docs/authentication/provide-credentials-adc?hl=ko
        // 서비스 계정 키 파일 Google Cloud Console(웹페이지)에서 생성하는게 더 편리합니다!
        // 서비스 계정 키 파일의 경로 OR gcloud cli 설치 후 생성한 사용자 인증 정보 파일의 경로를
        // new FileInputStream() 내부에 입력

        // build.gradle 에 라이브러리 추가 필요
        // https://cloud.google.com/speech-to-text/docs/transcribe-client-libraries?hl=ko#client-libraries-usage-java

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/home/ubuntu/.config/gcloud/application_default_credentials.json"))
                .createScoped(Lists.newArrayList());

        try (SpeechClient speech = SpeechClient.create(
                SpeechSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build()
        )){
            byte[] data = audioFile.getBytes();
            ByteString audioBytes = ByteString.copyFrom(data);

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.WEBM_OPUS) // 제공된 오디오의 인코딩
                    .setLanguageCode("en-US") // 제공된 오디오의 음성 인식에 사용할 언어와 리전/지역
                    .setSampleRateHertz(48000) // 제공된 오디오의 샘플링 레이트 지정
                    .setEnableAutomaticPunctuation(true) // 자동 구두점 가져오기
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            RecognizeResponse response = speech.recognize(config, audio);

            List<SpeechRecognitionResult> results = response.getResultsList();

            StringBuilder transcription = new StringBuilder();
            for (SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
//                transcription.append(alternative.getTranscript());

                String transcript = alternative.getTranscript();

                // 변환된 데이터의 첫 글자 대문자로 변환
                transcript = Character.toUpperCase(transcript.charAt(0)) + transcript.substring(1);
                transcription.append(transcript);
            }

            return transcription.toString();

        }


    }
}
