package com.project.humour2.dto;

import com.project.humour2.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponseDTO {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long id;
    private String content;
    private String userName;
    private String email;
    private String imageUrl;

    @Builder
    public CommentResponseDTO(Comment comment) {
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userName = comment.getMember().getUserName();
        this.email = comment.getMember().getEmail();
//        this.imageUrl = comment.getMember().getImage().getUrl();
    }
}