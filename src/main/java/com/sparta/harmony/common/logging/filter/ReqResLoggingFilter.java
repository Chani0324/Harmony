package com.sparta.harmony.common.logging.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class ReqResLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ReqResLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // request 추적을 위한 id 생성
        MDC.put("traceId", UUID.randomUUID().toString());

        // 래퍼로 요청, 응답 감싸기.
        final ContentCachingRequestWrapper cachingRequestWrapper = new ContentCachingRequestWrapper(request);
        final ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);

        // request 정보 로깅.
        logger.info("Request Method: {}", cachingRequestWrapper.getMethod());
        logger.info("Request URL: {}", cachingRequestWrapper.getRequestURL());

        // request 헤더 로깅.
        StringBuilder headers = new StringBuilder();
        cachingRequestWrapper.getHeaderNames().asIterator().forEachRemaining(headerName ->
                headers.append(headerName).append(": ").append(cachingRequestWrapper.getHeader(headerName)).append("\n"));
        logger.info("Request Headers:\n{}", headers);

        // 다음 필터 또는 서블릿 호출.
        filterChain.doFilter(cachingRequestWrapper, contentCachingResponseWrapper);

        // requestbody에 password가 들어있을시 로깅x
        String requestBody = new String(cachingRequestWrapper.getContentAsByteArray(), cachingRequestWrapper.getCharacterEncoding());
        if (requestBody.contains("password")) {
            logger.info("개인정보의 중요 부분이 포함되어 로깅이 제한됩니다.");
        } else {
            logger.info("Request Body: {}", requestBody);
        }

        // response 후 로깅 및 보기 좋게 다듬기.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // 줄바꿈 및 들여쓰기 활성화

        String responseBody = new String(contentCachingResponseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

        if (responseBody.isEmpty()) {
            logger.info("Request body is empty");
            MDC.clear();

            return;
        }

        Object json = objectMapper.readValue(responseBody, Object.class); // JSON 문자열을 객체로 변환
        String prettyResponseBody = objectMapper.writeValueAsString(json); // 다시 포맷된 JSON 문자열로 변환

        logger.info("Response Status: {}", contentCachingResponseWrapper.getStatus());
        logger.info("Response Content: {}", prettyResponseBody);

        contentCachingResponseWrapper.copyBodyToResponse();

        MDC.clear();
    }
}
