package com.example.demo.domain.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.domain.kakao.KakaoContoller;
import com.example.demo.domain.user.dto.JoinUserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(KakaoContoller.class);

    public String joinUser(JoinUserDto joinUserDto) {
        String existUserCode = "Y";

        LOGGER.info("joinUser: {}", joinUserDto);
        Boolean existUserYn = userRepository.existsByUsername(joinUserDto.getUsername());
        if (!existUserYn) {
            userRepository.save(joinUserDto.toEntity());
            existUserCode = "N";
        }

        return existUserCode;
    }

}
