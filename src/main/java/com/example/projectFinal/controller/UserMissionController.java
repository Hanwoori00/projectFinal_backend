package com.example.projectFinal.controller;

import com.example.projectFinal.dto.UserMissionDto;
import com.example.projectFinal.service.UserMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class UserMissionController {
    @Autowired
    private  UserMissionService userMissionService;

//    @PostMapping("/course")
//    public void addUserMissionsForCourse(@RequestParam String course, @RequestParam String userId) {
//        userMissionService.addUserMissionsForCourse(course, userId);
//    }

    @CrossOrigin
    @PostMapping("/course")
    public void addUserMissionsForCourse(@RequestBody Map<String, String> request,
                                         @CookieValue(name = "accessToken", required = false) String accessToken,
                                         @CookieValue(name = "RefreshToken", required = false) String RefreshToken) {
        String course = request.get("course");
        userMissionService.addUserMissionsForCourse(course, accessToken, RefreshToken);
    }

//    @GetMapping("/missions")
//    public List<UserMissionDto> getUnusedMissionsForUser(@RequestParam String userId) {
//        return userMissionService.getUnusedMissionsForUser(userId);
//    }

    @GetMapping("/learn")
    public List<UserMissionDto> getUnLearnMissionsForUser(@RequestParam String course,
                                                          @CookieValue(name = "accessToken", required = false) String accessToken,
                                                          @CookieValue(name = "RefreshToken", required = false) String RefreshToken) {
        return userMissionService.getUnLearnMissionsForUser(course, accessToken, RefreshToken);
    }

    @PostMapping("/learned")
    public void setLearnMissionsForUser(@RequestBody Map<String, String> request,
                            @CookieValue(name = "accessToken", required = false) String accessToken,
                            @CookieValue(name = "RefreshToken", required = false) String RefreshToken) {
        String missionId = request.get("mission_id");
        userMissionService.setLearnMissionsForUser(accessToken, RefreshToken, missionId);
    }

    @GetMapping("/missions")
    public List<UserMissionDto> getUncompletedMissionsForUser(@CookieValue(name = "accessToken", required = false) String accessToken,
                                                              @CookieValue(name = "RefreshToken", required = false) String RefreshToken) {
        return userMissionService.getUncompletedMissionsForUser(accessToken, RefreshToken);
    }

//    @CrossOrigin
//    @PostMapping("/checkMission")
//    public ResponseEntity<?> checkMission(@RequestBody String postData) {
//        System.out.println("Received data:");
//        System.out.println(postData);
//
//        return ResponseEntity.ok().build();
//    }

    @CrossOrigin
    @PostMapping("/checkMission")
    public ResponseEntity<String> checkMission(@RequestBody String postData) throws IOException {
        System.out.println("Received data from client: " + postData);

        // 데이터 처리 로직
//        missionService.makePrompt(postData);
        String response = userMissionService.textPrompt(postData);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/missionComplete")
    public ResponseEntity<String> SetMissionCompleteForUSer(@RequestBody Map<String, List<String>> request,
                                                       @CookieValue(name = "accessToken", required = false) String accessToken,
                                                       @CookieValue(name = "RefreshToken", required = false) String RefreshToken) {
        try {
            List<String> missionIds = request.get("mission_id");
            userMissionService.SetMissionCompleteForUSer(accessToken, RefreshToken, missionIds);
            return ResponseEntity.ok("Learned missions marked successfully.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while marking missions as learned.");
        }
    }


    @PostMapping("/testComplete")
    public ResponseEntity<String> completeMissions(@RequestBody Map<String, List<String>> requestBody) {
        List<String> missionIds = requestBody.get("missionId");
        userMissionService.updateMissions(missionIds);
        return ResponseEntity.status(HttpStatus.OK).body("Missions completed successfully.");
    }
}
