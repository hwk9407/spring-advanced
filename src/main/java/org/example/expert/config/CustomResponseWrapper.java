package org.example.expert.config;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

public class CustomResponseWrapper extends HttpServletResponseWrapper {
    private CharArrayWriter charArrayWriter = new CharArrayWriter();
    private PrintWriter writer = new PrintWriter(charArrayWriter);

    public CustomResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() {
        return writer; // 커스텀 프린트 라이터 반환
    }

    public String getResponseBody() {
        return charArrayWriter.toString(); // 응답 본문 반환
    }
}
