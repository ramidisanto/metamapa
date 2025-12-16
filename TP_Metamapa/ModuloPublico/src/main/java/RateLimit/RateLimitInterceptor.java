package RateLimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;



@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimitService rateLimiterService;
    // 1. Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else {
            // A veces viene una lista "IP_Cliente, IP_Proxy1, IP_Proxy2", nos quedamos con la primera
            ip = ip.split(",")[0].trim();
        }

        String uri = request.getRequestURI();
        String bucketKey;
        long limit;


        if (uri.matches(".*/hechos/\\d+")) {
            bucketKey = ip + "_DETAIL";
            limit = 20;
        }
        else if (uri.contains("/buscar") || uri.endsWith("/hechos")) {
            bucketKey = ip + "_SEARCH";
            limit = 30;
        }
        else if (uri.contains("/paises") || uri.contains("/provincias") || uri.contains("/localidades") || uri.contains("/categorias") || uri.contains("/colecciones")) {
            bucketKey = ip + "_AUX";
            limit = 60;
        }
        else {
            bucketKey = ip + "_DEFAULT";
            limit = 20;
        }

        // --------------------------------------------

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
            response.flushBuffer(); // CORTA EL RESTO DEL CICLO, Spring no puede meter 401

            return false;
        }
    }
}