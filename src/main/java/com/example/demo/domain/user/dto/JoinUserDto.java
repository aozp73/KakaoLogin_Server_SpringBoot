package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.User;

import lombok.Data;

@Data
public class JoinUserDto {

    private String username;

    public User toEntity() {
        return User.builder()
                .username(username)
                .role("user")
                .build();
    }
}
