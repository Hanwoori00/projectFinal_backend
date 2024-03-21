package com.example.projectFinal.dto;

import lombok.Data;

@Data
public class ChatDto {

    private String userMsg;
    private String aiMsg;
    private String[] messages;
}
