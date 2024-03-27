package com.example.projectFinal.controller;

import com.example.projectFinal.dto.MessageDto;
import com.example.projectFinal.repository.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("message")
public class MessageController {
    @Autowired
    MessageRepository messageRepository;

    @GetMapping("/getMessagesByRoomid")
    @ResponseBody
    public String getMessagesByRoomid(@RequestParam String roomid) {
        List<MessageDto> messageDtos = messageRepository.findByRoomid(roomid);
        ObjectMapper objectMapper = new ObjectMapper();
        String messages;
        try {
            messages = objectMapper.writeValueAsString(messageDtos);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // 예외 발생 시 에러 처리
            return "Error occurred while converting rooms to JSON";
        }
        return messages;
        // var msgArray = JSON.parse(messages); 프론트에서 이 작업 필요
    }
    @GetMapping("/wrongMessages")
    @ResponseBody
    public String getWrongMessagesByRoomid(@RequestParam String roomid) {
        List<MessageDto> wrongMessagesDtos = messageRepository.findByRoomidAndGrammarValidIsFalse(roomid);
        String wrongMessages;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            wrongMessages = objectMapper.writeValueAsString(wrongMessagesDtos);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // 예외 발생 시 에러 처리
            return "Error occurred while converting rooms to JSON";
        }
        return wrongMessages;
        // var wrongMsgArray = JSON.parse(wrongMessages)
    }



}
