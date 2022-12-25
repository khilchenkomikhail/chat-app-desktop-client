package ru.edu.spbstu.security.datemanagement;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.edu.spbstu.security.datemanagement.TokenCreationDate;
import ru.edu.spbstu.security.datemanagement.TokenCreationDateRepo;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class RemMeAuthSuccHand  implements AuthenticationSuccessHandler {

    private final TokenCreationDateRepo tokenCreationDateRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String name = authentication.getName();
        TokenCreationDate byLogin = tokenCreationDateRepo.getByLogin(name);
        if (byLogin.getCreationDate().plusDays(30).isBefore(LocalDateTime.now())) {
            tokenCreationDateRepo.delete(byLogin);
            response.sendError(401);
        }

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }
}
