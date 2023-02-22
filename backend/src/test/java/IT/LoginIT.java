package IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.edu.spbstu.Application;
import ru.edu.spbstu.controller.LoginController;
import ru.edu.spbstu.dao.UserRepository;
import ru.edu.spbstu.model.jpa.UserJpa;
import ru.edu.spbstu.request.SignUpRequest;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class
)
@AutoConfigureMockMvc
public class LoginIT
{
    @Autowired
    private MockMvc mvc;

    @Autowired
    LoginController loginController;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

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
}
