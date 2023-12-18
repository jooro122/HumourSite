package com.project.humour2.controller;

import com.project.humour2.dto.BoardResponseDTO;
import com.project.humour2.dto.MemberDTO;
import com.project.humour2.dto.MemberResponseDTO;
import com.project.humour2.dto.PrincipalDetail;
import com.project.humour2.entity.Member;
import com.project.humour2.service.BoardService;
import com.project.humour2.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/member")
@Controller
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final BoardService boardService;


    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("MemberDTO", new MemberDTO());
        return "member/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("MemberDTO") MemberDTO memberDTO, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "member/join";
        }
        try {
            Member member = Member.createMember(memberDTO, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/join";

        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginMember() {
        return "/member/login";
    }

    @GetMapping("/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "/member/login";
    }

    @GetMapping("/mypage")
    public String update(Model model, Authentication authentication){
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/member/login"; // 권한이 없을 경우 홈페이지로 리다이렉트
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDTO memberResponseDTO = memberService.findMember(userDetails.getUsername());
        model.addAttribute("memberResponseDTO", memberResponseDTO);
        return "/member/mypage";
    }

    @PostMapping("/mypage")
    public String updateUsername(@Valid MemberResponseDTO memberResponseDTO, BindingResult bindingResult, String email, Model model) {
        if (bindingResult.hasErrors()) {
            return "member/mypage";
        }
        try {
            memberService.updateMember(email, memberResponseDTO);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/mypage";
        }
        return "redirect:/";
    }

    @GetMapping("/delete")
    public String withdraw(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/member/login"; // 권한이 없을 경우 홈페이지로 리다이렉트
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDTO member = memberService.findMember(userDetails.getUsername());
        model.addAttribute("member", member);
        return "member/delete";
    }

    @PostMapping("/delete")
    public String withdrawConfirm(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        memberService.deleteMember(userDetails.getUsername());
        return "redirect:/member/logout";
    }

    @GetMapping("/article")
    public String myArticles(Model model, Authentication authentication, Principal principal,
                             @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/member/login"; // 권한이 없을 경우 홈페이지로 리다이렉트
        }

        String email = principal.getName();
        List<BoardResponseDTO> myArticles = boardService.boardList(pageable, "email", null, null, null, email).getContent();
        model.addAttribute("myArticles", myArticles);
        return "member/myArticle";
    }

    @GetMapping("/article/like")
    public String likedArticles(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/member/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        List<BoardResponseDTO> likedArticles = boardService.getLikedBoardsByUserEmail(email);
        model.addAttribute("likedArticles", likedArticles);

        return "member/likedArticle";
    }


}
