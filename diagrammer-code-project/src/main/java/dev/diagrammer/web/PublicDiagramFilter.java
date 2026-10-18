package dev.diagrammer.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Answers {@code /diagrams/**} with 404 after security has allowed the request.
 * Vaadin's servlet is mapped to {@code /*}; this filter keeps those public
 * URLs from falling through into the authenticated shell.
 */
@Configuration
public class PublicDiagramFilter {

    /**
     * Spring Security's filter is ordered at -100. This runs immediately after it,
     * so a denied request is already rejected and a permitted {@code /diagrams/**}
     * request is turned into the placeholder 404.
     */
    private static final int AFTER_SECURITY = -99;

    @Bean
    FilterRegistrationBean<OncePerRequestFilter> publicDiagramPlaceholder() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new PlaceholderFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(AFTER_SECURITY);
        return registration;
    }

    static final class PlaceholderFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(
                HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, IOException {
            String path = request.getRequestURI().substring(request.getContextPath().length());
            if ("/diagrams".equals(path) || path.startsWith("/diagrams/")) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Diagram not found");
                return;
            }
            chain.doFilter(request, response);
        }
    }
}
