package io.github.gerardpi.easy.jpaentities.test1.web.headrequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ForceGetRequestWrapper extends HttpServletRequestWrapper {
    public ForceGetRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * Lies about the HTTP method.
     *
     * @return A GET at all times.
     */
    @Override
    public String getMethod() {
        return "GET";
    }
}
