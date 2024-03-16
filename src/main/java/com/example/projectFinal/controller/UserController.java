package com.example.projectFinal.controller;

import com.example.projectFinal.dto.UserDto;
import com.example.projectFinal.entity.User;
import com.example.projectFinal.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    //    유저 컨트롤러
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public void register(@RequestBody UserDto.RegisterDto registerDto){
        User result = this.userService.SignUp(registerDto);
        System.out.println(result);

    }

}
