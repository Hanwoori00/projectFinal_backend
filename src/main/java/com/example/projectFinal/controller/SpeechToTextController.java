package com.example.projectFinal.controller;

import com.example.projectFinal.service.STTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class SpeechToTextController {

    @Autowired
    STTService sttService;

    @CrossOrigin
    @PostMapping("/speech")
    public String stt(@RequestParam("audio") MultipartFile audioFile) {
        try {
            return sttService.SpeechToText(audioFile);
        } catch (IOException e) {
            throw new RuntimeException("stt fail: " + e);
        }

    }
}
