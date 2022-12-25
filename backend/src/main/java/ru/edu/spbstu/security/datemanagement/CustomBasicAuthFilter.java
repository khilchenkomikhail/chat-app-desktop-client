package ru.edu.spbstu.security.datemanagement;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class CustomBasicAuthFilter extends BasicAuthenticationFilter {

    private final TokenCreationDateRepo tokenCreationDateRepo;

    public CustomBasicAuthFilter(AuthenticationManager authenticationManager, TokenCreationDateRepo tokenCreationDateRepo) {
        super(authenticationManager);
        this.tokenCreationDateRepo = tokenCreationDateRepo;
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
        super.onSuccessfulAuthentication(request, response, authResult);
        String parameter = request.getParameter("remember-me");
        if (parameter != null) {
            String name = authResult.getName();
            TokenCreationDate tokenCreationDate = new TokenCreationDate();
            tokenCreationDate.setLogin(name);
            tokenCreationDate.setCreationDate(LocalDateTime.now());
            tokenCreationDateRepo.save(tokenCreationDate);
        }
    }
}
