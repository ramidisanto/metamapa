package RateLimit;

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

        // 1. IP Real (Soporte Nube)
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }

        String method = request.getMethod(); // GET, POST, PUT, DELETE...
        String bucketKey;
        long limit;

        // 2. REGLAS PARA MODULO AVD
        // Separamos operaciones de Lectura (inofensivas) de las de Escritura (críticas)

        if ("GET".equalsIgnoreCase(method)) {
            // ZONA DE LECTURA (Ver solicitudes, ver colecciones)
            // Límite: 50 peticiones por minuto (ágil para administración)
            bucketKey = ip + "_AVD_READ";
            limit = 50;
        } else {
            // ZONA DE ESCRITURA (Crear, Borrar, Modificar, Aprobar)
            // Métodos: POST, PUT, DELETE, PATCH
            // Límite: 20 peticiones por minuto (seguridad estricta)
            bucketKey = ip + "_AVD_WRITE";
            limit = 20;
        }

        // 3. Consumo
        Bucket tokenBucket = rateLimiterService.resolveBucket(bucketKey, limit);
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Has excedido el límite de operaciones administrativas.");
            return false;
        }
    }
}