package com.sparta.harmony.store.controller;

import com.sparta.harmony.store.dto.CategoryRequestDto;
import com.sparta.harmony.store.dto.CategoryResponseDto;
import com.sparta.harmony.store.entity.Category;
import com.sparta.harmony.store.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryResponseDto createProduct(@RequestBody CategoryRequestDto requestDto) {
        return categoryService.createCategory(requestDto);
    }

    @PutMapping("/{categoryId}")
    public CategoryResponseDto updateCategory(@PathVariable UUID categoryId, @RequestBody CategoryRequestDto requestDto) {
        return categoryService.updateCategory(categoryId, requestDto);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(@PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);
    }
}
