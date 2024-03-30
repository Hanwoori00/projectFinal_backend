package com.example.projectFinal.controller;

import com.example.projectFinal.dto.UserMissionDto;
import com.example.projectFinal.entity.UserMissionEntity;
import com.example.projectFinal.service.UserMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserMissionController {
    @Autowired
    private  UserMissionService userMissionService;

//    @PostMapping("/course")
//    public void addUserMissionsForCourse(@RequestParam String course, @RequestParam String userId) {
//        userMissionService.addUserMissionsForCourse(course, userId);
//    }

    @PostMapping("/course")
    public void addUserMissionsForCourse(@RequestParam String course,
                                         @CookieValue(name = "accessToken", required = false) String accessToken,
                                         @CookieValue(name = "RefreshToken", required = false) String RefreshToken) {
        userMissionService.addUserMissionsForCourse(course, accessToken, RefreshToken);
    }

//    @GetMapping("/missions")
//    public List<UserMissionDto> getUnusedMissionsForUser(@RequestParam String userId) {
//        return userMissionService.getUnusedMissionsForUser(userId);
//    }

    @GetMapping("/missions")
    public List<UserMissionDto> getUnusedMissionsForUser(@CookieValue(name = "accessToken", required = false) String accessToken,
                                                         @CookieValue(name = "RefreshToken", required = false) String RefreshToken) {
        return userMissionService.getUnusedMissionsForUser(accessToken, RefreshToken);
    }

//    @CrossOrigin
//    @PostMapping("/checkMission")
//    public ResponseEntity<?> checkMission(@RequestBody String postData) {
//        System.out.println("Received data:");
//        System.out.println(postData);
//
//        return ResponseEntity.ok().build();
//    }
}
