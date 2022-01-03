package com.djwilde.inzynierka.aspect;

import com.djwilde.inzynierka.helpers.LogHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.time.Duration;
import java.time.Instant;

@Aspect
public class LoggingAspect {
    private final LogHelper logHelper = LogHelper.getInstance();

    @Around("execution(* *(..)) && @annotation(com.djwilde.inzynierka.helpers.Timer)")
    public Object logPerformanceTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint);
    }

    @Around("execution(* *(..)) && @annotation(com.djwilde.inzynierka.helpers.Logged)")
    public Object logEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

    private Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant beginning = Instant.now();
        System.out.println("Wywołano " + joinPoint.getSignature());
        Object proceed = joinPoint.proceed();
        Instant end = Instant.now();
        long timeElapsed = Duration.between(beginning, end).toMillis();
        System.out.println("Metoda zakończyła działanie z czasem " + timeElapsed + " ms.");
        return proceed;
    }
}
