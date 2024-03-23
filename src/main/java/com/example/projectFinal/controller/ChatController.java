package com.example.projectFinal.controller;


import com.example.projectFinal.dto.ChatDto;
import com.example.projectFinal.dto.UserDto;
import com.example.projectFinal.service.ChatService;
import com.example.projectFinal.service.Pooh;
import com.example.projectFinal.service.TTSService;
import com.example.projectFinal.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

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

    @PostMapping("/getAnswer")
    @ResponseBody
    public String GetAnswer(@RequestBody ChatDto chatDto) {
        return chatService.getAnswer(chatDto);
    }
//    @PostMapping("/createConnection")
//    @ResponseBody
//    public String CreateConnection() {
//        chatService.createConnection();
//        return "Connection created successfully";
//    }
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
