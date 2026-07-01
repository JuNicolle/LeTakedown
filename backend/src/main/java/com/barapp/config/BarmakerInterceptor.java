package com.barapp.config;

import com.barapp.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class BarmakerInterceptor implements HandlerInterceptor {

    private final TokenStore tokenStore;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String method = req.getMethod();
        String path = req.getRequestURI();

        if ("GET".equals(method) && (
                path.startsWith("/api/cocktails") ||
                path.startsWith("/api/categories") ||
                path.startsWith("/api/ingredients"))) {
            return true;
        }

        if (path.contains("/panier/") ||
                path.matches("/api/commandes/client/.*") ||
                ("GET".equals(method) && path.matches("/api/commandes/\\d+"))) {
            return true;
        }

        String token = req.getHeader("X-Auth-Token");
        if (token == null) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token manquant");
            return false;
        }
        var entry = tokenStore.find(token);
        if (entry.isEmpty() || entry.get().role() != Role.BARMAKER) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
            return false;
        }
        return true;
    }
}
