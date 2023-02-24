package IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import ru.edu.spbstu.Application;
import ru.edu.spbstu.dao.ChatRepository;
import ru.edu.spbstu.dao.UserChatDetailsRepository;
import ru.edu.spbstu.dao.UserRepository;
import ru.edu.spbstu.exception.ResourceNotFound;
import ru.edu.spbstu.model.ChatRole;
import ru.edu.spbstu.model.Language;
import ru.edu.spbstu.model.jpa.ChatJpa;
import ru.edu.spbstu.model.jpa.UserChatDetailsJpa;
import ru.edu.spbstu.model.jpa.UserJpa;
import ru.edu.spbstu.request.ChatUpdateRequest;
import ru.edu.spbstu.request.CreateChatRequest;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class
)
@AutoConfigureMockMvc(addFilters = false)
public class ChatSecondPartTestIT {
    @Autowired
    private MockMvc mvc;

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    UserChatDetailsRepository userChatDetailsRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void test11() throws Exception {
        String chatName = "Name11";
        String adminLogin = "admin11";
        String userLogin = "user11";
        UserJpa admin = saveUser(adminLogin);
        UserJpa user = saveUser(userLogin);

        CreateChatRequest request = new CreateChatRequest();
        request.setChat_name(chatName);
        request.setAdmin_login(adminLogin);
        request.setUser_logins(List.of(userLogin));
        request.setLanguage(Language.RUSSIAN);

        Assertions.assertEquals(0, chatRepository.findAllByName(chatName).size());

        mvc.perform(post("/create_chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());

        List<ChatJpa> chats = chatRepository.findAllByName(chatName);
        Assertions.assertEquals(1, chats.size());

        List<UserChatDetailsJpa> userChatDetailsJpaList = userChatDetailsRepository.getChatMembers(chats.get(0).getId());
        Assertions.assertEquals(2, userChatDetailsJpaList.size());
        Assertions.assertEquals(user.getId(), userChatDetailsJpaList.get(0).getUser().getId());
        Assertions.assertEquals(ChatRole.USER, userChatDetailsJpaList.get(0).getChatRole());
        Assertions.assertEquals(admin.getId(), userChatDetailsJpaList.get(1).getUser().getId());
        Assertions.assertEquals(ChatRole.ADMIN, userChatDetailsJpaList.get(1).getChatRole());
    }

    @Test
    public void test12() throws Exception {
        String chatName = "Name12";
        String adminLogin = "admin12";
        String userLogin = "user12";
        saveUser(adminLogin);

        CreateChatRequest request = new CreateChatRequest();
        request.setChat_name(chatName);
        request.setAdmin_login(adminLogin);
        request.setUser_logins(List.of(userLogin));
        request.setLanguage(Language.ENGLISH);

        Assertions.assertEquals(0, chatRepository.findAllByName(chatName).size());

        MvcResult result = mvc.perform(post("/create_chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound()).andReturn();

        Assertions.assertEquals(0, chatRepository.findAllByName(chatName).size());

        ResourceNotFound notFoundException = (ResourceNotFound) result.getResolvedException();
        Assertions.assertNotNull(notFoundException);
        Assertions.assertEquals("User with login '" + userLogin + "' was not found", notFoundException.getMessage());
    }

    @Test
    public void test13() throws Exception {
        String chatName = "Name13";
        String adminLogin = "admin13";
        String userLogin = "user13";
        UserJpa admin = saveUser(adminLogin);
        UserJpa user = saveUser(userLogin);
        ChatJpa chat = saveChat(chatName, admin, user);

        Assertions.assertFalse(chatRepository.findByIdIs(chat.getId()).isEmpty());
        Assertions.assertEquals(2, userChatDetailsRepository.getChatMembers(chat.getId()).size());

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("chat_id", chat.getId().toString());
        mvc.perform(delete("/delete_chat").params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertTrue(chatRepository.findByIdIs(chat.getId()).isEmpty());
        Assertions.assertEquals(0, userChatDetailsRepository.getChatMembers(chat.getId()).size());
    }

    @Test
    public void test14() throws Exception {
        Long chatId = 0L;
        Assertions.assertTrue(chatRepository.findByIdIs(chatId).isEmpty());

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("chat_id", chatId.toString());
        MvcResult result = mvc.perform(delete("/delete_chat").params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();

        Assertions.assertTrue(chatRepository.findByIdIs(chatId).isEmpty());

        ResourceNotFound notFoundException = (ResourceNotFound) result.getResolvedException();
        Assertions.assertNotNull(notFoundException);
        Assertions.assertEquals("Chat with id '" + chatId + "' was not found", notFoundException.getMessage());
    }

    @Test
    public void test15() throws Exception {
        String chatName = "Name15";
        String adminLogin = "admin15";
        String userLogin = "user15";
        UserJpa admin = saveUser(adminLogin);
        UserJpa user = saveUser(userLogin);
        ChatJpa chat = saveChat(chatName, admin, user);

        ChatUpdateRequest request = new ChatUpdateRequest();
        request.setChat_id(chat.getId());
        request.setUser_logins(List.of(userLogin));

        Assertions.assertFalse(chatRepository.findByIdIs(chat.getId()).isEmpty());

        List<UserChatDetailsJpa> userChatDetailsJpaList = userChatDetailsRepository.getChatMembers(chat.getId());
        Assertions.assertEquals(2, userChatDetailsJpaList.size());

        Assertions.assertEquals(user.getId(), userChatDetailsJpaList.get(0).getUser().getId());
        Assertions.assertEquals(admin.getId(), userChatDetailsJpaList.get(1).getUser().getId());

        mvc.perform(patch("/delete_users_from_chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());

        Assertions.assertFalse(chatRepository.findByIdIs(chat.getId()).isEmpty());

        userChatDetailsJpaList = userChatDetailsRepository.getChatMembers(chat.getId());
        Assertions.assertEquals(1, userChatDetailsJpaList.size());
        Assertions.assertEquals(admin.getId(), userChatDetailsJpaList.get(0).getUser().getId());
    }

    @Test
    public void test16() throws Exception {
        Long chatId = 0L;
        Assertions.assertTrue(chatRepository.findByIdIs(chatId).isEmpty());

        ChatUpdateRequest request = new ChatUpdateRequest();
        request.setChat_id(chatId);

        MvcResult result = mvc.perform(patch("/delete_users_from_chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound()).andReturn();

        Assertions.assertTrue(chatRepository.findByIdIs(chatId).isEmpty());

        ResourceNotFound notFoundException = (ResourceNotFound) result.getResolvedException();
        Assertions.assertNotNull(notFoundException);
        Assertions.assertEquals("Chat with id '" + chatId + "' was not found", notFoundException.getMessage());
    }

    @Test
    public void test17() throws Exception {
        String chatName = "Name17";
        String adminLogin = "admin17";
        String userLogin = "user17";
        UserJpa admin = saveUser(adminLogin);
        ChatJpa chat = saveChat(chatName, admin, null);

        ChatUpdateRequest request = new ChatUpdateRequest();
        request.setChat_id(chat.getId());
        request.setUser_logins(List.of(userLogin));

        Assertions.assertFalse(chatRepository.findByIdIs(chat.getId()).isEmpty());
        List<UserChatDetailsJpa> userChatDetailsJpaList = userChatDetailsRepository.getChatMembers(chat.getId());
        Assertions.assertEquals(1, userChatDetailsJpaList.size());
        Assertions.assertEquals(admin.getId(), userChatDetailsJpaList.get(0).getUser().getId());

        MvcResult result = mvc.perform(patch("/delete_users_from_chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound()).andReturn();

        Assertions.assertFalse(chatRepository.findByIdIs(chat.getId()).isEmpty());

        userChatDetailsJpaList = userChatDetailsRepository.getChatMembers(chat.getId());
        Assertions.assertEquals(1, userChatDetailsJpaList.size());
        Assertions.assertEquals(admin.getId(), userChatDetailsJpaList.get(0).getUser().getId());

        ResourceNotFound notFoundException = (ResourceNotFound) result.getResolvedException();
        Assertions.assertNotNull(notFoundException);
        Assertions.assertEquals("User with login '" + userLogin + "' was not found", notFoundException.getMessage());
    }

    @Test
    public void test18() throws Exception {
        String chatName = "Name18";
        String adminLogin = "admin18";
        String userLogin = "user18";
        UserJpa user = saveUser(userLogin);
        UserJpa admin = saveUser(adminLogin);
        ChatJpa chat = saveChat(chatName, admin, null);

        ChatUpdateRequest request = new ChatUpdateRequest();
        request.setChat_id(chat.getId());
        request.setUser_logins(List.of(userLogin));

        Assertions.assertFalse(chatRepository.findByIdIs(chat.getId()).isEmpty());
        List<UserChatDetailsJpa> userChatDetailsJpaList = userChatDetailsRepository.getChatMembers(chat.getId());
        Assertions.assertEquals(1, userChatDetailsJpaList.size());
        Assertions.assertEquals(admin.getId(), userChatDetailsJpaList.get(0).getUser().getId());

        mvc.perform(patch("/add_users_to_chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());

        Assertions.assertFalse(chatRepository.findByIdIs(chat.getId()).isEmpty());

        userChatDetailsJpaList = userChatDetailsRepository.getChatMembers(chat.getId());
        Assertions.assertEquals(2, userChatDetailsJpaList.size());
        Assertions.assertEquals(admin.getId(), userChatDetailsJpaList.get(0).getUser().getId());
        Assertions.assertEquals(user.getId(), userChatDetailsJpaList.get(1).getUser().getId());
    }

    @Test
    public void test19() throws Exception {
        Long chatId = 0L;
        Assertions.assertTrue(chatRepository.findByIdIs(chatId).isEmpty());

        ChatUpdateRequest request = new ChatUpdateRequest();
        request.setChat_id(chatId);

        MvcResult result = mvc.perform(patch("/add_users_to_chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound()).andReturn();

        Assertions.assertTrue(chatRepository.findByIdIs(chatId).isEmpty());

        ResourceNotFound notFoundException = (ResourceNotFound) result.getResolvedException();
        Assertions.assertNotNull(notFoundException);
        Assertions.assertEquals("Chat with id '" + chatId + "' was not found", notFoundException.getMessage());
    }

    @Test
    public void test20() throws Exception {
        String chatName = "Name20";
        String adminLogin = "admin20";
        String userLogin = "user20";
        UserJpa admin = saveUser(adminLogin);
        ChatJpa chat = saveChat(chatName, admin, null);

        ChatUpdateRequest request = new ChatUpdateRequest();
        request.setChat_id(chat.getId());
        request.setUser_logins(List.of(userLogin));

        Assertions.assertFalse(chatRepository.findByIdIs(chat.getId()).isEmpty());
        List<UserChatDetailsJpa> userChatDetailsJpaList = userChatDetailsRepository.getChatMembers(chat.getId());
        Assertions.assertEquals(1, userChatDetailsJpaList.size());
        Assertions.assertEquals(admin.getId(), userChatDetailsJpaList.get(0).getUser().getId());

        MvcResult result = mvc.perform(patch("/add_users_to_chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound()).andReturn();

        Assertions.assertFalse(chatRepository.findByIdIs(chat.getId()).isEmpty());

        userChatDetailsJpaList = userChatDetailsRepository.getChatMembers(chat.getId());
        Assertions.assertEquals(1, userChatDetailsJpaList.size());
        Assertions.assertEquals(admin.getId(), userChatDetailsJpaList.get(0).getUser().getId());

        ResourceNotFound notFoundException = (ResourceNotFound) result.getResolvedException();
        Assertions.assertNotNull(notFoundException);
        Assertions.assertEquals("User with login '" + userLogin + "' was not found", notFoundException.getMessage());
    }

    private UserJpa saveUser(String login) {
        UserJpa user = new UserJpa();
        user.setLogin(login);
        return userRepository.save(user);
    }

    private ChatJpa saveChat(String name, UserJpa admin, UserJpa user) {
        ChatJpa chat = new ChatJpa();
        chat.setName(name);
        chat = chatRepository.save(chat);
        if (user != null) {
            UserChatDetailsJpa userChatDetailsJpa1 = new UserChatDetailsJpa();
            userChatDetailsJpa1.setChat(chat);
            userChatDetailsJpa1.setUser(user);
            userChatDetailsJpa1.setChatRole(ChatRole.USER);
            userChatDetailsRepository.save(userChatDetailsJpa1);
        }
        UserChatDetailsJpa userChatDetailsJpa2 = new UserChatDetailsJpa();
        userChatDetailsJpa2.setChat(chat);
        userChatDetailsJpa2.setUser(admin);
        userChatDetailsJpa2.setChatRole(ChatRole.ADMIN);
        userChatDetailsRepository.save(userChatDetailsJpa2);
        return chat;
    }
}
