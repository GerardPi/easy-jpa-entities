package io.github.gerardpi.easy.jpaentities.test1.web.headrequest;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

@Order(2)
@Component
public class HttpHeadFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (isHttpHead(httpServletRequest)) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            NoBodyResponseWrapper noBodyResponseWrapper = new NoBodyResponseWrapper(response);
            filterChain.doFilter(new ForceGetRequestWrapper(httpServletRequest), noBodyResponseWrapper);
            noBodyResponseWrapper.setContentLength();
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Do nothing.
    }

    @Override
    public void destroy() {
        // Do nothing.
    }

    private boolean isHttpHead(HttpServletRequest request) {
        return "HEAD".equals(request.getMethod());
    }

    private static class NoBodyResponseWrapper extends HttpServletResponseWrapper {
        private final NoBodyOutputStream noBodyOutputStream = new NoBodyOutputStream();
        private PrintWriter writer;

        public NoBodyResponseWrapper(HttpServletResponse response) {
            super(response);
        }
        @Override
        public ServletOutputStream getOutputStream() {
            return noBodyOutputStream;
        }

        @Override
        public PrintWriter getWriter() throws UnsupportedEncodingException {
            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(noBodyOutputStream, getCharacterEncoding()));
            }
            return writer;
        }

        void setContentLength() {
            super.setContentLength(noBodyOutputStream.getContentLength());
        }
    }

    private static class NoBodyOutputStream extends ServletOutputStream {
        private int contentLength = 0;

        int getContentLength() {
            return contentLength;
        }

        @Override
        public void write(byte[] b, int off, int len) {
            contentLength += len;
        }

        @Override
        public void write(int b) {
            contentLength++;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            // Nothing...
        }
    }
}
