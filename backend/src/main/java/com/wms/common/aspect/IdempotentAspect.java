package com.wms.common.aspect;

import java.lang.reflect.Method;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;

import com.wms.common.annotation.Idempotent;
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
public class IdempotentAspect {

    private static final java.util.concurrent.ConcurrentHashMap<String, Long> IDEMPOTENT_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    @Pointcut("@annotation(com.wms.common.annotation.Idempotent)")
    public void idempotentPointcut() {
    }

    @Around("idempotentPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Idempotent idempotent = method.getAnnotation(Idempotent.class);

        String key;
        if (idempotent.required()) {
            key = idempotent.keyPrefix() + buildRequestKey(idempotent.value());
        } else {
            String requestKey = buildRequestKey(idempotent.value());
            if (requestKey == null || requestKey.isEmpty()) {
                return joinPoint.proceed();
            }
            key = idempotent.keyPrefix() + requestKey;
        }

        long expireMs = idempotent.timeUnit().toMillis(idempotent.expire());
        long now = System.currentTimeMillis();

        Long existing = IDEMPOTENT_CACHE.get(key);
        if (existing != null && (now - existing) < expireMs) {
            throw new BusinessException("请勿重复提交，请稍后再试");
        }

        IDEMPOTENT_CACHE.put(key, now);

        try {
            return joinPoint.proceed();
        } finally {
            cleanupExpired(now, expireMs);
        }
    }

    private String buildRequestKey(String customKey) {
        if (customKey != null && !customKey.isEmpty()) {
            return customKey;
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "";
        }
        HttpServletRequest request = attributes.getRequest();
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String sessionId = request.getSession().getId();
        String fingerprint = request.getHeader("X-Idempotent-Key");
        if (fingerprint == null || fingerprint.isEmpty()) {
            fingerprint = uri + ":" + method + ":" + sessionId;
        }
        return fingerprint;
    }

    private void cleanupExpired(long now, long expireMs) {
        if (IDEMPOTENT_CACHE.size() > 10000) {
            IDEMPOTENT_CACHE.entrySet().removeIf(entry -> (now - entry.getValue()) > expireMs * 2);
        }
    }
}
