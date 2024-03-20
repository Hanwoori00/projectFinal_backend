package com.example.projectFinal.controller;


import com.example.projectFinal.dto.ChatDto;
import com.example.projectFinal.service.ChatService;
import com.example.projectFinal.service.Pooh;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/getAnswer")
    @ResponseBody
    public String GetAnswer(ChatDto chatDto) {
        return chatService.getAnswer(chatDto);
    }
}
