package ru.documents.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Аспект для логирования выполнения методов репозиториев, сервисов и контроллеров.
 * @author Артем Дружинин.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {
    /**
     * Pointcut, соответствующий всем репозиториям, сервисам и контроллерам.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
    }

    /**
     * Pointcut, соответсвующий всем компонентам Spring в основных пакетах приложения.
     */
    @Pointcut("within(ru.documents.repository..*)" +
            " || within(ru.documents.service..*)" +
            " || within(ru.documents.controller..*)")
    public void applicationPackagePointcut() {
    }

    /**
     * Advice для логирования случаев получения исключений
     * в методах всех репозиториев, сервисов и контроллеров.
     *
     * @param joinPoint Точка, из которой вызван метод логирования.
     * @param e Исключение
     */
    @AfterThrowing(pointcut = "springBeanPointcut() && applicationPackagePointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
    }

    /**
     * Advice для логирования моментов входа и выхода
     * из методов классов репозиториев, сервисов и контроллеров.
     *
     * @param joinPoint Точка, из которой вызван метод логирования.
     * @return Возвращает результат выполнения метода, для которого осуществляется логирование.
     * @throws Throwable любое исключение, выбрасываемое методом, для которого осуществляется логирование.
     */
    @Around("springBeanPointcut() && applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Entered method: {}.{}() with argument(s) = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isDebugEnabled()) {
                log.debug("Exited method: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), result);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument(s): {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }
}
