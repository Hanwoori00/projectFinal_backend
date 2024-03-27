package com.example.projectFinal.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection="messages")
@Getter
@Setter
@NoArgsConstructor
public class Message {
    @Id
    private String id;
    //속한 방
    private String roomid;
    //대화 주체들
    private String userid;
    private String ai;
    //누가 말핬는지
    @Column(nullable = false)
    private Boolean userSpeaking;

    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Boolean grammarValid;
    private String correctedContent;
    @CreatedDate
    private LocalDateTime createdAt;

}