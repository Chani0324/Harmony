package com.sparta.harmony.common.handler.success;

import com.sparta.harmony.common.dto.ApiPageResponseDto;
import com.sparta.harmony.common.dto.ApiResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class SuccessResponseHandler {

    @ResponseBody
    public <T> ResponseEntity<ApiResponseDto<T>> handleSuccess(HttpStatus status, String message, T dto) {
        ApiResponseDto<T> response = new ApiResponseDto<>(
                status.value(),
                message,
                dto
        );
        return ResponseEntity.status(status).body(response);
    }

    @ResponseBody
    public <T> ResponseEntity<ApiPageResponseDto<T>> handlePageSuccess(HttpStatus status, String message, Page<T> page) {
        List<T> content = page.getContent();

        ApiPageResponseDto<T> response = new ApiPageResponseDto<>(
                status.value(),
                message,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                content
        );
        return ResponseEntity.status(status).body(response);
    }
}
