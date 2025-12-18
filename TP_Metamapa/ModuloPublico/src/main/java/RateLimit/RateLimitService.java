package RateLimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key, long limitPerMinute) {
        return cache.computeIfAbsent(key, k -> newBucket(limitPerMinute));
    }

    private Bucket newBucket(long limitPerMinute) {
        Bandwidth limit = Bandwidth.classic(limitPerMinute, Refill.intervally(limitPerMinute, Duration.ofMinutes(1)));

        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
}
