package com.sparta.harmony.common.config;

import com.sparta.harmony.common.logging.filter.ReqResLoggingFilter;
import lombok.Getter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class LoggingFilterConfig {
    @Bean
    public FilterRegistrationBean<ReqResLoggingFilter> loggingFilter() {
        FilterRegistrationBean<ReqResLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ReqResLoggingFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}
