package IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.edu.spbstu.Application;
import ru.edu.spbstu.controller.LoginController;
import ru.edu.spbstu.dao.UserRepository;
import ru.edu.spbstu.model.Language;
import ru.edu.spbstu.model.jpa.UserJpa;
import ru.edu.spbstu.request.CheckEmailRequest;
import ru.edu.spbstu.request.SendTemporaryPasswordRequest;
import ru.edu.spbstu.request.SignUpRequest;
import ru.edu.spbstu.service.LoginService;

import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class
)
@AutoConfigureMockMvc
public class LoginTestIT
{
    private static final String MAIN_TEST_LOGIN = "main-login-test-login";
    private static final String MAIN_TEST_PASSWORD = "main-login-test-password";
    private static final String MAIN_TEST_MAIL = "main-login-test-mail@mail.com";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;

    @Autowired
    LoginController loginController;
    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    private static GreenMail testSmtp;

    @BeforeAll
    public static void smtpInit() {
        testSmtp = new GreenMail(ServerSetupTest.SMTP);
        testSmtp.setUser("hellotest", "hellotest");
        testSmtp.start();
    }

    @BeforeEach
    public void setupTests() {
        if (userRepository.getByLogin(MAIN_TEST_LOGIN).isEmpty()) {
            SignUpRequest request = new SignUpRequest();
            request.setLogin(MAIN_TEST_LOGIN);
            request.setPassword(MAIN_TEST_PASSWORD);
            request.setEmail(MAIN_TEST_MAIL);

            loginService.signUp(request);


        }
    }

    @Test
    public void test1() throws Exception {
        String registerLogin = "test-login";
        String registerPassword = "test-password";
        String registerMail = "test-mail@mail.com";

        SignUpRequest request = new SignUpRequest();
        request.setLogin(registerLogin);
        request.setPassword(registerPassword);
        request.setEmail(registerMail);

        Optional<UserJpa> byLogin = userRepository.getByLogin(registerLogin);
        Assertions.assertTrue(byLogin.isEmpty());

        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());

        byLogin = userRepository.getByLogin(registerLogin);
        Assertions.assertTrue(byLogin.isPresent());
        Assertions.assertEquals(registerLogin, byLogin.get().getLogin());
        Assertions.assertEquals(registerMail, byLogin.get().getEmail());
        Assertions.assertTrue(passwordEncoder.matches(registerPassword, byLogin.get().getPassword()));
    }

    @Test
    public void test2() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("email", MAIN_TEST_MAIL);

        MvcResult result = mvc.perform(get("/is_email_used")
                        .contentType(MediaType.APPLICATION_JSON).params(params))
                .andExpect(status().isOk()).andReturn();
        Assertions.assertTrue(Boolean.parseBoolean(result.getResponse().getContentAsString()));

        String uniqueMail = UUID.randomUUID().toString().substring(0, 7) + "@mail.com";
        params.clear();
        params.add("email", uniqueMail);

        result = mvc.perform(get("/is_email_used")
                        .contentType(MediaType.APPLICATION_JSON).params(params))
                .andExpect(status().isOk()).andReturn();
        Assertions.assertFalse(Boolean.parseBoolean(result.getResponse().getContentAsString()));
    }

    @Test
    public void test3() throws Exception {
        CheckEmailRequest request = new CheckEmailRequest();
        request.setLogin(MAIN_TEST_LOGIN);
        request.setEmail(UUID.randomUUID().toString().substring(0, 7) + "@mail.com");

        MvcResult result = mvc.perform(post("/check_user_email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn();
        Assertions.assertFalse(Boolean.parseBoolean(result.getResponse().getContentAsString()));

        request.setEmail(MAIN_TEST_MAIL);

        result = mvc.perform(post("/check_user_email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn();
        Assertions.assertTrue(Boolean.parseBoolean(result.getResponse().getContentAsString()));
    }

    @Test
    public void test4() throws Exception {
        String registerLogin = "tmp-pass-test-login";
        String registerPassword = "tmp-pass-test-password";
        String registerMail = "tmp-pass-test-mail@mail.com";

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setLogin(registerLogin);
        signUpRequest.setPassword(registerPassword);
        signUpRequest.setEmail(registerMail);

        mvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signUpRequest)))
                .andExpect(status().isOk());

        SendTemporaryPasswordRequest request = new SendTemporaryPasswordRequest();
        request.setLogin(registerLogin);
        request.setLanguage(Language.ENGLISH);

        mvc.perform(patch("/send-tmp-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpectAll(status().isOk()).andReturn();

        MimeMessage[] messages = testSmtp.getReceivedMessages();
        Assertions.assertEquals(1, messages.length);
        Assertions.assertEquals("Reset password", messages[0].getSubject());
        Assertions.assertEquals(1, messages[0].getAllRecipients().length);
        Assertions.assertEquals(registerMail, messages[0].getAllRecipients()[0].toString());
    }

    @Test
    public void test5() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("login", MAIN_TEST_LOGIN);

        MvcResult result = mvc.perform(get("/is_user_present")
                        .contentType(MediaType.APPLICATION_JSON).params(params))
                .andExpect(status().isOk()).andReturn();
        Assertions.assertTrue(Boolean.parseBoolean(result.getResponse().getContentAsString()));

        String uniqueLogin = UUID.randomUUID().toString().substring(0, 7);
        params.clear();
        params.add("login", uniqueLogin);

        result = mvc.perform(get("/is_user_present")
                        .contentType(MediaType.APPLICATION_JSON).params(params))
                .andExpect(status().isOk()).andReturn();
        Assertions.assertFalse(Boolean.parseBoolean(result.getResponse().getContentAsString()));
    }

    @AfterAll
    public static void cleanup() {
        testSmtp.stop();
    }
}
