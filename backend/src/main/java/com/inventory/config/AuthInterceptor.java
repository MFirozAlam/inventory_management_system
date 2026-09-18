package com.inventory.config;

import com.inventory.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final AuthService auth;
    public AuthInterceptor(AuthService auth) { this.auth = auth; }
    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) return true;
        if (request.getRequestURI().startsWith("/api/auth/")) return true;
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ") && auth.userForToken(header.substring(7)).isPresent()) return true;
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
