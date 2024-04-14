package com.example.projectFinal.service;

import com.example.projectFinal.entity.Message;
import com.example.projectFinal.entity.Room;
import com.example.projectFinal.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MessageService {
    @Autowired
    MessageRepository messageRepository;

    public void addMessagesByRoomId(Room room, String[] messages) {
        for (String message : messages) {
            Message temp = new Message();
            temp.setRoomid(room.getId());
            temp.setUserid(room.getUserid());
            temp.setAi(room.getAi());
            String[] content = message.split(": ");

            if (content[0].contains("user")) {//유저면
                temp.setUserSpeaking(true);
                if(content[1].contains("->")) { // 교정받은거
                    temp.setGrammarValid(false);
                    String[] two = content[1].split("->");
                    temp.setContent(two[0]);
                    temp.setCorrectedContent(two[1]);
                } else {//교정 안받은거
                    temp.setGrammarValid(true);
                    temp.setContent(content[1]);
                }
            } else {//ai면
                temp.setUserSpeaking(false);
                temp.setContent(content[1]);
                temp.setGrammarValid(true);
            }
            messageRepository.save(temp);
        }
    };

}
