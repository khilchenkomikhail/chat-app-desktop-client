package ru.edu.spbstu.security;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import ru.edu.spbstu.security.datemanagement.RememberMeStrictLifetimeTokenService;
import ru.edu.spbstu.security.datemanagement.TokenCreationDateRepo;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final ApplicationUserService applicationUserService;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;
    private final TokenCreationDateRepo tokenCreationDateRepo;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        RememberMeStrictLifetimeTokenService rememberMeStrictLifetimeTokenService = new RememberMeStrictLifetimeTokenService(UUID.randomUUID().toString(),
                applicationUserService,
                tokenRepository(),
                tokenCreationDateRepo);
        rememberMeStrictLifetimeTokenService.setTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30));

        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                    .authorizeRequests()
                    .antMatchers("/register", "/send-tmp-password","/check_user_email", "/is_user_present","/is_email_used").permitAll()
                    .anyRequest()
                    .authenticated()
                .and()
                .rememberMe()
                    .rememberMeServices(rememberMeStrictLifetimeTokenService)
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                .and()
                .httpBasic();
        return http.build();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
}
