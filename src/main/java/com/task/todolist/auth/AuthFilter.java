package com.task.todolist.auth;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestedURI = httpRequest.getRequestURI();
        if (isAllowedPage(requestedURI)) {
            chain.doFilter(request, response);
            return;
        }

        if (session == null || session.getAttribute("username") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");

        } else {
            String userRole = (String) session.getAttribute("userRole");
            if (requestedURI.endsWith("/inputitem.jsp")) {
                if (userRole != null && userRole.equals("admin")) {
                    chain.doFilter(request, response);
                } else {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/unauthorized.jsp");
                }
            } else {
                chain.doFilter(request, response);
            }
        }
    }
    private boolean isAllowedPage(String requestedURI) {
        String[] allowedPages = { "/login.jsp","login","login.css","/register.jsp","register", "/registration-error.jsp"};
        for (String page : allowedPages) {
            if (requestedURI.endsWith(page)) {
                return true;
            }
        }
        return false;
    }
    public void destroy() {
    }
}