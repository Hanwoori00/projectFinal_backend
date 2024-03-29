package com.example.projectFinal.controller;


import com.example.projectFinal.dto.ChatDto;
import com.example.projectFinal.dto.UserDto;
import com.example.projectFinal.service.ChatService;
import com.example.projectFinal.service.TTSService;
import com.example.projectFinal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;

    private final TTSService TTSservice;


    public ChatController(ChatService chatService, UserService userService, TTSService ttSservice) {
        this.chatService = chatService;
        this.userService = userService;
        TTSservice = ttSservice;
    }

    @PostMapping("/checkMission")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> CheckMission(@RequestBody ChatDto chatDto) {
        Map<String, Object> responseBody = chatService.missionCheck(chatDto);
        return ResponseEntity.ok(responseBody);
    }
    @PostMapping("/createConnection")
    @ResponseBody
    public String CreateConnection() throws IOException {
        chatService.createConnection();
        return "Connection created successfully";
    }
    @PostMapping("/getCorrection")
    @ResponseBody
    public String[] GecCorrection(@RequestBody ChatDto chatDto) {
        return chatService.getCorrection(chatDto);
    }
    @GetMapping("/testPage")
    public String RenderPage() {
        return "test";
    }

    @ResponseBody
    @PostMapping("/SendChat")
    public UserDto.SendChatDto sendChat(@CookieValue(name = "accessToken", required = false) String accessToken,
                                        @CookieValue(name = "RefreshToken", required = false) String RefreshToken,
                                        @RequestBody ChatDto chatDto){
        UserDto.SendChatDto sendChatDto = new UserDto.SendChatDto();

        try {
            this.chatService.createConnection();

            System.out.println("sendChat 토큰 확인 " + accessToken + RefreshToken);

            UserDto.AuthuserDto authuser = this.userService.authuser(accessToken, RefreshToken);

            chatDto.setMessages(chatDto.getMessages());

            ChatDto getAnswerDto = this.chatService.getAnswer(chatDto);

            System.out.println("푸 답변" + getAnswerDto.getAiMsg());

            TTSservice.callExternalApi(getAnswerDto.getAiMsg());

            sendChatDto.setAimsg(getAnswerDto.getAiMsg());
            sendChatDto.setResult(true);
            sendChatDto.setUserMsg(Arrays.toString(chatDto.getMessages()));
            sendChatDto.setEmotion(getAnswerDto.getEmotion());

            if(!authuser.isResult()){
                return sendChatDto;
            } else{

                sendChatDto.setNickname(authuser.getNickname());
                return sendChatDto;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }
}
