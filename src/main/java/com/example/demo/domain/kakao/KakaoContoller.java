package com.example.demo.domain.kakao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoContoller {

    private static final Logger LOGGER = LoggerFactory.getLogger(KakaoContoller.class);

    @Value("${rest-api-key}")
    private String restApiKey;

    @Autowired
    public KakaoService kakaoService;

    @RequestMapping("/login")
    public String goKakaoOAuth() {
        return kakaoService.goKakaoOAuth("");
    }

    @RequestMapping("/getProfile")
    public ResponseEntity<?> loginCallback(@RequestParam("code") String code) {
        LOGGER.info("코드 값", code);

        return ResponseEntity.ok().body(kakaoService.loginProcessWtihCode(code));
    }

    @RequestMapping("/profile")
    public String getProfile() {
        return kakaoService.getProfile();
    }

    // @RequestMapping("/authorize")
    // public String goKakaoOAuth(@RequestParam("scope") String scope) {
    // return kakaoService.goKakaoOAuth(scope);
    // }

    // @RequestMapping("/friends")
    // public String getFriends() {
    // return kakaoService.getFriends();
    // }

    // @RequestMapping("/message")
    // public String message() {
    // return kakaoService.message();
    // }

    // @RequestMapping("/friends-message")
    // public String friends_message(@RequestParam("uuids") String uuids) {
    // return kakaoService.friendMessage(uuids);
    // }

    // @RequestMapping("/logout")
    // public String logout() {
    // return kakaoService.logout();
    // }
}
