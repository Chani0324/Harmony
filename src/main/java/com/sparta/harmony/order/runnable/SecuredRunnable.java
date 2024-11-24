package com.sparta.harmony.order.runnable;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecuredRunnable implements Runnable {
    private final Runnable task;
    private final SecurityContext securityContext;

    public SecuredRunnable(Runnable task) {
        this.task = task;
        this.securityContext = SecurityContextHolder.getContext();
    }

    @Override
    public void run() {
        try {
            SecurityContextHolder.setContext(securityContext);
            task.run();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
