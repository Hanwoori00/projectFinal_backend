package com.example.projectFinal.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageDto {
    private String id;
    private String roomid;
    private String userid;
    private String ai;
    private Boolean userSpeaking;
    private String content;
    private Boolean grammarValid;
    private String correctedContent;
    private LocalDateTime createdAt;
}
