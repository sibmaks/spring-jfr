package com.github.sibmaks.spring.jfr.controller.rest;

import com.github.sibmaks.spring.jfr.OnClassConditional;
import com.github.sibmaks.spring.jfr.event.RestControllerInvocationEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@OnClassConditional("org.springframework.web.bind.annotation.RestController")
public class RestControllerJfrAspect {

    @Pointcut("@within(restController) && execution(* *(..))")
    public void restControllerMethods(RestController restController) {
        // Pointcut to capture all methods in classes annotated with @RestController
    }

    @Around(value = "restControllerMethods(restController)", argNames = "joinPoint,restController")
    public Object traceRestController(ProceedingJoinPoint joinPoint, RestController restController) throws Throwable {
        var requestAttributes = RequestContextHolder.getRequestAttributes();
        String url = null;
        String method = null;
        if(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            var rq = servletRequestAttributes.getRequest();
            url = rq.getRequestURI();
            method = rq.getMethod();
        }
        var event = new RestControllerInvocationEvent(
                joinPoint.getSignature().toShortString(),
                method,
                url
        );

        event.begin();
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            event.setException(throwable.toString());
            throw throwable;
        } finally {
            event.commit();
        }
    }
}
