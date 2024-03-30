package com.example.projectFinal.repository;

import com.example.projectFinal.entity.User;
import com.example.projectFinal.entity.UserMissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMissionRepository extends JpaRepository<UserMissionEntity, Integer> {
    List<UserMissionEntity> findByUserIdAndMissionId_Course(User userID, String course);
    List<UserMissionEntity> findByUserIdAndCompleteAndLearn(User userId, boolean complete, boolean learn);

    List<UserMissionEntity> findByUserIdAndLearn(User usrId, boolean learn);
}
