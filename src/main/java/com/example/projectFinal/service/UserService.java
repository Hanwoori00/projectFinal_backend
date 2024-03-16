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

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void SignUp(UserDto.SignUpDto signUpDto){
        User user = new User();
        user.setUserId(signUpDto.getUserId());
        user.setPassword(signUpDto.getPassword());
        user.setEmail(signUpDto.getEmail());
        user.setNickname(signUpDto.getNickname());
        User result = userRepository.save(user);
        System.out.println(result);
    }

}
