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

        return "redirect:/oauth2/authorization/naver";
    }

}