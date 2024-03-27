package com.example.projectFinal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;


@Document(collection="rooms")
@Getter
@Setter
@NoArgsConstructor
public class Room{
    @Id
    private String id;
    @Column(nullable = false)
    private String userid;
    @Column(nullable = false)
    private String ai;
    private String[] messages;
    @CreatedDate
    private LocalDateTime createdAt;
}
