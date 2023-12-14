package com.project.humour2.security;

import com.project.humour2.dto.BoardResponseDTO;
import com.project.humour2.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("boardSecurity")
public class BoardSecurity {

    @Autowired
    private BoardService boardService;

    public boolean checkAuthorization(Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        BoardResponseDTO boardResponseDTO = boardService.boardDetail(id);

        // 로그인한 사용자가 게시물 작성자이거나 ADMIN인 경우에만 true 반환
        return boardResponseDTO.getEmail().equals(userDetails.getUsername()) ||
                authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
    }
}
