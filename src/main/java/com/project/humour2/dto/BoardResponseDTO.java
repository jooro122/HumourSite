package com.project.humour2.dto;

import com.project.humour2.entity.Board;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BoardResponseDTO {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String userName;
    private String email;
    private Long views;
    private Long likes;
    private Long comments;
    private String boardName;
    private Long category;
    private String categoryText;

    @Builder
    public BoardResponseDTO(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createdAt = board.getCreatedAt();
        this.userName = board.getMember().getUserName();
        this.email = board.getMember().getEmail();
        this.views = board.getViews();
        this.likes = (long) board.getLikes().size();
        this.comments = (long) board.getComments().size();
        this.boardName = board.getBoardName();
        this.category = board.getCategory();
    }

}