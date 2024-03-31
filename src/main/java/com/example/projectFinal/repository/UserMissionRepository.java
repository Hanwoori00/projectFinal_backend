package com.example.projectFinal.repository;

import com.example.projectFinal.entity.MissionEntity;
import com.example.projectFinal.entity.User;
import com.example.projectFinal.entity.UserMissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMissionRepository extends JpaRepository<UserMissionEntity, Integer> {
    List<UserMissionEntity> findByUserIdAndMissionId_Course(User user, String course);
    List<UserMissionEntity> findByUserIdAndCompleteAndLearn(User user, boolean complete, boolean learn);
    List<UserMissionEntity> findByUserIdAndLearnAndMissionId_Course(User user, boolean learn, String course);
    UserMissionEntity findByUserIdAndMissionIdAndLearn(User user, MissionEntity mission, boolean learn);
    List<UserMissionEntity> findByUserIdAndMissionIdIn(User user, List<MissionEntity> missions);

}
