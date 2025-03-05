package com.redbeans.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("RedBeans 백엔드 API가 실행 중입니다!");
    }

    @GetMapping("/api")
    public ResponseEntity<String> apiRoot() {
        return ResponseEntity.ok("RedBeans API is running");
    }

    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build();
    }
}