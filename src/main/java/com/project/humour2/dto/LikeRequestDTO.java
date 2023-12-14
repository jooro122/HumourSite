package com.project.humour2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeRequestDTO {
    private Long boardId;
    private Long memberId;
}