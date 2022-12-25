package ru.edu.spbstu.security;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ru.edu.spbstu.security.datemanagement.CustomBasicAuthFilter;
import ru.edu.spbstu.security.datemanagement.RemMeAuthSuccHand;
import ru.edu.spbstu.security.datemanagement.TokenCreationDateRepo;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final ApplicationUserService applicationUserService;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;
    private final RemMeAuthSuccHand remMeAuthSuccHand;
    private final TokenCreationDateRepo tokenCreationDateRepo;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   @Autowired AuthenticationManager authManager) throws Exception {
        http
                .addFilterAt(new CustomBasicAuthFilter(authManager, tokenCreationDateRepo), BasicAuthenticationFilter.class)
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                    .authorizeRequests()
                    .antMatchers("/register", "/send-tmp-password","/check_user_email", "/is_user_present","/is_email_used", "h2-console/**").permitAll()
                    .anyRequest()
                    .authenticated()
                .and()
                .rememberMe()
                    .userDetailsService(applicationUserService)
                    .tokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(2))
                    .rememberMeParameter("remember-me")
                    .tokenRepository(tokenRepository())
                    .authenticationSuccessHandler(remMeAuthSuccHand)
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }
}
