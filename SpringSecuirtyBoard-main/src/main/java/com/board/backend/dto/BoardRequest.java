package com.board.backend.dto;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class BoardRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}