package com.example.projectFinal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "mission")
public class MissionEntity {
    @Column(name = "course")
    private String course;

    @Id
    private String missionId;

    @Column(name = "mission")
    private String mission;

    @Column(name = "meaning")
    private String meaning;

}
