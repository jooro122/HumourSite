package com.project.humour2.entity;

import com.project.humour2.dto.MemberDTO;
import com.project.humour2.dto.MemberRole;
import javax.persistence.*;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "member")
@Entity
@Builder
public class Member extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Board> board = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();

    @Builder
    public Member(Long id,String userName, String email, String password, String address, MemberRole role) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.address = address;
        this.role = role;
    }

    public static Member createMember(MemberDTO memberDTO, PasswordEncoder passwordEncoder) {
        Member member = Member.builder()
                .id(memberDTO.getId())
                .userName(memberDTO.getUserName())
                .email(memberDTO.getEmail())
                .address(memberDTO.getAddress())
                .password(passwordEncoder.encode(memberDTO.getPassword()))  //암호화처리
                .role(MemberRole.ROLE_USER)
                .build();
        return member;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
    }

    public void updateAddress(String address) {
        this.address = address;
    }


    public void updatePassword(String password) {
        this.password = password;
    }


}
