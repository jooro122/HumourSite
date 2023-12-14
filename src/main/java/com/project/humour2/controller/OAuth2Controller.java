package com.project.humour2.controller;

import com.project.humour2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class OAuth2Controller {

    private final CustomOAuth2UserService customOAuth2UserService;

    @GetMapping("/login/naver")
    public String naverLogin() {
        // 네이버 로그인을 시작할 페이지로 이동
//        return "redirect:" + customOAuth2UserService.getNaverAuthorizationUri();
        return "redirect:/oauth2/authorization/naver";
    }

}