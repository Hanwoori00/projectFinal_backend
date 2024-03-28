package com.example.projectFinal.controller;

import com.example.projectFinal.dto.ChatDto;

import com.example.projectFinal.service.PracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.Map;


@Controller
@RequestMapping("/practice")
public class PracticeController {
    @Autowired
    PracticeService practiceService;

    @GetMapping("/getPractice")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPractice(@RequestParam String expression) throws IOException {
        Map<String, Object> responseBody = practiceService.getPractice(expression);
        return ResponseEntity.ok(responseBody);
    }
}
