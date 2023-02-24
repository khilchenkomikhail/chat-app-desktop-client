package IT;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.edu.spbstu.Application;
import ru.edu.spbstu.controller.ChatController;
import ru.edu.spbstu.dao.UserRepository;
import ru.edu.spbstu.exception.InvalidRequestParameter;
import ru.edu.spbstu.exception.ResourceNotFound;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.ChatUser;
import ru.edu.spbstu.model.Language;
import ru.edu.spbstu.request.CreateChatRequest;
import ru.edu.spbstu.request.SignUpRequest;
import ru.edu.spbstu.service.ChatService;
import ru.edu.spbstu.service.LoginService;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class
)
@AutoConfigureMockMvc
public class ChatITFirstPart {
    private static final String MAIN_BASIC_AUTH_HEADER_KEY = "Authorization";
    private static final String MAIN_TEST_LOGIN = "main-chat-test-login";
    private static final String MAIN_TEST_PASSWORD = "main-chat-test-password";
    private static final String MAIN_TEST_MAIL = "main-chat-test-mail@mail.com";
    private static final String MAIN_BASIC_AUTH_HEADER_VALUE = " Basic bWFpbi1jaGF0LXRlc3QtbG9naW46bWFpbi1jaGF0LXRlc3QtcGFzc3dvcmQ=";

    private static final String SECONDARY_TEST_LOGIN = "secondary-chat-test-login";
    private static final String SECONDARY_TEST_PASSWORD = "secondary-chat-test-password";
    private static final String SECONDARY_TEST_MAIL = "secondary-chat-test-mail@mail.com";

    private static final String MAIN_TEST_CHAT_NAME = "main-chat-test-name";
    private static Long MAIN_TEST_CHAT_ID = -1L;
    private static final String SECONDARY_TEST_CHAT_NAME = "secondary-chat-test-nam";
    private static Long SECONDARY_TEST_CHAT_ID = -1L;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;
    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ChatController chatController;
    @Autowired
    ChatService chatService;

    @BeforeEach
    public void setupTests() {
        if (userRepository.getByLogin(MAIN_TEST_LOGIN).isEmpty()) {
            SignUpRequest request = new SignUpRequest();
            request.setLogin(MAIN_TEST_LOGIN);
            request.setPassword(MAIN_TEST_PASSWORD);
            request.setEmail(MAIN_TEST_MAIL);
            loginService.signUp(request);

            request = new SignUpRequest();
            request.setLogin(SECONDARY_TEST_LOGIN);
            request.setPassword(SECONDARY_TEST_PASSWORD);
            request.setEmail(SECONDARY_TEST_MAIL);
            loginService.signUp(request);

            CreateChatRequest chatRequest = new CreateChatRequest();
            chatRequest.setChat_name(MAIN_TEST_CHAT_NAME);
            chatRequest.setUser_logins(Collections.singletonList(SECONDARY_TEST_LOGIN));
            chatRequest.setLanguage(Language.ENGLISH);
            chatRequest.setAdmin_login(MAIN_TEST_LOGIN);
            chatService.createChat(chatRequest);

            chatRequest = new CreateChatRequest();
            chatRequest.setChat_name(SECONDARY_TEST_CHAT_NAME);
            chatRequest.setUser_logins(Collections.singletonList(SECONDARY_TEST_LOGIN));
            chatRequest.setLanguage(Language.ENGLISH);
            chatRequest.setAdmin_login(MAIN_TEST_LOGIN);
            chatService.createChat(chatRequest);

            List<Chat> chats = chatService.getChats(MAIN_TEST_LOGIN, 1);
            Chat chat1 = chats.stream()
                    .filter(chat -> chat.getName().equals(MAIN_TEST_CHAT_NAME)).findFirst().get();
            MAIN_TEST_CHAT_ID = chat1.getId();
            Chat chat2 = chats.stream()
                    .filter(chat -> chat.getName().equals(SECONDARY_TEST_CHAT_NAME)).findFirst().get();
            SECONDARY_TEST_CHAT_ID = chat2.getId();
        }
    }

    @Test
    public void test6() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("login", MAIN_TEST_LOGIN);
        params.add("page_number", String.valueOf(1));

        MvcResult result = mvc.perform(get("/get_chats")
                        .header(MAIN_BASIC_AUTH_HEADER_KEY, MAIN_BASIC_AUTH_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON).params(params))
                .andExpect(status().isOk()).andReturn();

        List<Chat> chats = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

