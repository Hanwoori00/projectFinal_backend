package com.example.projectFinal.controller;


import com.example.projectFinal.dto.ChatDto;
import com.example.projectFinal.service.ChatService;
import com.example.projectFinal.service.Pooh;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/checkMission")
    @ResponseBody
    public String CheckMission(@RequestBody ChatDto chatDto) { return chatService.missionCheck(chatDto);}
    @PostMapping("/getAnswer")
    @ResponseBody
    public String GetAnswer(@RequestBody ChatDto chatDto) {
        return chatService.getAnswer(chatDto);
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
}
