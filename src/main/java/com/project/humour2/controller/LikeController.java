package com.project.humour2.controller;

import com.project.humour2.dto.LikeRequestDTO;
import com.project.humour2.entity.Member;
import com.project.humour2.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/add")
    public String addLike(@RequestParam Long boardId,
                          @RequestParam Long memberId, Model model) {
        likeService.addLike(boardId, memberId);
        int likesCount = likeService.getLikesCount(boardId);
        model.addAttribute("likesCount", likesCount);

        // 좋아요 수를 포함한 전체 페이지를 리턴
        return "redirect:/board/" + boardId;
    }

    @PostMapping("/remove")
    public String removeLike(@RequestParam Long boardId,
                             @RequestParam Long memberId, Model model) {
        likeService.removeLike(boardId, memberId);
        int likesCount = likeService.getLikesCount(boardId);
        model.addAttribute("likesCount", likesCount);
        return "redirect:/board/" + boardId;
    }

    @GetMapping("/status")
    public ResponseEntity<String> getLikeStatus(@RequestParam Long boardId, @RequestParam Long memberId) {
        boolean isLiked = likeService.isLiked(boardId, memberId);
        return ResponseEntity.ok(isLiked ? "liked" : "notLiked");
    }

    @GetMapping("/checkLogin")
    @ResponseBody
    public boolean checkLoginStatus(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }
}