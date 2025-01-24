package com.icodesoft.auth.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author gary
 */
@Slf4j
public class SecurityUtil {

    private SecurityUtil() {
    }

    public static void exceptionHandler(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        Map<String, String> parameters = getErrorParameter(request, response, e);
        String wwwAuthenticate = computeWwwAuthenticateHeaderValue(parameters);
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
        try {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(Json.stringify(parameters));
            response.getWriter().flush();
        } catch (IOException ex) {
            log.error("error", e);
        }
    }

    private static Map<String, String> getErrorParameter(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        Map<String, String> parameters = new LinkedHashMap<>();
        if (request.getUserPrincipal() instanceof AbstractOAuth2TokenAuthenticationToken) {
            // 权限不足
            parameters.put("error", BearerTokenErrorCodes.INSUFFICIENT_SCOPE);
            parameters.put("error_description",
                    "The request requires higher privileges than provided by the access token.");
            parameters.put("error_uri", "https://icodesoft.com/error.html");
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
        if (e instanceof OAuth2AuthenticationException authenticationException) {
            // jwt异常，e.g. jwt超过有效期、jwt无效等
            OAuth2Error error = authenticationException.getError();
            parameters.put("error", error.getErrorCode());
            if (StringUtils.hasText(error.getUri())) {
                parameters.put("error_uri", error.getUri());
            }
            if (StringUtils.hasText(error.getDescription())) {
                parameters.put("error_description", error.getDescription());
            }
            if (error instanceof BearerTokenError bearerTokenError) {
                if (StringUtils.hasText(bearerTokenError.getScope())) {
                    parameters.put("scope", bearerTokenError.getScope());
                }
                response.setStatus(bearerTokenError.getHttpStatus().value());
            }
        }
        if (e instanceof InsufficientAuthenticationException) {
            parameters.put("error", BearerTokenErrorCodes.INVALID_TOKEN);
            parameters.put("error_description", "Not authorized.");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        parameters.put("message", e.getMessage());
        return parameters;
    }

    /**
     * 请求头的错误信息
     */
    public static String computeWwwAuthenticateHeaderValue(Map<String, String> parameters) {
        StringBuilder wwwAuthenticate = new StringBuilder();
        wwwAuthenticate.append("Bearer");
        if (!parameters.isEmpty()) {
            wwwAuthenticate.append(" ");
            int i = 0;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                wwwAuthenticate.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                if (i != parameters.size() - 1) {
                    wwwAuthenticate.append(", ");
                }
                i++;
            }
        }
        return wwwAuthenticate.toString();
    }
}
