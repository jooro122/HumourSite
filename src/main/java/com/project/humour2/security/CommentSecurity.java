package com.project.humour2.security;

import com.project.humour2.dto.CommentResponseDTO;
import com.project.humour2.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("commentSecurity")
public class CommentSecurity {

    @Autowired
    private CommentService commentService;

    public boolean checkAuthorization(Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        CommentResponseDTO commentResponseDTO = commentService.getCommentById(id);

        // 로그인한 사용자가 댓글 작성자이거나 ADMIN인 경우에만 true 반환
        return commentResponseDTO.getEmail().equals(userDetails.getUsername()) ||
                authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
    }

}
