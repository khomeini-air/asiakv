package com.asia.asiakv.shared.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 *  A custom filter to Log basic request/response information and enriches logs with MDC data: traceId - unique ID per request.
 */
@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Add traceId
        MDC.put("traceId", UUID.randomUUID().toString());

        long start = System.currentTimeMillis();

        try {
            // Continue the filter chain
            filterChain.doFilter(request, response);
        } finally {
            // Log request summary after processing
            long duration = System.currentTimeMillis() - start;

            log.info("request completed method={} path={} status={} durationMs={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);

            // Clear the MDC after processing
            MDC.clear();
        }
    }
}
