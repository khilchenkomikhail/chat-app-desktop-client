package ru.edu.spbstu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.edu.spbstu.exception.ResourceNotFound;
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

        doThrow(new ResourceNotFound("")).when(loginService).sendTemporaryPassword(isA(String.class));

        mockMvc.perform(MockMvcRequestBuilders.patch("/send-tmp-password")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writer().writeValueAsString(bodyText)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(loginService, times(1)).sendTemporaryPassword(anyString());
    }

    @Test
    void sendTmpPassword_ValidLogin() throws Exception {
        String bodyText = "login";

        doNothing().when(loginService).sendTemporaryPassword(isA(String.class));

        mockMvc.perform(MockMvcRequestBuilders.patch("/send-tmp-password")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writer().writeValueAsString(bodyText)))
                .andExpect(status().isOk())
                .andReturn();

        verify(loginService, times(1)).sendTemporaryPassword(anyString());
    }
}