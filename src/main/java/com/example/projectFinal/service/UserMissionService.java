package com.example.projectFinal.service;

import com.example.projectFinal.dto.UserDto;
import com.example.projectFinal.dto.UserMissionDto;
import com.example.projectFinal.entity.MissionEntity;
import com.example.projectFinal.entity.User;
import com.example.projectFinal.entity.UserMissionEntity;
import com.example.projectFinal.repository.MissionRepository;
import com.example.projectFinal.repository.UserMissionRepository;
import com.example.projectFinal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    UserService userService;

    public void addUserMissionsForCourse(String course, String accessToken, String refreshToken) {
        UserDto.AuthuserDto authuserDto = userService.authuser(accessToken, refreshToken);

        if (!authuserDto.isResult()) {
            return;
        }

        // user 찾기
        String userId = authuserDto.getUserId();
        User user = userRepository.findByUserId(userId);


        // 선택한 코스에 해당하는 미션 데이터
        List<MissionEntity> missions = missionRepository.findByCourse(course);

        for (MissionEntity mission : missions) {
            UserMissionEntity userMission = UserMissionEntity.builder()
                    .userId(user)
                    .missionId(mission)
                    .complete(false)
                    .build();

            userMissionRepository.save(userMission);
        }

    }


    public List<UserMissionDto> getUnusedMissionsForUser(String accessToken, String refreshToken) {
        UserDto.AuthuserDto authuserDto = userService.authuser(accessToken, refreshToken);
        if (!authuserDto.isResult()) {
            return Collections.emptyList();
        }

        // user 찾기
        String userId = authuserDto.getUserId();
        User user = userRepository.findByUserId(userId);

        // 사용하지 않은 미션 가져오기
        List<UserMissionEntity> unusedMissions = userMissionRepository.findByUserIdAndComplete(user, false);

        if (unusedMissions.isEmpty()) {
            return Collections.emptyList();
        }

        Collections.shuffle(unusedMissions);
        List<UserMissionEntity> limitedUnusedMissions = unusedMissions.stream().limit(3).toList();

        List<UserMissionDto> result = new ArrayList<>();

        for (UserMissionEntity limitedUnusedMission : limitedUnusedMissions) {
            UserMissionDto userMissionDto = UserMissionDto.builder()
                    .missionId(limitedUnusedMission.getMissionId().getMissionId())
                    .mission(limitedUnusedMission.getMissionId().getMission())
                    .meaning(limitedUnusedMission.getMissionId().getMeaning())
                    .complete(limitedUnusedMission.isComplete())
                    .build();

            result.add(userMissionDto);
        }
        return result;
    }


//    public List<UserMissionEntity> getUnusedMissionsForUser(String userId) {
//        // user 찾기
//        User user = userRepository.findByUserId(userId);
//        if (user == null) {
//            return Collections.emptyList();
//        }
//
//        // 사용하지 않은 미션 가져오기
//        List<UserMissionEntity> unusedMissions = userMissionRepository.findByUserIdAndUsed(user, false);
//
//        Collections.shuffle(unusedMissions);
//        return unusedMissions.stream().limit(3).collect(Collectors.toList());
//
//    }
}
