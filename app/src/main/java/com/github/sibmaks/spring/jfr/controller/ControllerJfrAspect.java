package com.github.sibmaks.spring.jfr.controller;

import com.github.sibmaks.spring.jfr.OnClassConditional;
import com.github.sibmaks.spring.jfr.event.ControllerInvocationEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@OnClassConditional("org.springframework.stereotype.Controller")
public class ControllerJfrAspect {

    @Pointcut("@within(controller) && execution(* *(..))")
    public void controllerMethods(Controller controller) {
        // Pointcut to capture all methods in classes annotated with @RestController
    }

    @Around(value = "controllerMethods(controller)", argNames = "joinPoint,controller")
    public Object traceRestController(ProceedingJoinPoint joinPoint, Controller controller) throws Throwable {
        var requestAttributes = RequestContextHolder.getRequestAttributes();
        String url = null;
        String method = null;
        if(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            var rq = servletRequestAttributes.getRequest();
            url = rq.getRequestURI();
            method = rq.getMethod();
        }
        var event = new ControllerInvocationEvent(
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
