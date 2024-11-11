package com.sparta.harmony.store.service;

import com.sparta.harmony.store.dto.CategoryRequestDto;
import com.sparta.harmony.store.dto.CategoryResponseDto;
import com.sparta.harmony.store.entity.Category;
import com.sparta.harmony.store.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        Category category = new Category(categoryRequestDto.getCategoryName());

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponseDto(savedCategory.getStoreCategoryId(), savedCategory.getCategoryName()
        );
    }

    @Transactional
    public CategoryResponseDto updateCategory(UUID categoryId, CategoryRequestDto categoryRequestDto) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));


        category.updateCategoryName(categoryRequestDto.getCategoryName());

        categoryRepository.save(category);

        return new CategoryResponseDto(category.getStoreCategoryId(), category.getCategoryName());
    }

    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        categoryRepository.delete(category);
    }


}
