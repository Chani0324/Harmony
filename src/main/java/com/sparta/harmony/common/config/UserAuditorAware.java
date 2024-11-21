package com.sparta.harmony.common.config;

import com.sparta.harmony.security.UserDetailsImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class UserAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && !authentication.getAuthorities().toString().equals("[ROLE_ANONYMOUS]")) {
            return Optional.of(((UserDetailsImpl) authentication.getPrincipal()).getUser().getEmail());
        }
        return Optional.of("SYSTEM");
    }
}
