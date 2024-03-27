package com.example.projectFinal.repository;


import com.example.projectFinal.dto.MessageDto;
import com.example.projectFinal.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    List<MessageDto> findByRoomid(String roomid);
    List<MessageDto> findByRoomidAndGrammarValidIsFalse(String roomid);



}
