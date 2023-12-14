package com.project.humour2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDTO {

    @NotNull(message = "제목을 입력해 주세요.")
    private String title;

    @NotNull(message = "내용을 입력해 주세요.")
    private String content;

    @NotNull(message = "게시판을 선택해 주세요.")
    private String boardName;

    @NotNull(message = "카테고리를 선택해 주세요.")
    private Long category;

}
