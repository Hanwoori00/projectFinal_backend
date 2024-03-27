package com.example.projectFinal.repository;

import com.example.projectFinal.dto.ChatDto;
import com.example.projectFinal.entity.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface RoomRepository extends MongoRepository<Room, String> {
    List<Room> findAllByUserid(String userid);
}