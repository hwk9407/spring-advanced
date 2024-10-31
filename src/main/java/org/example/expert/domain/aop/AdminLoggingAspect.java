package org.example.expert.domain.aop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.expert.config.CustomRequestWrapper;
import org.example.expert.config.CustomResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Component
@Aspect
@Slf4j
public class AdminLoggingAspect {


     /*
     <적용범위>
     - execution: 메서드 실행 시점 기준으로 적용
     - 반환타입: *(전부)
     - 경로: domain.*(모든패키지).controller
     - 클래스명: ~AdminController로 끝나는 클래스 전부
     - 메서드명: *(전부), 파라미터도 (..) (전부)
     */
    @Pointcut("execution(* org.example.expert.domain.*.controller.*AdminController.*(..))")
    public void adminControllerPointcut() {}

    /*
    <요구사항>
    - 요청한 사용자의 ID
    - API 요청 시각
    - API 요청 URL
    - response, request body 도 찍기
     */
    @Around("adminControllerPointcut()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        // 요청과 응답 가져오기
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        // CustomWrapper로 요청, 응답 래핑
        CustomResponseWrapper responseWrapper = new CustomResponseWrapper(response);
        CustomRequestWrapper customRequestWrapper = new CustomRequestWrapper(request);
        String requestBody = new String(customRequestWrapper.getInputStream().readAllBytes());

        // 요청 정보 로깅
        String requestURL = request.getRequestURI();
        String responseBody = "";

        LocalDateTime requestTime = LocalDateTime.now();


        try {
            Object output = joinPoint.proceed();
            responseBody = responseWrapper.getResponseBody();
            return output;
        } finally {
            Long userId = (Long) request.getAttribute("userId");

            // 로깅
            log.info("Request ID: {}", userId);
            log.info("Request URL: {}", requestURL);
            log.info("Request Body:\n{}", requestBody);
            log.info("Response Body:\n{}", responseBody);
            log.info("RequestTime: {} ms", requestTime);

        }
    }
}
