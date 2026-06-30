package com.wms.common.aspect;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpServletRequest;

import com.wms.common.annotation.RateLimit;
import com.wms.common.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RateLimitAspect {

    private static final ConcurrentHashMap<String, RateLimitEntry> CACHE = new ConcurrentHashMap<>();

    private static class RateLimitEntry {
        volatile int count;
        volatile long windowStart;
        RateLimitEntry(long start) {
            this.count = 1;
            this.windowStart = start;
        }
    }

    @Pointcut("@annotation(com.wms.common.annotation.RateLimit)")
    public void rateLimitPointcut() {
    }

    @Around("rateLimitPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        String key = buildKey(rateLimit);

        long now = System.currentTimeMillis();
        long windowMs = rateLimit.windowSeconds() * 1000L;

        synchronized (CACHE) {
            RateLimitEntry entry = CACHE.compute(key, (k, v) -> {
                if (v == null || (now - v.windowStart) > windowMs) {
                    return new RateLimitEntry(now);
                }
                v.count++;
                return v;
            });

            if (entry.count > rateLimit.maxAttempts() && (now - entry.windowStart) <= windowMs) {
                long waitSeconds = (windowMs - (now - entry.windowStart)) / 1000;
                throw new BusinessException("操作过于频繁，请在 " + waitSeconds + " 秒后重试");
            }
        }

        return joinPoint.proceed();
    }

    private String buildKey(RateLimit rateLimit) {
        if (!rateLimit.key().isEmpty()) {
            return "rl:" + rateLimit.key();
        }
        String clientIp = "unknown";
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            clientIp = request.getRemoteAddr();
        }
        return "rl:" + clientIp + ":" + rateLimit.windowSeconds();
    }
}
