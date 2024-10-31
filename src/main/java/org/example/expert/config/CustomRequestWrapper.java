package org.example.expert.config;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CustomRequestWrapper extends HttpServletRequestWrapper {
    private byte[] cachedBody;

    public CustomRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 요청 본문을 캐시합니다.
        cachedBody = request.getInputStream().readAllBytes();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedBodyServletInputStream(cachedBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    // 내부 클래스를 통해 캐시된 본문을 반환합니다.
    private static class CachedBodyServletInputStream extends ServletInputStream {
        private final byte[] body;
        private int index = 0;

        public CachedBodyServletInputStream(byte[] body) {
            this.body = body;
        }

        @Override
        public boolean isFinished() {
            return index >= body.length;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            // Not implemented
        }

        @Override
        public int read() throws IOException {
            if (index >= body.length) {
                return -1; // EOF
            }
            return body[index++];
        }
    }
}