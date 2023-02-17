package ru.edu.spbstu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import ru.edu.spbstu.exception.ResourceNotFound;
import ru.edu.spbstu.model.Language;
import ru.edu.spbstu.request.CheckEmailRequest;
import ru.edu.spbstu.request.SendTemporaryPasswordRequest;
import ru.edu.spbstu.request.SignUpRequest;
import ru.edu.spbstu.service.LoginService;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private LoginController loginController;

    @Mock
    private LoginService loginService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    void sendTmpPassword_InvalidLogin() throws Exception {
        String bodyText = "login";
        SendTemporaryPasswordRequest request = new SendTemporaryPasswordRequest();
        request.setLogin(bodyText);
        request.setLanguage(Language.ENGLISH);

        doThrow(new ResourceNotFound("")).when(loginService).sendTemporaryPassword(isA(String.class), isA(Language.class));

        mockMvc.perform(MockMvcRequestBuilders.patch("/send-tmp-password")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writer().writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(loginService, times(1)).sendTemporaryPassword(anyString(), any(Language.class));
    }

    @Test
    void sendTmpPassword_ValidLogin() throws Exception {
        String bodyText = "login";
        SendTemporaryPasswordRequest request = new SendTemporaryPasswordRequest();
        request.setLogin(bodyText);
        request.setLanguage(Language.ENGLISH);

        doNothing().when(loginService).sendTemporaryPassword(isA(String.class), isA(Language.class));

        mockMvc.perform(MockMvcRequestBuilders.patch("/send-tmp-password")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writer().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        verify(loginService, times(1)).sendTemporaryPassword(anyString(), any(Language.class));
    }

    @Test
    void signUpTest() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setLogin("login");
        request.setPassword("password");
        request.setEmail("mail@mail.com");

        doNothing().when(loginService).signUp(isA(SignUpRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writer().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        verify(loginService, times(1)).signUp(any(SignUpRequest.class));
    }

    @Test
    void checkUserEmail() throws Exception {
        CheckEmailRequest request = new CheckEmailRequest();
        request.setLogin("login");
        request.setEmail("mail@mail.com");

        when(loginService.checkUserEmail(isA(CheckEmailRequest.class))).thenReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/check_user_email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writer().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        verify(loginService, times(1)).checkUserEmail(any(CheckEmailRequest.class));
        Assertions.assertTrue(Boolean.parseBoolean(result.getResponse().getContentAsString()));

        when(loginService.checkUserEmail(isA(CheckEmailRequest.class))).thenReturn(false);

        result = mockMvc.perform(MockMvcRequestBuilders.post("/check_user_email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writer().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        verify(loginService, times(2)).checkUserEmail(any(CheckEmailRequest.class));
        Assertions.assertFalse(Boolean.parseBoolean(result.getResponse().getContentAsString()));
    }

    @Test
    void isUserPresent() throws Exception {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("login", "login");

        when(loginService.isUserPresent(anyString())).thenReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/is_user_present")
                        .params(params))
                .andExpect(status().isOk())
                .andReturn();

        verify(loginService, times(1)).isUserPresent(anyString());
        Assertions.assertTrue(Boolean.parseBoolean(result.getResponse().getContentAsString()));

        when(loginService.isUserPresent(anyString())).thenReturn(false);

        result = mockMvc.perform(MockMvcRequestBuilders.get("/is_user_present")
                        .params(params))
                .andExpect(status().isOk())
                .andReturn();

        verify(loginService, times(2)).isUserPresent(anyString());
        Assertions.assertFalse(Boolean.parseBoolean(result.getResponse().getContentAsString()));

    }

    @Test
    void isEmailUsed() throws Exception {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("email", "email");

        when(loginService.isEmailUsed(anyString())).thenReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/is_email_used")
                        .params(params))
                .andExpect(status().isOk())
                .andReturn();

        verify(loginService, times(1)).isEmailUsed(anyString());
        Assertions.assertTrue(Boolean.parseBoolean(result.getResponse().getContentAsString()));

        when(loginService.isEmailUsed(anyString())).thenReturn(false);

        result = mockMvc.perform(MockMvcRequestBuilders.get("/is_email_used")
                        .params(params))
                .andExpect(status().isOk())
                .andReturn();

        verify(loginService, times(2)).isEmailUsed(anyString());
        Assertions.assertFalse(Boolean.parseBoolean(result.getResponse().getContentAsString()));

    }

    @Test
    void getLogin() throws Exception {
        String login = "login";

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(login);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/get_login"))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(login, result.getResponse().getContentAsString());
    }
}