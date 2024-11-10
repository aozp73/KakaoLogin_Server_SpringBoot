package com.example.demo.service;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.common.Const;
import com.example.demo.transformer.Trans;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoService {

	private final HttpSession httpSession;

	@Autowired
	public HttpCallService httpCallService;

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

	public String loginCallback(String code) {
		String param = "grant_type=authorization_code&client_id=" + REST_API_KEY + "&redirect_uri=" + REDIRECT_URI
				+ "&client_secret=" + CLIENT_SECRET + "&code=" + code;
		String rtn = httpCallService.Call(Const.POST, TOKEN_URI, Const.EMPTY, param);
		httpSession.setAttribute("token", Trans.token(rtn, new JsonParser()));

		return "ok";
	}

	public String getProfile() {
		String uri = KAKAO_API_HOST + "/v2/user/me";
		return httpCallService.CallwithToken(Const.GET, uri, httpSession.getAttribute("token").toString());
	}

	public String getFriends() {
		String uri = KAKAO_API_HOST + "/v1/api/talk/friends";
		return httpCallService.CallwithToken(Const.GET, uri, httpSession.getAttribute("token").toString());
	}

	public String message() {
		String uri = KAKAO_API_HOST + "/v2/api/talk/memo/default/send";
		return httpCallService.CallwithToken(Const.POST, uri, httpSession.getAttribute("token").toString(),
				Trans.default_msg_param);
	}

	public String friendMessage(String uuids) {
		String uri = KAKAO_API_HOST + "/v1/api/talk/friends/message/default/send";
		return httpCallService.CallwithToken(Const.POST, uri, httpSession.getAttribute("token").toString(),
				Trans.default_msg_param + "&receiver_uuids=[" + uuids + "]");
	}

	public String logout() {
		String uri = KAKAO_API_HOST + "/v1/user/logout";
		return httpCallService.CallwithToken(Const.POST, uri, httpSession.getAttribute("token").toString());
	}
}
