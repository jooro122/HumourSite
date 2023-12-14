package com.project.humour2.controller;

import com.project.humour2.dto.CommentRequestDTO;
import com.project.humour2.dto.CommentResponseDTO;
import com.project.humour2.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/board/{id}/comment")
    public String writeComment(@PathVariable Long id, CommentRequestDTO commentRequestDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/member/login"; // 권한이 없을 경우 홈페이지로 리다이렉트
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        commentService.writeComment(commentRequestDTO, id, userDetails.getUsername());

        return "redirect:/board/" + id;
    }



    @PostMapping("/board/{id}/comment/{commentId}/update")
    @PreAuthorize("@commentSecurity.checkAuthorization(#id, authentication)")//권한 설정(본인, 관리자)
    public String updateComment(@PathVariable Long id, @PathVariable Long commentId, CommentRequestDTO commentRequestDTO) {
        commentService.updateComment(commentRequestDTO, commentId);
        return "redirect:/board/" + id;
    }


    @GetMapping("/board/{id}/comment/{commentId}/remove")
    @PreAuthorize("@commentSecurity.checkAuthorization(#commentId, authentication)")//권한 설정(본인, 관리자)
    public String deleteComment(@PathVariable Long id, @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/board/" + id;
    }
}