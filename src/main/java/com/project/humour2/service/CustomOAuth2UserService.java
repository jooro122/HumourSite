package com.project.humour2.service;

import com.project.humour2.dto.MemberRole;
import com.project.humour2.dto.PrincipalDetail;
import com.project.humour2.entity.Member;
import com.project.humour2.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {


    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    private final MemberRepository memberRepository;


    public String getNaverAuthorizationUri() {
        String naverAuthorizationUri = "https://nid.naver.com/oauth2.0/authorize";

        String state = UUID.randomUUID().toString();

        return UriComponentsBuilder.fromUriString(naverAuthorizationUri)
                .queryParam("response_type", "code")
                .queryParam("client_id", naverClientId)
                .queryParam("redirect_uri", naverRedirectUri)
                .queryParam("state", state)
                .build().toUriString();
    }


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        //카카오, 구글 추가하려면 여기에 분기점 생성
        Member member = processNaverOAuth2User(oAuth2User.getAttributes());

        return new PrincipalDetail(member, oAuth2User.getAttributes());
    }


    private Member processNaverOAuth2User(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");


        String email = (String) response.get("email");
        String userName = (String) response.get("name");
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            // 네이버로 처음 로그인하는 사용자라면 회원 가입 처리
            member = Member.builder()
                    .email(email)
                    .userName(userName)
                    .password("")
                    .address("")
                    .role(MemberRole.ROLE_USER)
                    .build();
            memberRepository.save(member);
        } else {
            // 이미 가입된 사용자라면 정보 업데이트
            member.updateUserName((String) response.get("name"));
            // 추가적인 업데이트가 필요한 경우 여기에 추가하세요.
            memberRepository.save(member);
        }
        return member;
    }



}
