package com.project.humour2.service;

import com.project.humour2.dto.MemberDTO;
import com.project.humour2.dto.MemberResponseDTO;
import com.project.humour2.dto.PrincipalDetail;
import com.project.humour2.entity.Member;
import com.project.humour2.repository.MemberRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public Member findByEmail(String email) {

        return memberRepository.findByEmail(email);
    }

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Member findMemberByEmail = memberRepository.findByEmail(member.getEmail());
        if (findMemberByEmail  != null) {
            throw new IllegalStateException("중복된 이메일입니다.");
        }
        Member findMemberByUserName = memberRepository.findByUserName(member.getUserName());
        if (findMemberByUserName != null) {
            throw new IllegalStateException("중복된 닉네임입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new UsernameNotFoundException("해당 이메일이 존재하지 않습니다.");
        }

        return new PrincipalDetail(member);
    }

//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Member member = memberRepository.findByEmail(email);
//
//        if (member == null) {
//            throw new UsernameNotFoundException("해당 이메일이 존재하지 않습니다.");
//        }
//
//        UserDetails userDetails = User.builder()
//                .username(member.getEmail())
//                .password(member.getPassword())
//                .roles(member.getRole().toString())
//                .build();
//
//
////        return userDetails;
//    }

    public MemberResponseDTO findMember(String email) {
        System.out.println("검색할 이메일: " + email);
        Member member = memberRepository.findByEmail(email);
        System.out.println(member + "@@@@@@@");

        if (member == null) {
            throw new UsernameNotFoundException("해당 이메일이 존재하지 않습니다.");
        }

        MemberResponseDTO memberResponseDTO = MemberResponseDTO.builder()
                .member(member)
                .build();

        return memberResponseDTO;
    }

    public Long updateMember(String email,MemberResponseDTO memberResponseDTO) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("해당 이메일이 존재하지 않습니다.");
        }
        //닉네임 중복값 확인
        String userName = memberResponseDTO.getUserName();
        Member checkUserName = memberRepository.findByUserName(userName);
        if (checkUserName != null && !checkUserName.getEmail().equals(email)) {
            // 동일한 닉네임이 다른 이메일에 이미 존재하면 중복
            throw new IllegalStateException("중복된 닉네임입니다.");
        }

        member.updateUserName(memberResponseDTO.getUserName());
        member.updateAddress(memberResponseDTO.getAddress());
        memberRepository.save(member);

        return member.getId();
    }

    public void deleteMember(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("해당 이메일이 존재하지 않습니다.");
        }
        memberRepository.delete(member);
    }

}
