package org.pfemanager.security;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import org.pfemanager.controller.AuthBean;
import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*", "/etudiant/*", "/encadrant/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        boolean connecte = false;

        if (session != null) {
            AuthBean authBean = (AuthBean) session.getAttribute("authBean");
            connecte = (authBean != null && authBean.isConnecte());
        }

        if (!connecte) {
            res.sendRedirect(req.getContextPath() + "/login.xhtml");
        } else {
            chain.doFilter(request, response);
        }
    }
}