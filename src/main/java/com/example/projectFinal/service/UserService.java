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
import org.springframework.transaction.annotation.Transactional;

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

    public UserDto.RegisterResDto register(UserDto.RegisterDto registerDto) {
        User user = new User();
        UserDto.RegisterResDto registerResDto = new UserDto.RegisterResDto();
        try {
            user.setUserId(registerDto.getUserId());
            String encryptPw = bCryptPasswordEncoder.encode(registerDto.getPassword());
            user.setPassword(encryptPw);
            user.setEmail(registerDto.getEmail());
            User result = userRepository.save(user);
            System.out.println("회원가입 결과: " + result);
            registerResDto.setResult(true);
            registerResDto.setUserId(registerDto.getUserId());
            registerResDto.setEmail(registerDto.getEmail());
            registerResDto.setPassword(encryptPw);
            return registerResDto;
        } catch (Exception e) {
            System.out.println("회원 가입 진행 중 에러 발생 " + e);

            registerResDto.setResult(false);
            return registerResDto;
        }
    }

    public UserDto.LoginResDto Login(UserDto.LoginDto loginDto){
        User user = userRepository.findByUserId(loginDto.getUserId());
        UserDto.LoginResDto result = new UserDto.LoginResDto();

        if(user.getUserId() != null){
            if(user.getDeletedAt() == null){
                result.setResult(false);
                result.setMsg("탈퇴한 계정의 아이디입니다.");
                return result;
            }

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

            System.out.println("유저 아이디" + validToken.getMsg());

            authuserDto.setResult(true);
            authuserDto.setNickname(user.getNickname());
            authuserDto.setUserId(validToken.getMsg());

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
        authuserDto.setUserId(user.getUserId());
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
        return this.userRepository.findByUserId(nickname);
    }

    public boolean uploadProfileImg(String awsurl, String userid){
        return this.userRepository.updateProfileImg(awsurl, userid);
    }

    public UserDto.ResDto changePW(String userid, String inputpw, String email) {
        UserDto.ResDto resDto = new UserDto.ResDto();
        try {
            User user = this.userRepository.findByUserId(userid);

            if (!user.getEmail().equals(email)) {
                resDto.setResult(false);
                resDto.setMsg("이메일 정보가 등록된 정보와 일치하지 않습니다.");
                return resDto;
            }

            String encryptPw = bCryptPasswordEncoder.encode(inputpw);

            userRepository.updatePW(userid, encryptPw);

            resDto.setResult(true);
            resDto.setMsg("비밀번호 변경이 완료되었습니다.");
            return resDto;
        } catch (Exception e) {

            resDto.setResult(false);
            resDto.setMsg("비밀번호 변경 중 오류가 발생했습니다.");
            e.printStackTrace();
            return resDto;
        }

    }

    public UserDto.ResDto changeEmail(String userid, String inputpw, String email) {
        UserDto.ResDto resDto = new UserDto.ResDto();
        try {
            User user = this.userRepository.findByUserId(userid);

            boolean comparePW = bCryptPasswordEncoder.matches(inputpw, user.getPassword());

            if(!comparePW){
                resDto.setResult(false);
                resDto.setMsg("비밀번호가 일치하지 않습니다");
                return resDto;
            }

            userRepository.updateEmail(userid, email);

            resDto.setResult(true);
            resDto.setMsg("이메일 변경이 완료되었습니다.");
            return resDto;

        } catch (Exception e) {
            // 예외 발생 시 처리
            resDto.setResult(false);
            resDto.setMsg("이메일 정보 변경 중 에러가 발생하였습니다..");
            e.printStackTrace();
            return resDto;
        }

    }
    @Transactional
    public UserDto.ResDto withdraw(String userId) {
        UserDto.ResDto resDto = new UserDto.ResDto();

        try {
            System.out.println("회원탈퇴 유저 ID" + userId);
            // 회원 아이디의 유효성을 검사하고 존재하지 않는 경우 예외 처리
            if (!userRepository.existsByUserId(userId)) {
                resDto.setResult(false);
                resDto.setMsg("회원 아이디가 유효하지 않습니다.");
                return resDto;
            }

            // 회원 탈퇴를 위해 softDeleteUserById 메소드 호출
            userRepository.softDeleteUserById(userId);

            resDto.setResult(true);
            resDto.setMsg("회원 탈퇴가 완료되었습니다.");
        } catch (Exception e) {
            // 예외 발생 시 롤백되도록 처리
            resDto.setResult(false);
            resDto.setMsg("회원 탈퇴 중 에러가 발생하였습니다.");
            e.printStackTrace(); // 예외 정보 로깅
        }

        return resDto;
    }
}
