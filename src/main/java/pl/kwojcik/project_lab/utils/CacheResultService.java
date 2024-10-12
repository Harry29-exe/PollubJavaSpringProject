package pl.kwojcik.project_lab.utils;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

record CachedResult<T>(Instant validUntil, T value) {};

@Service
public class CacheResultService<A, T> {
    private final Duration CACHE_TTL = Duration.ofSeconds(30);
    private final Map<A, CachedResult<T>> cachedResults = new HashMap<>();

    //start L4 functional interface
    public interface FuncWithCacheableResult<T, A> {
        T apply(A a);
    }

    public T executeOrGetCached(A args, FuncWithCacheableResult<T, A> funcWithCacheableResult) {
        var cachedResult = this.cachedResults.get(args);
        if (cachedResult != null && cachedResult.validUntil().isAfter(Instant.now())) {
            return cachedResult.value();
        }

        var result = funcWithCacheableResult.apply(args);
        this.cachedResults.put(args, new CachedResult<>(Instant.now().plus(CACHE_TTL), result));
        return result;
    }
}
