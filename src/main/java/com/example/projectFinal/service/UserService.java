package com.example.projectFinal.service;

import com.example.projectFinal.dto.UserDto;
import com.example.projectFinal.entity.User;
import com.example.projectFinal.jwt.TokenProvider;
import com.example.projectFinal.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final TokenProvider tokenProvider;

    @Autowired
    public UserService(UserRepository userRepository, TokenProvider tokenProvider, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public ResponseEntity<User> SignUp(UserDto.RegisterDto registerDto) {
        try {
            User user = new User();
            user.setUserId(registerDto.getUserId());
            String encryptPw = bCryptPasswordEncoder.encode(registerDto.getPassword());
            user.setPassword(encryptPw);
            user.setEmail(registerDto.getEmail());
            user.setNickname(registerDto.getNickname());
            User result = userRepository.save(user);
            System.out.println("회원가입 결과: " + result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {

            log.error("Signup error", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public UserDto.LoginResDto Login(UserDto.LoginDto loginDto){
        Optional<User> SelectId = userRepository.findById(loginDto.getUserId());
        UserDto.LoginResDto result = new UserDto.LoginResDto();
        if(SelectId.isPresent()){
            User user = SelectId.get();

//            비밀번호 일치 여부 확인
            boolean comparePW = bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword());

//          비밀번호 일치 하지 않을 경우 리턴
            if(!comparePW){
                result.setResult(false);
                result.setMsg("비밀번호가 일치하지 않습니다");
                return result;
            }

            UserDto.TokenDto tokenDto = tokenProvider.generateToken(loginDto.getUserId());
            System.out.println("액세스 토큰 확인" + tokenDto.getAccessToken());

            userRepository.updateRefreshToken(loginDto.getUserId(), tokenDto.getRefreshToken());
            result.setResult(true);
            result.setMsg("로그인 성공!");
            result.setAccessToken(tokenDto.getAccessToken());
            result.setRefreshToken(tokenDto.getRefreshToken());
            return result;
        } else{
            result.setResult(false);
            result.setMsg("아이디가 존재하지 않습니다.");
            return result;
        }
    }

    public boolean Logout(String token) {
//        try{
            System.out.println(token);
            UserDto.ResDto loginUser = tokenProvider.validateAndGetUserId(token);
            System.out.println("토큰 유효 여부 확인" + loginUser);

            userRepository.RefreshTokenToNull(loginUser.getMsg());

            return true;
    }

    public UserDto.AuthuserDto authuser(String accessToken, String RefreshToken){
        UserDto.AuthuserDto authuserDto = new UserDto.AuthuserDto();

        if(accessToken != null){
            System.out.println("액세스 토큰 존재");
            UserDto.ResDto validToken = this.tokenProvider.validateAndGetUserId(accessToken);

            System.out.println(validToken.getMsg() + validToken.isResult());

            User user = this.userRepository.findByUserId(validToken.getMsg());

            authuserDto.setResult(true);
            authuserDto.setNickname(user.getNickname());

            return authuserDto;
        }

        User user = this.userRepository.findNicknameFromToken(RefreshToken);
        if(user == null){
            System.out.println("리프레시 토큰 유저 없음");
            authuserDto.setResult(false);
            authuserDto.setNickname(null);
            authuserDto.setUserId(null);

            return authuserDto;
        }

        UserDto.TokenDto tokenDto = this.tokenProvider.generateAccessToken(user.getUserId());

        authuserDto.setNickname(user.getNickname());
        authuserDto.setNewToken(tokenDto.getAccessToken());
        authuserDto.setResult(true);

        return authuserDto;
    }

    public boolean CheckDupId(String UserId){
        return this.userRepository.existsByUserId(UserId);

    }

    public boolean CheckDupNick(String Nickname){
        return this.userRepository.existsByNickname(Nickname);
    }

    public User getUserDto(String nickname){
        return this.userRepository.findByNickname(nickname);
    }

    public boolean uploadProfileImg(String awsurl, String userid){
        return this.userRepository.updateProfileImg(awsurl, userid);
    }

//    public UserDto.ResDto UpdateUserInfo(UserDto.GetUserDto getUserDto, String inputpw) {
//        UserDto.ResDto resDto = new UserDto.ResDto();
//        User user = this.userRepository.findByUserId(getUserDto.getUserId());
//
//        boolean comparePW = bCryptPasswordEncoder.matches(inputpw, user.getPassword());
//
//        if (!comparePW) {
//            resDto.setResult(false);
//            resDto.setMsg("비밀번호가 일치하지 않습니다.");
//        }
//    }
}
