package com.wms.common.aspect;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;

import com.wms.common.annotation.AuditLog;
import com.wms.common.security.CustomUserDetails;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuditLogAspect {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    @Pointcut("@annotation(com.wms.common.annotation.AuditLog)")
    public void auditLogPointcut() {
    }

    @Around("auditLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - start;

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuditLog annotation = method.getAnnotation(AuditLog.class);

        String username = "anonymous";
        Long userId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            username = userDetails.getUsername();
            userId = userDetails.getUserId();
        }

        String uri = "";
        String httpMethod = "";
        String ip = "";
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest req = attrs.getRequest();
            uri = req.getRequestURI();
            httpMethod = req.getMethod();
            ip = getClientIp(req);
        }

        auditLogger.info("userId={} username={} ip={} method={} uri={} module={} action={} elapsed={}ms",
                userId, username, ip, httpMethod, uri,
                annotation.module(), annotation.value(), elapsed);

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
