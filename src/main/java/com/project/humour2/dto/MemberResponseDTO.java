package com.project.humour2.dto;

import com.project.humour2.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberResponseDTO {

    private String email;
    private String userName;
    private String address;
    private String password;

    @Builder
    public MemberResponseDTO(Member member) {
        this.email = member.getEmail();
        this.userName = member.getUserName();
        this.address = member.getAddress();
        this.password = member.getPassword();
    }


}
