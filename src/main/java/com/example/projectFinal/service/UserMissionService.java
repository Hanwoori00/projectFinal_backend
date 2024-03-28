package com.example.projectFinal.service;

import com.example.projectFinal.entity.MissionEntity;
import com.example.projectFinal.entity.User;
import com.example.projectFinal.entity.UserMissionEntity;
import com.example.projectFinal.repository.MissionRepository;
import com.example.projectFinal.repository.UserMissionRepository;
import com.example.projectFinal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMissionService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MissionRepository missionRepository;

    @Autowired
    UserMissionRepository userMissionRepository;

    public void addUserMissionsForCourse(String course, String userId) {
        // user 찾기
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return;
        }

        // 선택한 코스에 해당하는 미션 데이터
        List<MissionEntity> missions = missionRepository.findByCourse(course);

        for (MissionEntity mission : missions) {
            UserMissionEntity userMission = UserMissionEntity.builder()
                    .userId(user)
                    .missionId(mission)
                    .used(false)
                    .build();

            userMissionRepository.save(userMission);
        }

    }


    public List<UserMissionEntity> getUnusedMissionsForUser(String userId) {
        // user 찾기
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return Collections.emptyList();
        }

        // 사용하지 않은 미션 가져오기
        List<UserMissionEntity> unusedMissions = userMissionRepository.findByUserIdAndUsed(user, false);

        Collections.shuffle(unusedMissions);
        return unusedMissions.stream().limit(3).collect(Collectors.toList());

    }
}
