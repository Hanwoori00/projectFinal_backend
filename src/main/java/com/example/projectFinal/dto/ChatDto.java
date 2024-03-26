package com.example.projectFinal.dto;

import lombok.*;


//비즈니스 서비스 내 chat bison 과 통신하기위한 수단. DB 용 아님. DB는 MessageDto
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {

    private String[] missions;
    private String userMsg;
    private String aiMsg;
    private String[] messages;
    private Boolean missionSuccess;
    private Integer successNumber;

}
