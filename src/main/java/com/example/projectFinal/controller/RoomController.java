package com.example.projectFinal.controller;
import com.example.projectFinal.dto.ChatDto;
import com.example.projectFinal.dto.UserDto;
import com.example.projectFinal.entity.Room;
import com.example.projectFinal.service.MessageService;
import com.example.projectFinal.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.projectFinal.dto.RoomDto;
import com.example.projectFinal.service.RoomService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/room")
public class RoomController {
    @Autowired
    RoomService roomService;
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;

    @GetMapping("/getRooms")
    @ResponseBody
    public String getRooms(@CookieValue(name = "accessToken", required = false) String accessToken, @CookieValue(name = "RefreshToken", required = false) String RefreshToken) {
//    public String getRooms(@RequestParam String userid) {
        UserDto.AuthuserDto authuser = this.userService.authuser(accessToken, RefreshToken);
        List<RoomDto> roomDtos = roomService.findAllByUserid(authuser.getUserId());
//        List<RoomDto> roomDtos = roomService.findAllByUserid(userid);
        // ObjectMapper를 사용하여 RoomDto 리스트를 JSON 문자열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String rooms;
        try {
            rooms = objectMapper.writeValueAsString(roomDtos);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // 예외 발생 시 에러 처리
            return "Error occurred while converting rooms to JSON";
        }
        return rooms;
        // var roomsArray = JSON.parse(rooms); 프론트에서 이 작업 필요
    }

    @PostMapping("/newRoom")
    @ResponseBody
    public String newRoom(@RequestBody Room room, @CookieValue(name = "accessToken", required = false) String accessToken, @CookieValue(name = "RefreshToken", required = false) String RefreshToken) {
        UserDto.AuthuserDto authuser = this.userService.authuser(accessToken, RefreshToken);
        room.setUserid(authuser.getUserId());
        Room newRoom = roomService.newRoom(room);
        messageService.addMessagesByRoomId(room, room.getMessages());
        return newRoom.getId();
    }
}


//
//
//
//@Controller
//public class UserController {
//
//    @Autowired
//    UserService userService;
//
//    @GetMapping("/")
//    public String getUsers(Model model) {
//        List<UserDTO> users = userService.getUserList();
//        model.addAttribute("list", users);
//        return "user";
//    }
//
////    @GetMapping("/searchUserByName")
////    public String searchUsers(Model model) {
////
////    }
//
//    @PostMapping("/insert")
//    @ResponseBody
//    public String insertUser(@RequestBody UserEntity user) {
//        String newName = userService.insertUser(user);
//        return newName + "Success";
//    }
//}