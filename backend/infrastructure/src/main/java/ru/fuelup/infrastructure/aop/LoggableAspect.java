package ru.fuelup.infrastructure.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.fuelup.common.annotations.Loggable;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggableAspect {

    @Around("@annotation(loggable)")
    public Object logMethodCall(ProceedingJoinPoint pjp, Loggable loggable) throws Throwable {
        var signature = (MethodSignature) pjp.getSignature();
        var methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

        if (log.isDebugEnabled()) {
            log.debug(">> {} args={}", methodName, Arrays.toString(pjp.getArgs()));
        }

        long start = System.currentTimeMillis();
        try {
            var result = pjp.proceed();
            long elapsed = System.currentTimeMillis() - start;
            if (loggable.logResult()) {
                log.debug("<< {} result={} elapsed={}ms", methodName, result, elapsed);
            } else {
                log.debug("<< {} elapsed={}ms", methodName, elapsed);
            }
            return result;
        } catch (Throwable t) {
            log.error("<< {} threw {} after {}ms", methodName, t.getClass().getSimpleName(),
                    System.currentTimeMillis() - start);
            throw t;
        }
    }
}
