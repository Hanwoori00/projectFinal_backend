package com.example.projectFinal.dto;

import lombok.Data;

import java.net.HttpURLConnection;

@Data
public class ChatDto {

    private String userMsg;
    private String aiMsg;
    private String[] messages;
}
