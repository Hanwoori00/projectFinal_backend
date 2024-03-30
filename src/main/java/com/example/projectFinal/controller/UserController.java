package com.example.projectFinal.controller;

import com.example.projectFinal.dto.ChatDto;
import com.example.projectFinal.dto.UserDto;
import com.example.projectFinal.entity.User;
import com.example.projectFinal.jwt.TokenProvider;
import com.example.projectFinal.service.ChatService;
import com.example.projectFinal.service.S3Service;
import com.example.projectFinal.service.TTSService;
import com.example.projectFinal.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kong.unirest.HttpResponse;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    //    유저 컨트롤러
    private final UserService userService;

    private final TTSService TTSservice;
    private final S3Service s3Service;

    private final ChatService chatService;

    private final TokenProvider tokenProvider;

    public UserController(UserService userService, TTSService ttSservice, S3Service s3Service, ChatService chatService, TokenProvider tokenProvider) {
        this.userService = userService;
        TTSservice = ttSservice;
        this.s3Service = s3Service;
        this.chatService = chatService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public UserDto.RegisterResDto register(@RequestBody UserDto.RegisterDto registerDto) {
        System.out.println("회원 가입 요청");
        try {
            return this.userService.register(registerDto);
        } catch (Exception e) {
            System.out.println("회원 가입 실패: " + e.getMessage());
            return null;
        }
    }


    @GetMapping("/checkDupId")
    public boolean CheckDupID(@RequestParam("InputId") String InputId) throws Exception {
        return userService.CheckDupId(InputId);
    }

    @GetMapping("/checkDupNick")
    public boolean CheckDupNickname(@RequestParam("nickname") String NickName) throws Exception {
        return userService.CheckDupNick(NickName);
    }

    @PostMapping("/upload")
    public UserDto.ResDto UploadProfileImg(@RequestParam("image") MultipartFile image, @RequestParam("userid") String userid) {
        UserDto.ResDto resDto = new UserDto.ResDto();
        try {
            String awsurl = this.s3Service.upload(image);
            if(awsurl == null){
                resDto.setResult(false);
                resDto.setMsg("이미지 업로드 실패");
                return resDto;
            }
            boolean result = this.userService.uploadProfileImg(awsurl, userid);
            resDto.setResult(result);
            resDto.setMsg(awsurl);
            return resDto;
        } catch (IOException e) {
            System.out.println("이미지 업로드 중 오류 발생: " + e.getMessage());
            resDto.setResult(false);
            resDto.setMsg("이미지 업로드 중 오류 발생");
            return resDto;
        }
    }


    @PostMapping("/login")
    public UserDto.RealloginResDto Login(@RequestBody UserDto.LoginDto loginDto, HttpServletResponse response) {
        UserDto.RealloginResDto realloginResDto = new UserDto.RealloginResDto();
        try {
            UserDto.LoginResDto result = this.userService.Login(loginDto);
            if (!result.isResult()) {
                realloginResDto.setResult(result.isResult());
                realloginResDto.setMsg(result.getMsg());
                return realloginResDto;
            }
            long now = (new Date().getTime());

            Cookie AccessCookie = new Cookie("accessToken", String.valueOf(result.getAccessToken()));
            AccessCookie.setMaxAge(1800);
            AccessCookie.setHttpOnly(true);
            AccessCookie.setPath("/");
            AccessCookie.setAttribute("SameSite", "Lax");
            response.addCookie(AccessCookie);

            Cookie Refreshcookie = new Cookie("RefreshToken", String.valueOf(result.getRefreshToken()));
            Refreshcookie.setMaxAge(86400 * 7);
            Refreshcookie.setHttpOnly(true);
            Refreshcookie.setPath("/");
            Refreshcookie.setAttribute("SameSite", "Lax");
            response.addCookie(Refreshcookie);

            realloginResDto.setResult(result.isResult());
            realloginResDto.setMsg(result.getMsg());

            response.flushBuffer();

            return realloginResDto;
        } catch (Exception e) {
            // 예외 발생 시 처리 로직
            System.out.println("로그인 중 오류 발생: " + e.getMessage());
            realloginResDto.setResult(false);
            realloginResDto.setMsg("로그인 중 오류 발생");
            return realloginResDto;
        }
    }


    @GetMapping("/logout")
    public UserDto.ResDto Logout(HttpServletResponse response, @CookieValue(name = "accessToken", required = false) String token, @CookieValue(name = "accessToken", required = false) String Reftoken) {
        UserDto.ResDto resDto = new UserDto.ResDto();
        try {
            System.out.println("로그아웃 토큰" + token);
            if (token == null && Reftoken == null) {
                resDto.setResult(false);
                resDto.setMsg("로그인 상태가 아닙니다.");
            } else {
                boolean result = this.userService.Logout(token);

                Cookie AccessCookie = new Cookie("accessToken", null);
                AccessCookie.setMaxAge(0);
                AccessCookie.setPath("/");
                AccessCookie.setHttpOnly(true);
                response.addHeader("Access-Control-Allow-Credentials", "true");
                response.addHeader("Access-Control-Allow-Origin", "*");
                response.addCookie(AccessCookie);

                Cookie Refreshcookie = new Cookie("RefreshToken", null);
                Refreshcookie.setMaxAge(0);
                Refreshcookie.setPath("/");
                Refreshcookie.setHttpOnly(true);
                response.addCookie(Refreshcookie);

                resDto.setResult(result);
            }
        } catch (Exception e) {
            resDto.setResult(false);
            resDto.setMsg("로그아웃 처리 중 오류가 발생했습니다.");
        }
        return resDto;
    }


    @GetMapping("/authuser")
    public UserDto.AuthuserDto authUser(HttpServletResponse response, @CookieValue(name = "accessToken", required = false) String accessToken, @CookieValue(name = "RefreshToken", required = false) String RefreshToken) {
        UserDto.AuthuserDto authuserDto = new UserDto.AuthuserDto();
        try {
            System.out.println("auth user 리프레시 토큰 검증" + RefreshToken);
            if (RefreshToken == null) {
                System.out.println("토큰 null 일 경우" + RefreshToken);
                authuserDto.setResult(false);
                authuserDto.setNickname("로그인 상태가 아닙니다");
                return authuserDto;
            }

            UserDto.AuthuserDto authuser = this.userService.authuser(accessToken, RefreshToken);

            System.out.println("토큰 재생성 여부 확인" + authuser.getNewToken() + authuser.getUserId());

            if(authuser.getNewToken() != null){
                System.out.println("액세스 토큰, 쿠키 재생성" + authuser.getNewToken());
                Cookie AccessCookie = new Cookie("accessToken", String.valueOf(authuser.getNewToken()));
                AccessCookie.setMaxAge(1800);
                AccessCookie.setHttpOnly(true);
                AccessCookie.setPath("/");
                response.addHeader("Access-Control-Allow-Credentials", "true");
                response.addHeader("Access-Control-Allow-Origin", "*");
                AccessCookie.setAttribute("SameSite", "Lax");
                response.addCookie(AccessCookie);

                response.flushBuffer();

                authuserDto.setResult(true);
                authuserDto.setNickname(authuser.getNickname());
                authuserDto.setUserId(authuser.getUserId());

                System.out.println("컨트롤러에서 쿠키 확인" + accessToken + " refresh  " + RefreshToken);
                System.out.println("유저 ID" + authuser.getUserId());

                return authuserDto;
            }

            authuserDto.setResult(true);
            authuserDto.setNickname(authuser.getNickname());
            authuserDto.setUserId(authuser.getUserId());

            System.out.println("컨트롤러에서 쿠키 확인" + accessToken + " refresh  " + RefreshToken);

            return authuserDto;


        } catch (Exception e) {
            authuserDto.setResult(false);
            authuserDto.setNickname("인증 처리 중 오류가 발생했습니다." + e);
            return authuserDto;
        }
    }


    @GetMapping("/info")
    public UserDto.GetUserDto getUserInfo(@CookieValue(name = "accessToken", required = false) String accessToken, @CookieValue(name = "RefreshToken", required = false) String RefreshToken) {
        UserDto.GetUserDto getUserDto = new UserDto.GetUserDto();
        try {
            UserDto.AuthuserDto authuser = this.userService.authuser(accessToken, RefreshToken);

            User user = this.userService.getUserDto(authuser.getUserId());

            getUserDto.setResult(true);
            getUserDto.setUserId(user.getUserId());
            getUserDto.setEmail(user.getEmail());
            getUserDto.setNickname(user.getNickname());
            getUserDto.setProfileImg(user.getProfileImg());
            getUserDto.setRoomId(user.getRoomId());

            return getUserDto;
        } catch (Exception e) {
            getUserDto.setResult(false);
            getUserDto.setNickname("정보 조회 중 오류가 발생했습니다." + e);
            return getUserDto;
        }
    }

    @PatchMapping("/changePW")
    public UserDto.ResDto changePW(@RequestBody UserDto.UpdateInfoDto updateInfoDto){
        // 유저 정보 수정
        return this.userService.changePW(updateInfoDto.getUserid(), updateInfoDto.getInputpw(), updateInfoDto.getEmail());
    }

    @PatchMapping("/changeEmail")
    public UserDto.ResDto changeEmail(@RequestBody UserDto.UpdateInfoDto updateInfoDto){
        // 유저 정보 수정
        return this.userService.changeEmail(updateInfoDto.getUserid(), updateInfoDto.getInputpw(), updateInfoDto.getEmail());
    }

    @DeleteMapping("/withdraw")
    public UserDto.ResDto withdraw(HttpServletResponse response,@RequestBody UserDto.WithdrawRequest withdrawRequest){
        UserDto.ResDto resDto = userService.withdraw(withdrawRequest.getUserId());

        Cookie AccessCookie = new Cookie("accessToken", null);
        AccessCookie.setMaxAge(0);
        AccessCookie.setPath("/");
        AccessCookie.setHttpOnly(true);
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addCookie(AccessCookie);

        Cookie Refreshcookie = new Cookie("RefreshToken", null);
        Refreshcookie.setMaxAge(0);
        Refreshcookie.setPath("/");
        Refreshcookie.setHttpOnly(true);
        response.addCookie(Refreshcookie);

        return resDto;
    }


}
