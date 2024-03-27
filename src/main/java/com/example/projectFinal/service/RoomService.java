package com.example.projectFinal.service;

import com.example.projectFinal.dto.RoomDto;
import com.example.projectFinal.entity.Room;
import com.example.projectFinal.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RoomService {
    @Autowired
    RoomRepository roomRepository;

    public List<RoomDto> findAllByUserid(String userid) {
        List<Room> rooms = roomRepository.findAllById(Collections.singleton(userid));
        List<RoomDto> result = new ArrayList<>();
        for (Room room: rooms) {
            RoomDto roomDto = RoomDto.builder()
                    .id(room.getId())
                    .userid(room.getUserid())
                    .ai(room.getAi())
                    .createdAt(room.getCreatedAt())
                    .build();
            result.add(roomDto);
        }
        return result;
    }

    public Room newRoom(Room room) {
        return roomRepository.insert(room);
    }

}
