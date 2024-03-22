package com.example.projectFinal.controller;

import com.example.projectFinal.dto.ChatDto;
import com.example.projectFinal.dto.UserDto;
import com.example.projectFinal.entity.User;
import com.example.projectFinal.service.ChatService;
import com.example.projectFinal.service.S3Service;
import com.example.projectFinal.service.TTSService;
import com.example.projectFinal.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kong.unirest.HttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {
    //    유저 컨트롤러
    private final UserService userService;

    private final TTSService TTSservice;
    private final S3Service s3Service;

    private final ChatService chatService;

    public UserController(UserService userService, TTSService ttSservice, S3Service s3Service, ChatService chatService) {
        this.userService = userService;
        TTSservice = ttSservice;
        this.s3Service = s3Service;
        this.chatService = chatService;
    }
    @PostMapping("/testTTs")
    public void TTStest(@RequestBody String text) throws UnsupportedAudioFileException, IOException {
        TTSservice.callExternalApi(text);
    }

    @PostMapping("/register")
    public User register(@RequestBody UserDto.RegisterDto registerDto) throws Exception{
        return userService.SignUp(registerDto).getBody();
    }

    @GetMapping("/checkDupId")
    public boolean CheckDupID(@RequestParam("InputId") String InputId) throws Exception{
        return userService.CheckDupId(InputId);
    }

    @GetMapping("/checkDupNick")
    public boolean CheckDupNickname(@RequestParam("nickname") String NickName) throws Exception{
        return userService.CheckDupNick(NickName);
    }

    @PostMapping("/upload")
    public void UploadProfileImg(@RequestParam("image") MultipartFile image) throws IOException{
        System.out.println(image);
        System.out.println(this.s3Service.upload(image));
    }

    @PostMapping("/login")
    public UserDto.LoginResDto Login(@RequestBody UserDto.LoginDto loginDto, HttpServletResponse response) throws Exception{
        UserDto.LoginResDto result = this.userService.Login(loginDto);
        if(!result.isResult()){
            return result;
        }
        long now = (new Date().getTime());

        Cookie AccessCookie = new Cookie("accessToken", String.valueOf(result.getAccessToken()));
        AccessCookie.setMaxAge(1800);
        AccessCookie.setHttpOnly(true);
        response.addCookie(AccessCookie);

        Cookie Refreshcookie = new Cookie("RefreshToken", String.valueOf(result.getRefreshToken()));
        Refreshcookie.setMaxAge(86400 * 7);
        Refreshcookie.setHttpOnly(true);
        response.addCookie(Refreshcookie);
        return result;
    }

    @GetMapping("/logout")
    public UserDto.ResDto Logout(HttpServletResponse response,@CookieValue(name = "accessToken", required = false) String token){
        UserDto.ResDto resDto = new UserDto.ResDto();
        System.out.println("로그아웃 토큰" + token);
        if(token == null){
            resDto.setResult(false);
            return resDto;
        } else{
            System.out.println("토큰 있음");

            boolean result = this.userService.Logout(token);

            Cookie AccessCookie = new Cookie("accessToken", null);
            AccessCookie.setMaxAge(0);
            AccessCookie.setPath("/user");
            AccessCookie.setHttpOnly(true);
            response.addCookie(AccessCookie);

            Cookie Refreshcookie = new Cookie("RefreshToken", null);
            Refreshcookie.setMaxAge(0);
            AccessCookie.setPath("/user");
            Refreshcookie.setHttpOnly(true);
            response.addCookie(Refreshcookie);

            resDto.setResult(result);

            return resDto;
        }

    }

    @GetMapping("/authuser")
    public UserDto.AuthuserDto authUser(HttpServletResponse response,
                                   @CookieValue(name = "accessToken", required = false) String accessToken,
                                   @CookieValue(name = "RefreshToken", required = false) String RefreshToken){
        UserDto.AuthuserDto authuserDto = new UserDto.AuthuserDto();

        if(RefreshToken == null){
            authuserDto.setResult(false);
            authuserDto.setNickname("로그인 상태가 아닙니다");
            return authuserDto;
        } else{
            System.out.println("컨트롤러에서 토큰 확인" + accessToken +" refresh  "+ RefreshToken);
            UserDto.AuthuserDto result = this.userService.authuser(accessToken, RefreshToken);
            authuserDto.setResult(result.isResult());
            authuserDto.setNickname(result.getNickname());

            Cookie AccessCookie = new Cookie("accessToken", String.valueOf(result.getNewToken()));
            AccessCookie.setMaxAge(1800);
            AccessCookie.setHttpOnly(true);
            response.addCookie(AccessCookie);

            return authuserDto;

        }
    }

    @PostMapping("/info")
    public void getUserInfo(HttpServletResponse response,
                            @CookieValue(name = "accessToken", required = false) String accessToken,
                            @CookieValue(name = "RefreshToken", required = false) String RefreshToken){
        UserDto.AuthuserDto authuser = this.userService.authuser(accessToken, RefreshToken);

        System.out.println("유저 정보 가져오는 Post api 실행 결과" + authuser);
    }

    @PostMapping("/sendChat")
    public UserDto.SendChatDto sendChat(@CookieValue(name = "accessToken", required = false) String accessToken,
                                        @CookieValue(name = "RefreshToken", required = false) String RefreshToken,
                                        @RequestBody ChatDto chatDto){
        UserDto.SendChatDto sendChatDto = new UserDto.SendChatDto();

        try {
            UserDto.AuthuserDto authuser = this.userService.authuser(accessToken, RefreshToken);
            System.out.println("토큰 검증" + authuser.getUserId() + authuser.getNickname());

            chatDto.setMessages(chatDto.getMessages());

            String aimsg = this.chatService.getAnswer(chatDto);

            System.out.println("푸 답변" + aimsg);

            TTSservice.callExternalApi(aimsg);

            sendChatDto.setAimsg(aimsg);
            sendChatDto.setNickname(authuser.getNickname());
            sendChatDto.setResult(true);
            sendChatDto.setUserMsg(chatDto.getUserMsg());

            return sendChatDto;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }


}
