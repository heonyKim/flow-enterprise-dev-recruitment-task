package com.heony.flowteam.enterprisedevrecruitmenttask._common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heony.flowteam.enterprisedevrecruitmenttask._common.util.MyStringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyFilter extends OncePerRequestFilter {

    private static final Logger requestLogger = LoggerFactory.getLogger("REQUEST_LOGGER");
    public static final String[] PERMIT_STATIC_URLs = {
            "/favicon.ico",
            "/images/**",

            "/api-docs",
            "/api-docs/swagger-config",

            /* swagger v2 */
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",

            /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**",

            /* h2-console*/
            "/h2-console/**"
    };

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappingResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(wrappingRequest, wrappingResponse);
        } finally {
            String uri = request.getRequestURI();
            long duration = System.currentTimeMillis() - startTime;
            if(!MyStringUtils.isContainsUriPatterns(PERMIT_STATIC_URLs, uri)){
                logRequest(wrappingRequest, wrappingResponse, duration);
            }
            wrappingResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long duration) {

        try {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("method", request.getMethod());
            logMap.put("uri", request.getRequestURI());
            logMap.put("status", response.getStatus());
            logMap.put("duration", duration + "ms");
            logMap.put("remoteAddr", request.getRemoteAddr());

            Map<String, String> headers = new HashMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName, request.getHeader(headerName));
            }
            logMap.put("headers", headers);

            String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(requestBody)) {
                // 로깅 시, 보안 이슈가 있는것은 별도로 처리가 가능
//                if(request.getRequestURI().equals("보안처리할 PATH")){
//
//                }
                logMap.put("requestBody", requestBody);
            }

            String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(responseBody)) {
                logMap.put("responseBody", responseBody);
            }

            requestLogger.info(objectMapper.writeValueAsString(logMap));
        } catch (Exception e) {
            log.error("Logging failed", e);
        }
    }
}
