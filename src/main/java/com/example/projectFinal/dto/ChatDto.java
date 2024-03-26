package com.example.projectFinal.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


//비즈니스 서비스 내 chat bison 과 통신하기위한 수단. DB 용 아님. DB는 MessageDto
@Getter
@Setter
@Builder
public class ChatDto {

    private String[] missions;
    private String userMsg;
    private String aiMsg;
    private String[] messages;
    private String missionCheck;
}
