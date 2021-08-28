package io.github.gerardpi.easy.jpaentities.test1;

import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

public class IntegrationTestUtils {
    public static final Map<HttpMethod, Function<String, MockHttpServletRequestBuilder>> MOCK_HTTP_SERVLET_REQ_BUILDERS =
            ImmutableMap.<HttpMethod, Function<String, MockHttpServletRequestBuilder>>builder()
                    .put(HttpMethod.GET, MockMvcRequestBuilders::get)
                    .put(HttpMethod.HEAD, MockMvcRequestBuilders::head)
                    .put(HttpMethod.POST, MockMvcRequestBuilders::post)
                    .put(HttpMethod.PUT, MockMvcRequestBuilders::put)
                    .put(HttpMethod.PATCH, MockMvcRequestBuilders::patch)
                    .put(HttpMethod.OPTIONS, MockMvcRequestBuilders::options)
                    .put(HttpMethod.DELETE, MockMvcRequestBuilders::delete)
                    .build();

    public static MockMvc createMockMvc(WebApplicationContext wac) {
        return MockMvcBuilders
                .webAppContextSetup(wac)
                .addFilter(createUtf8Filter())
                .build();
    }

    public static Filter createUtf8Filter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
                filterChain.doFilter(request, response);
            }
        };
    }

    public static MockHttpServletRequestBuilder createRequestBuilder(HttpMethod httpMethod, String uri, String body) {
        MockHttpServletRequestBuilder reqBuilder = createRequestBuilder(httpMethod, uri);
        if (body != null) {
            reqBuilder
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(body);
        }
        return reqBuilder;
    }

    public static MockHttpServletRequestBuilder createRequestBuilder(HttpMethod httpMethod, String uri) {
        return ofNullable(MOCK_HTTP_SERVLET_REQ_BUILDERS.get(httpMethod))
                .orElseThrow(() -> new IllegalArgumentException("No " + HttpMethod.class.getSimpleName() +
                        " exists for '" + httpMethod + "'"))
                .apply(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(StandardCharsets.UTF_8.displayName());
    }

    public static MockHttpServletRequestBuilder createRequestBuilder(String httpMethodStr, String uri, String body) {
        return createRequestBuilder(HttpMethod.valueOf(httpMethodStr), uri, body);
    }

    public static MockHttpServletRequestBuilder createRequestBuilder(String httpMethodStr, String uri) {
        return createRequestBuilder(HttpMethod.valueOf(httpMethodStr), uri);
    }
    public static String extractJson(String json, String jsonPath) {
        DocumentContext jsonDocContext = JsonPath.parse(json);
        return jsonDocContext.read(jsonPath, String.class);
    }
    public static String extractJsonArray(String json, String jsonPath) {
        return JsonPath.read(json, jsonPath).toString();
    }
    public static int extractJsonArraySize(String json, String jsonPath) {
        JSONArray jsonArray = JsonPath.read(json, jsonPath);
        return jsonArray.size();
    }
}
