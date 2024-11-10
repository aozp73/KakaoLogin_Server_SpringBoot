package com.example.demo.domain.kakao;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.common.Const;
import com.example.demo.domain.user.UserRepository;
import com.example.demo.service.HttpCallService;
import com.example.demo.transformer.Trans;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoService {

	private final HttpSession httpSession;

	private static final Logger LOGGER = LoggerFactory.getLogger(KakaoContoller.class);

	@Autowired
	public HttpCallService httpCallService;
	@Autowired
	public UserRepository userRepository;

	@Value("${rest-api-key}")
	private String REST_API_KEY;

	@Value("${redirect-uri}")
	private String REDIRECT_URI;

	@Value("${authorize-uri}")
	private String AUTHORIZE_URI;

	@Value("${token-uri}")
	public String TOKEN_URI;

	@Value("${client-secret}")
	private String CLIENT_SECRET;

	@Value("${kakao-api-host}")
	private String KAKAO_API_HOST;

	public String goKakaoOAuth(String scope) {
		String uri = AUTHORIZE_URI + "?redirect_uri=" + REDIRECT_URI + "&response_type=code&client_id=" + REST_API_KEY;
		if (!scope.isEmpty())
			uri += "&scope=" + scope;

		return uri;
	}

	@Transactional(readOnly = true)
	public Map<String, String> loginProcessWtihCode(String code) {

		Map<String, String> responseMap = new HashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();

		// 1. 인가 코드 -> 엑세스 토큰
		getAccessToken(code);
		try {
			// 2. 엑세스 토큰 -> 사용자 정보
			String userProfile = getProfile();

			// 3-1. 파싱
			JsonNode rootNode = objectMapper.readTree(userProfile);
			JsonNode kakaoAccountNode = rootNode.path("kakao_account");
			JsonNode profileNode = kakaoAccountNode.path("profile");
			String userName = profileNode.path("nickname").asText();

			// 3-2. 회원등록여부 확인 (인증서 방식에선 '인증결과 CI값 / DB CI값' 비교)
			Boolean joinYn = findJoinInfo(userName);
			LOGGER.info("userName: {}", userName);
			LOGGER.info("YN: {}", joinYn);

			// 회원등록여부에 따른 처리
			if (joinYn) {
				// 화면: 유저 정보 / SpringSecurity: 로그인 처리
				responseMap.put("profile", userProfile);
			} else if (!joinYn) {
				responseMap.put("profile", null);
			}
		} catch (Exception e) {
		}

		return responseMap;
	}

	public Boolean findJoinInfo(String userName) {
		return userRepository.existsByUsername(userName);
	}

	public void getAccessToken(String code) {
		String param = "grant_type=authorization_code&client_id=" + REST_API_KEY + "&redirect_uri=" + REDIRECT_URI
				+ "&client_secret=" + CLIENT_SECRET + "&code=" + code;
		String rtn = httpCallService.Call(Const.POST, TOKEN_URI, Const.EMPTY, param);
		httpSession.setAttribute("token", Trans.token(rtn, new JsonParser()));
	}

	public String getProfile() {
		String uri = KAKAO_API_HOST + "/v2/user/me";
		return httpCallService.CallwithToken(Const.GET, uri, httpSession.getAttribute("token").toString());
	}

	// public String getFriends() {
	// String uri = KAKAO_API_HOST + "/v1/api/talk/friends";
	// return httpCallService.CallwithToken(Const.GET, uri,
	// httpSession.getAttribute("token").toString());
	// }

	// public String message() {
	// String uri = KAKAO_API_HOST + "/v2/api/talk/memo/default/send";
	// return httpCallService.CallwithToken(Const.POST, uri,
	// httpSession.getAttribute("token").toString(),
	// Trans.default_msg_param);
	// }

	// public String friendMessage(String uuids) {
	// String uri = KAKAO_API_HOST + "/v1/api/talk/friends/message/default/send";
	// return httpCallService.CallwithToken(Const.POST, uri,
	// httpSession.getAttribute("token").toString(),
	// Trans.default_msg_param + "&receiver_uuids=[" + uuids + "]");
	// }

	// public String logout() {
	// String uri = KAKAO_API_HOST + "/v1/user/logout";
	// return httpCallService.CallwithToken(Const.POST, uri,
	// httpSession.getAttribute("token").toString());
	// }
}
