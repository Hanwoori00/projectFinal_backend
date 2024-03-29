package com.example.projectFinal.controller;

import com.example.projectFinal.dto.UserMissionDto;
import com.example.projectFinal.entity.UserMissionEntity;
import com.example.projectFinal.service.UserMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserMissionController {
    @Autowired
    private  UserMissionService userMissionService;

    @PostMapping("/course")
    public void addUserMissionsForCourse(@RequestParam String course, @RequestParam String userId) {
        userMissionService.addUserMissionsForCourse(course, userId);
    }

    @GetMapping("/missions")
    public List<UserMissionDto> getUnusedMissionsForUser(@RequestParam String userId) {
        return userMissionService.getUnusedMissionsForUser(userId);
    }
}
