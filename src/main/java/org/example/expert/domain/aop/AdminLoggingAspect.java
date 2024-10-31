package org.example.expert.domain.aop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.expert.config.CustomResponseWrapper;
import org.example.expert.config.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@Aspect
@Slf4j
public class AdminLoggingAspect {

//    private final JwtUtil jwtUtil;
//    private final HttpServletRequest request;
//    private final HttpServletResponse response;

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

        // CustomResponseWrapper로 응답 래핑
        CustomResponseWrapper responseWrapper = new CustomResponseWrapper(response);


        // 요청 본문 읽기
        String requestBody = "";
        StringBuilder requestBodyBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBodyBuilder.append(line);
            }
        }
        requestBody = requestBodyBuilder.toString();

        // 요청 정보 로깅
        String requestURL = request.getRequestURI();
        String responseBody = "";

        LocalDateTime requestTime = LocalDateTime.now();


        try {
            Object output = joinPoint.proceed();
            // 응답 본문 가져오기
            responseBody = responseWrapper.getResponseBody();
            return output;
        } finally {

            // 로깅
            log.info("Request URL: {}", requestURL);
            log.info("Request Body: {}", requestBody);
            log.info("Response Body: {}", responseBody);
            log.info("RequestTime: {} ms", requestTime);

        }
    }
}
