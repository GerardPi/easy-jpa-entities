package io.github.gerardpi.easy.jpaentities.test1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.Map;

public class MockMvcExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(MockMvcExecutor.class);

    private final MockMvc mockMvc;

    public MockMvcExecutor(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public MockMvcExecutor(WebApplicationContext wac) {
        this.mockMvc = IntegrationTestUtils.createMockMvc(wac);
    }

    public ResultActions executeHttpRequest(String httpMethodStr, String uri) {
        return executeHttpRequest(HttpMethod.valueOf(httpMethodStr), uri);
    }

    public ResultActions executeHttpRequest(String httpMethodStr, String uri, String content) {
        return executeHttpRequest(HttpMethod.valueOf(httpMethodStr), uri, content);
    }
    public ResultActions executeHttpRequest(String httpMethodStr, String uri, Map<String, String> httpHeaders, String content) {
        return executeHttpRequest(HttpMethod.valueOf(httpMethodStr), uri, httpHeaders, content);
    }

    public ResultActions executeHttpRequest(HttpMethod httpMethod, String uri) {
        return executeHttpRequest(httpMethod, uri, Collections.emptyMap(), null);
    }

    public ResultActions executeHttpRequest(HttpMethod httpMethod, String uri, String content) {
        return executeHttpRequest(httpMethod, uri, Collections.emptyMap(), content);
    }

    public ResultActions executeHttpRequest(HttpMethod httpMethod, String uri, Map<String, String> headers, String content) {
        try {
            MockHttpServletRequestBuilder reqBuilder = IntegrationTestUtils.createRequestBuilder(httpMethod, uri, content);
            headers.forEach(reqBuilder::header);
            return mockMvc
                    .perform(reqBuilder)
                    .andDo(MockMvcResultHandlers.print());
        } catch (Exception e) {
            LOG.info("Caught {}: '{}'", e.getClass(), e.getMessage());
            throw new IllegalStateException(e);
        }
    }
}
