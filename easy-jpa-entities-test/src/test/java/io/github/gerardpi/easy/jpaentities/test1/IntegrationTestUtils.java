package io.github.gerardpi.easy.jpaentities.test1;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class IntegrationTestUtils {
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
                Optional.ofNullable(response).ifPresent(r -> r.setCharacterEncoding(StandardCharsets.UTF_8.toString()));
                if (filterChain != null) {
                    filterChain.doFilter(request, response);
                }
            }
        };
    }
}
