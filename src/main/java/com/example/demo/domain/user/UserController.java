package com.example.demo.domain.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.kakao.KakaoContoller;
import com.example.demo.domain.user.dto.JoinUserDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(KakaoContoller.class);

    @RequestMapping("/joinUser")
    public ResponseEntity<?> joinUser(@RequestBody JoinUserDto joinUserDto) {

        return ResponseEntity.ok().body(userService.joinUser(joinUserDto));
    }
}
