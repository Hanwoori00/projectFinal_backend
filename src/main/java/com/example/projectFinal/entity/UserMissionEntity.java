package com.example.projectFinal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_mission")
public class UserMissionEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int no;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "missionId")
    private MissionEntity missionId;

    @Column(name = "complete")
    private boolean complete;

}
