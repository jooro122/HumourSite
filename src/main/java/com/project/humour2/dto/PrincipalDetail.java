package com.project.humour2.dto;

import com.project.humour2.entity.Member;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//일반 로그인(UserDetails)과 소셜 로그인(OAuth2User) 통합 인증을 위한 클래스
@Data
public class PrincipalDetail implements UserDetails, OAuth2User {
    private Member member;
    private Map<String,Object> attributes;

    //일반 로그인
    public PrincipalDetail(Member member){
        this.member =  member;
    }

    //OAuth 로그인
    public PrincipalDetail(Member member,  Map<String, Object> attributes){
        this.member =  member;
        this.attributes = attributes;
    }
    //OAuth2User의 메서드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    //UserDetails의 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();

        //권한 가져오기
        if (member.getRole() != null) {
            collect.add(new SimpleGrantedAuthority(member.getRole().name()));
        }

        return collect;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}
