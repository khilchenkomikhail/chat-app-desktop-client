package ru.edu.spbstu.security.datemanagement;

import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

public class RememberMeStrictLifetimeTokenService extends PersistentTokenBasedRememberMeServices {

    private final TokenCreationDateRepo tokenCreationDateRepo;
    private final PersistentTokenRepository tokenRepository;

    public RememberMeStrictLifetimeTokenService(String key,
                                                UserDetailsService userDetailsService,
                                                PersistentTokenRepository tokenRepository,
                                                TokenCreationDateRepo tokenCreationDateRepo) {
        super(key, userDetailsService, tokenRepository);
        this.tokenCreationDateRepo = tokenCreationDateRepo;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 + " tokens, but contained '"
                    + Arrays.asList(cookieTokens) + "'");
        }
        String presentedSeries = cookieTokens[0];
        String presentedToken = cookieTokens[1];
        PersistentRememberMeToken token = this.tokenRepository.getTokenForSeries(presentedSeries);
        if (token == null) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }
        // We have a match for this user/series combination
        if (!presentedToken.equals(token.getTokenValue())) {
            // Token doesn't match series value. Delete all logins for this user and throw
            // an exception to warn them.
            this.tokenRepository.removeUserTokens(token.getUsername());
            throw new CookieTheftException(this.messages.getMessage(
                    "PersistentTokenBasedRememberMeServices.cookieStolen",
                    "Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack."));
        }

        TokenCreationDate byLogin = tokenCreationDateRepo.getByLogin(token.getUsername());
        ZoneId zoneId = ZoneId.systemDefault();
        long epoch = byLogin.getCreationDate().atZone(zoneId).toEpochSecond();

        long l1 = token.getDate().getTime() + getTokenValiditySeconds() * 1000L;
        long l2 = (epoch + getTokenValiditySeconds()) * 1000L;
        long l3 = System.currentTimeMillis();
        if (l1 < l3
                || l2 < l3) {
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }
        // Token also matches, so login is valid. Update the token value, keeping the
        // *same* series number.
        this.logger.debug(LogMessage.format("Refreshing persistent login token for user '%s', series '%s'",
                token.getUsername(), token.getSeries()));
        PersistentRememberMeToken newToken = new PersistentRememberMeToken(token.getUsername(), token.getSeries(),
                generateTokenData(), new Date());
        try {
            this.tokenRepository.updateToken(newToken.getSeries(), newToken.getTokenValue(), newToken.getDate());
            addCookie(newToken, request, response);
        }
        catch (Exception ex) {
            this.logger.error("Failed to update token: ", ex);
            throw new RememberMeAuthenticationException("Autologin failed due to data access problem");
        }
        return getUserDetailsService().loadUserByUsername(token.getUsername());
    }

    private void addCookie(PersistentRememberMeToken token, HttpServletRequest request, HttpServletResponse response) {
        setCookie(new String[] { token.getSeries(), token.getTokenValue() }, getTokenValiditySeconds(), request,
                response);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        super.logout(request, response, authentication);
        if (authentication != null) {
            tokenCreationDateRepo.deleteByLogin(authentication.getName());
        }
    }

    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        String username = successfulAuthentication.getName();
        this.logger.debug(LogMessage.format("Creating new persistent login for user %s", username));
        PersistentRememberMeToken persistentToken = new PersistentRememberMeToken(username, generateSeriesData(),
                generateTokenData(), new Date());
        TokenCreationDate tokenCreationDate = new TokenCreationDate();
        tokenCreationDate.setLogin(username);
        tokenCreationDate.setCreationDate(LocalDateTime.now());
        try {
            this.tokenRepository.createNewToken(persistentToken);
            tokenCreationDateRepo.save(tokenCreationDate);
            addCookie(persistentToken, request, response);
        }
        catch (Exception ex) {
            this.logger.error("Failed to save persistent token ", ex);
        }
    }
}
