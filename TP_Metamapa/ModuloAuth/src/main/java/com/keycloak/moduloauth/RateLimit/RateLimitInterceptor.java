package com.keycloak.moduloauth.RateLimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimitService rateLimiterService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        System.out.println(">>> [Interceptor] Nueva petición entrante!");
        System.out.println(">>> URI: " + request.getRequestURI());
        System.out.println(">>> Método: " + request.getMethod());


        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }

        String uri = request.getRequestURI();
        String bucketKey;
        long limit;


        //  Login y Creación de Usuario
        if (uri.contains("/iniciar-sesion") || uri.contains("/create")) {
            // Protección contra fuerza bruta y creación masiva de cuentas basura
            bucketKey = ip + "_AUTH_CRITICAL";
            limit = 5; // 10 intentos por minuto
        }
        // Buscar usuarios o ver roles
        else if (uri.contains("/search") || uri.contains("/role")) {
            bucketKey = ip + "_AUTH_READ";
            limit = 50;
        }
        else {
            bucketKey = ip + "_AUTH_DEFAULT";
            limit = 30;
        }

        // 3. CONSUMO DE TOKENS
        Bucket tokenBucket = rateLimiterService.resolveBucket(bucketKey, limit);
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            System.out.println(">>> Petición ACEPTADA. Tokens restantes: " + probe.getRemainingTokens());
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            System.out.println(">>> Petición BLOQUEADA (429)");
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String body = "{"
                    + "\"error\":\"BLOQUEO_RATELIMIT\","
                    + "\"message\":\"Demasiados intentos de autenticación\","
                    + "\"retry_after_seconds\":" + waitForRefill
                    + "}";

            response.getWriter().write(body);
            response.flushBuffer();

            return false;
        }
    }
}