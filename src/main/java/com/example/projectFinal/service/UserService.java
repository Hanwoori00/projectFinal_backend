package com.example.projectFinal.service;

import com.example.projectFinal.dto.UserDto;
import com.example.projectFinal.entity.User;
import com.example.projectFinal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User SignUp(UserDto.RegisterDto registerDto){
        User user = new User();
        user.setUserId(registerDto.getUserId());
        String encryptPw = bCryptPasswordEncoder.encode(registerDto.getPassword());
        System.out.println("암호화 비밀번호" + encryptPw);
        user.setPassword(encryptPw);
        user.setEmail(registerDto.getEmail());
        user.setNickname(registerDto.getNickname());
        User result = userRepository.save(user);
        System.out.println("회원가입 결과"+ result);
        return result;
    }

}
