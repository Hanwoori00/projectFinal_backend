package com.example.projectFinal.controller;

import com.example.projectFinal.dto.UserDto;
import com.example.projectFinal.entity.User;
import com.example.projectFinal.service.S3Service;
import com.example.projectFinal.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
    //    유저 컨트롤러
    private final UserService userService;
    private final S3Service s3Service;

    public UserController(UserService userService, S3Service s3Service) {
        this.userService = userService;
        this.s3Service = s3Service;
    }

    @PostMapping("/register")
    public User register(@RequestBody UserDto.RegisterDto registerDto) throws Exception{
        return this.userService.SignUp(registerDto);
    }

    @GetMapping("/checkDupId")
    public boolean CheckDupID(@RequestParam("InputId") String InputId) throws Exception{
        return this.userService.CheckDupId(InputId);
    }

    @GetMapping("/checkDupNick")
    public boolean CheckDupNickname(@RequestParam("nickname") String NickName) throws Exception{
        return this.userService.CheckDupNick(NickName);
    }

    @PostMapping("/upload")
    public void UploadProfileImg(@RequestParam("image") MultipartFile image) throws IOException{
        System.out.println(image);
        System.out.println(this.s3Service.upload(image));
    }


}
