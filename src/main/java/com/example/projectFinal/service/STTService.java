package com.example.projectFinal.service;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class STTService {

    public String SpeechToText(MultipartFile audioFile) throws IOException {
        try (SpeechClient speech = SpeechClient.create()){
            byte[] data = audioFile.getBytes();
            ByteString audioBytes = ByteString.copyFrom(data);

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.ENCODING_UNSPECIFIED)
                    .setLanguageCode("en-US")
                    .setSampleRateHertz(48000)
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
                transcript = Character.toUpperCase(transcript.charAt(0)) + transcript.substring(1);
                transcription.append(transcript);
            }

            return transcription.toString();

        }


    }
}
