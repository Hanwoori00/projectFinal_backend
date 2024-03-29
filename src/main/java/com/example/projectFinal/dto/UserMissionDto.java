package com.example.projectFinal.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMissionDto {
    private String missionId;
    private String mission;
    private String meaning;
    private boolean complete;

}
