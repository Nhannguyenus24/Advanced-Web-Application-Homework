package com.example.demo.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.io.IOException;

public class XSSFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
    }
    
    private static class XSSRequestWrapper extends HttpServletRequestWrapper {
        
        private static final PolicyFactory POLICY = Sanitizers.FORMATTING
                .and(Sanitizers.LINKS)
                .and(Sanitizers.BLOCKS);
        
        public XSSRequestWrapper(HttpServletRequest request) {
            super(request);
        }
        
        @Override
        public String[] getParameterValues(String parameter) {
            String[] values = super.getParameterValues(parameter);
            if (values == null) {
                return null;
            }
            
            int count = values.length;
            String[] encodedValues = new String[count];
            for (int i = 0; i < count; i++) {
                encodedValues[i] = sanitize(values[i]);
            }
            
            return encodedValues;
        }
        
        @Override
        public String getParameter(String parameter) {
            String value = super.getParameter(parameter);
            return sanitize(value);
        }
        
        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            return sanitize(value);
        }
        
        private String sanitize(String value) {
            if (value == null) {
                return null;
            }
            
            // Remove XSS patterns
            return POLICY.sanitize(value)
                    .replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll("\"", "&quot;")
                    .replaceAll("'", "&#x27;")
                    .replaceAll("/", "&#x2F;");
        }
    }
}

