package org.chat.supportpilot.Limiter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.chat.supportpilot.Auth;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class RateLimiterInterceptor implements HandlerInterceptor {
    private final RateLimiter rateLimiter;
    public RateLimiterInterceptor(RateLimiter rateLimiter){
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String userId = request.getHeader("userId");
        if(userId == null){
            return true;
        }
        Auth.checkValid(userId);
        if(rateLimiter.allow(userId)){
            return true;
        }
        response.setStatus(429);
        response.setHeader("Retry-After", "60");
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Too many requests\"}");
        return false;
    }
}
