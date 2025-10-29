package com.example.SpringBootWeb.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateQuizDto {
    @NotNull
    @Size(min = 5, max = 255)
    private String title;

    @NotNull  // ← Bắt buộc cả description
    private String description;

    @NotNull
    @Min(1)
    @Max(3600)
    private Integer duration;

    @Size(max = 500)
    private String thumbnailUrl;

    @NotNull  // ← Bắt buộc cả isActive
    private Boolean isActive;
}
