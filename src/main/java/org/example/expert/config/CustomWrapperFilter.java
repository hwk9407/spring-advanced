package org.example.expert.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(2)
public class CustomWrapperFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 래핑된 요청과 응답 생성
        CustomRequestWrapper customRequestWrapper = new CustomRequestWrapper(httpRequest);
        CustomResponseWrapper customResponseWrapper = new CustomResponseWrapper(httpResponse);

        // 체인 진행
        chain.doFilter(customRequestWrapper, customResponseWrapper);
    }
}
