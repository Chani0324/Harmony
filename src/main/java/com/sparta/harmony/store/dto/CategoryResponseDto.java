package com.sparta.harmony.store.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryResponseDto {
    private UUID storeCategoryId;
    private String categoryName;

    public CategoryResponseDto(UUID storeCategoryId, String categoryName) {
        this.storeCategoryId = storeCategoryId;
        this.categoryName = categoryName;
    }
}