        Assertions.assertEquals(2, chats.size());
        Assertions.assertTrue(chats.stream().anyMatch(chat -> chat.getName().equals(MAIN_TEST_CHAT_NAME)));
        Assertions.assertTrue(chats.stream().anyMatch(chat -> chat.getName().equals(SECONDARY_TEST_CHAT_NAME)));
    }

    @Test
    public void test7() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("login", MAIN_TEST_LOGIN + "error");
        params.add("page_number", String.valueOf(1));

        MvcResult result = mvc.perform(get("/get_chats")
                        .header(MAIN_BASIC_AUTH_HEADER_KEY, MAIN_BASIC_AUTH_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON).params(params))
                .andExpect(status().isNotFound()).andReturn();

        ResourceNotFound notFoundException = (ResourceNotFound) result.getResolvedException();
        Assertions.assertNotNull(notFoundException);
        Assertions.assertEquals("User with login '"
                + MAIN_TEST_LOGIN
                + "error"
                + "' was not found", notFoundException.getMessage());
    }

    @Test
    public void test8() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("login", MAIN_TEST_LOGIN);
        params.add("begin", "");
        params.add("page_number", String.valueOf(1));

        MvcResult result = mvc.perform(get("/get_chats_by_search")
                        .header(MAIN_BASIC_AUTH_HEADER_KEY, MAIN_BASIC_AUTH_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON).params(params))
                .andExpect(status().isOk()).andReturn();

        List<Chat> chats = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

        Assertions.assertEquals(2, chats.size());
        Assertions.assertTrue(chats.stream().anyMatch(chat -> chat.getName().equals(MAIN_TEST_CHAT_NAME)));
        Assertions.assertTrue(chats.stream().anyMatch(chat -> chat.getName().equals(SECONDARY_TEST_CHAT_NAME)));

        params = new LinkedMultiValueMap<>();
        params.add("login", MAIN_TEST_LOGIN);
        params.add("begin", "main");
        params.add("page_number", String.valueOf(1));

        result = mvc.perform(get("/get_chats_by_search")
                        .header(MAIN_BASIC_AUTH_HEADER_KEY, MAIN_BASIC_AUTH_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON).params(params))
                .andExpect(status().isOk()).andReturn();

        chats = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

        Assertions.assertEquals(1, chats.size());
        Assertions.assertTrue(chats.stream().anyMatch(chat -> chat.getName().equals(MAIN_TEST_CHAT_NAME)));

        params = new LinkedMultiValueMap<>();
        params.add("login", MAIN_TEST_LOGIN);
        params.add("begin", "invalid_begin");
        params.add("page_number", String.valueOf(1));

        result = mvc.perform(get("/get_chats_by_search")
                        .header(MAIN_BASIC_AUTH_HEADER_KEY, MAIN_BASIC_AUTH_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON).params(params))
                .andExpect(status().isBadRequest()).andReturn();

        InvalidRequestParameter invalidRequestParameter = (InvalidRequestParameter) result.getResolvedException();
        Assertions.assertNotNull(invalidRequestParameter);
        Assertions.assertEquals("There are no chats on page 1", invalidRequestParameter.getMessage());
    }

    @Test
    public void test9() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("chat_id", String.valueOf(MAIN_TEST_CHAT_ID));

        MvcResult result = mvc.perform(get("/get_chat_members")
                        .header(MAIN_BASIC_AUTH_HEADER_KEY, MAIN_BASIC_AUTH_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON).params(params))
                .andExpect(status().isOk()).andReturn();

        List<ChatUser> members = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

        Assertions.assertEquals(2, members.size());
        Assertions.assertTrue(members.stream().anyMatch(el -> el.getLogin().equals(MAIN_TEST_LOGIN)));
        Assertions.assertTrue(members.stream().anyMatch(el -> el.getLogin().equals(SECONDARY_TEST_LOGIN)));
    }

    @Test
    public void test10() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        Long uniqueId = new Random().nextLong();
        while (uniqueId.equals(MAIN_TEST_CHAT_ID) || uniqueId.equals(SECONDARY_TEST_CHAT_ID)) {
            uniqueId = new Random().nextLong();
        }

        params.add("chat_id", String.valueOf(uniqueId));

        MvcResult result = mvc.perform(get("/get_chat_members")
                        .header(MAIN_BASIC_AUTH_HEADER_KEY, MAIN_BASIC_AUTH_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON).params(params))
                .andExpect(status().isNotFound()).andReturn();

        ResourceNotFound notFoundException = (ResourceNotFound) result.getResolvedException();
        Assertions.assertNotNull(notFoundException);
        Assertions.assertEquals("Chat with id '"
                + uniqueId
                + "' was not found", notFoundException.getMessage());
    }
}
