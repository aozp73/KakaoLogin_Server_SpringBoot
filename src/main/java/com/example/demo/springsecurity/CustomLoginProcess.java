package com.example.demo.springsecurity;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.demo.domain.kakao.KakaoContoller;
import com.example.demo.domain.kakao.KakaoService;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomLoginProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(KakaoContoller.class);

    private final KakaoService kakaoService;
    private final UserRepository userRepository;

    public Map<String, String> loginProcessWithCode(String code) {
        Map<String, String> responseMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String userProfile = "";

        // 1. 카카오 API를 통해 액세스 토큰을 얻음
        kakaoService.getAccessToken(code);

        try {
            // 2. 사용자 프로필 정보 가져오기
            userProfile = kakaoService.getProfile();

            JsonNode rootNode = objectMapper.readTree(userProfile);
            JsonNode kakaoAccountNode = rootNode.path("kakao_account");
            JsonNode profileNode = kakaoAccountNode.path("profile");
            String userName = profileNode.path("nickname").asText();

            LOGGER.info("userName: {}", userName);

            // 3. DB에서 해당 사용자가 존재하는지 확인
            Boolean joinYn = kakaoService.findJoinInfo(userName);
            LOGGER.info("User exists: {}", joinYn);

            // 4. 사용자 존재시 로그인 처리
            if (joinYn) {
                // 4-1. 사용자 정보를 기반으로 UserDetails 객체 생성
                UserDetails userDetails = loadUserByUsername(userName);

                // 4-2. 인증 객체 생성 후 SecurityContext에 설정
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                // 4-3. JSESSIONID는 Spring Security가 자동으로 관리하므로 따로 처리하지 않아도 됨

                // 4-4. 사용자 프로필 정보 반환
                responseMap.put("profile", userProfile);
            } else {
                // 회원가입이 되어 있지 않으면 프로필을 null로 반환
                responseMap.put("profile", null);
            }
        } catch (Exception e) {
            LOGGER.error("OAuth login failed", e);
        }

        return responseMap;

    }

    // 사용자 조회 메서드
    private UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);

        return new PrincipalDetails(user);
    }
}
