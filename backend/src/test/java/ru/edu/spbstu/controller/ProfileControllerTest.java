package ru.edu.spbstu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import ru.edu.spbstu.exception.ResourceNotFound;
import ru.edu.spbstu.exception.UnauthorizedAccess;
import ru.edu.spbstu.model.User;
import ru.edu.spbstu.request.EmailUpdateRequest;
import ru.edu.spbstu.request.ProfilePhotoUpdateRequest;
import ru.edu.spbstu.service.ProfileService;

import java.util.Objects;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
public class ProfileControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ProfileController profileController;

    @Mock
    private ProfileService profileService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();
    }

    @Test
    void getProfilePhoto_validLogin() throws Exception {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("login", "login");

        String kindOfEncodedString = "something";

        when(profileService.getProfilePhoto(anyString())).thenReturn(kindOfEncodedString);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/get_profile_photo")
                    .params(params))
                .andExpect(status().isOk())
                .andReturn();

        verify(profileService, times(1)).getProfilePhoto(anyString());
        Assertions.assertEquals(kindOfEncodedString, result.getResponse().getContentAsString());
    }

    @Test
    void getProfilePhoto_invalidLogin() throws Exception {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("login", "login");

        when(profileService.getProfilePhoto(anyString())).thenThrow(new ResourceNotFound("login login not found"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/get_profile_photo")
                        .params(params))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(profileService, times(1)).getProfilePhoto(anyString());
        Assertions.assertEquals(ResourceNotFound.class, Objects.requireNonNull(result.getResolvedException()).getClass());
    }

    @Test
    void getUser_ValidLogin() throws Exception {
        String login = "login";
        String password = "password";
        String email = "mail@mail.com";

        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setEmail(email);

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("login", login);

        when(profileService.getUser(anyString())).thenReturn(user);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/get_user")
                        .params(params))
                .andExpect(status().isOk())
                .andReturn();

        verify(profileService, times(1)).getUser(anyString());
        Assertions.assertEquals(user,
                new ObjectMapper().readValue(result.getResponse().getContentAsString(), User.class));
    }

    @Test
    void getUser_InvalidLogin() throws Exception {
        String login = "login";

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("login", login);

        when(profileService.getUser(anyString())).thenThrow(new ResourceNotFound("login login not found"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/get_user")
                        .params(params))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(profileService, times(1)).getUser(anyString());
        Assertions.assertEquals(ResourceNotFound.class,
                Objects.requireNonNull(result.getResolvedException()).getClass());
    }

    @Test
    void updateProfilePhoto_WrongUser() throws Exception {
        String login = "login";
        String encodedProfileImage = "New profile image";

        ProfilePhotoUpdateRequest request = new ProfilePhotoUpdateRequest();
        request.setLogin(login);
        request.setNew_profile_image(encodedProfileImage);

        doThrow(new UnauthorizedAccess("")).when(profileService).updateProfilePhoto(isA(ProfilePhotoUpdateRequest.class));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/update_profile_photo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        verify(profileService, times(1)).updateProfilePhoto(isA(ProfilePhotoUpdateRequest.class));
        Assertions.assertEquals(UnauthorizedAccess.class, Objects.requireNonNull(result.getResolvedException()).getClass());
    }

    @Test
    void updateProfilePhoto_InvalidLogin() throws Exception {
        String login = "login";
        String encodedProfileImage = "New profile image";

        ProfilePhotoUpdateRequest request = new ProfilePhotoUpdateRequest();
        request.setLogin(login);
        request.setNew_profile_image(encodedProfileImage);

        doThrow(new ResourceNotFound("")).when(profileService).updateProfilePhoto(isA(ProfilePhotoUpdateRequest.class));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/update_profile_photo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(profileService, times(1)).updateProfilePhoto(isA(ProfilePhotoUpdateRequest.class));
        Assertions.assertEquals(ResourceNotFound.class, Objects.requireNonNull(result.getResolvedException()).getClass());
    }

    @Test
    void updateProfilePhoto_Success() throws Exception {
        String login = "login";
        String encodedProfileImage = "New profile image";

        ProfilePhotoUpdateRequest request = new ProfilePhotoUpdateRequest();
        request.setLogin(login);
        request.setNew_profile_image(encodedProfileImage);

        doNothing().when(profileService).updateProfilePhoto(isA(ProfilePhotoUpdateRequest.class));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/update_profile_photo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        verify(profileService, times(1)).updateProfilePhoto(isA(ProfilePhotoUpdateRequest.class));
    }

    @Test
    void updateEmail_WrongUser() throws Exception {
        String login = "login";
        String email = "mail@mail.com";

        EmailUpdateRequest request = new EmailUpdateRequest();
        request.setLogin(login);
        request.setNew_email(email);

        doThrow(new UnauthorizedAccess("")).when(profileService).updateEmail(isA(EmailUpdateRequest.class));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/update_email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        verify(profileService, times(1)).updateEmail(isA(EmailUpdateRequest.class));
        Assertions.assertEquals(UnauthorizedAccess.class, Objects.requireNonNull(result.getResolvedException()).getClass());
    }

    @Test
    void updateEmail_InvalidLogin() throws Exception {
        String login = "login";
        String email = "mail@mail.com";

        EmailUpdateRequest request = new EmailUpdateRequest();
        request.setLogin(login);
        request.setNew_email(email);

        doThrow(new ResourceNotFound("")).when(profileService).updateEmail(isA(EmailUpdateRequest.class));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/update_email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(profileService, times(1)).updateEmail(isA(EmailUpdateRequest.class));
        Assertions.assertEquals(ResourceNotFound.class, Objects.requireNonNull(result.getResolvedException()).getClass());
    }

    @Test
    void updateEmail_Success() throws Exception {
        String login = "login";
        String email = "mail@mail.com";

        EmailUpdateRequest request = new EmailUpdateRequest();
        request.setLogin(login);
        request.setNew_email(email);

        doNothing().when(profileService).updateEmail(isA(EmailUpdateRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/update_email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        verify(profileService, times(1)).updateEmail(isA(EmailUpdateRequest.class));
    }
}
