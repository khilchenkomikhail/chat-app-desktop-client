package ru.edu.spbstu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.request.CreateChatRequest;
import ru.edu.spbstu.request.SignUpRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ConsoleTestClient {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        String login = "login";
        String password = "password";
        String email = "email@email.com";
        String image = "image"; // must be changed later

        int regStatus = register(login, password, email, image);
        System.out.println(regStatus); // is status is 200 OK then everything is fine

        if (regStatus != 200) {
            throw new RuntimeException();
        }

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
        provider.setCredentials(AuthScope.ANY, credentials);

        int reqStatusCreateChat = createChat(provider, "chat1", Collections.singletonList(login), login);
        System.out.println(reqStatusCreateChat);

        if (reqStatusCreateChat != 200) {
            throw new RuntimeException();
        }

        getAllChats(provider, login, 1).forEach(chat -> System.out.println(chat.getName()));
    }

    private static int register(String login, String password, String email, String image) throws IOException {
        SignUpRequest signUpRequest = new SignUpRequest(login, password, email, image);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost signUpReq = new HttpPost("http://localhost:8080/sign-up");
            signUpReq.addHeader("content-type", "application/json");
            signUpReq.setEntity(new StringEntity(jsonMapper.writeValueAsString(signUpRequest)));
            return client.execute(signUpReq).getStatusLine().getStatusCode();
        }
    }

    private  static List<Chat> getAllChats(CredentialsProvider provider, String login, Integer page) throws IOException {

        String getChatsUrlBlueprint = "http://localhost:8080/get_chats?login=%s&page_number=%d";

        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultCredentialsProvider(provider)
                .build()) {
            HttpGet httpGet = new HttpGet(String.format(getChatsUrlBlueprint, login, page));
            CloseableHttpResponse re = client.execute(httpGet);
            String json = EntityUtils.toString(re.getEntity());
            return jsonMapper.readValue(json, new TypeReference<>() {});
        }
    }

    private static int createChat(CredentialsProvider provider, String chatName, List<String> users, String admin) throws IOException {
        CreateChatRequest request = new CreateChatRequest();
        request.setAdmin_login(admin);
        request.setChat_name(chatName);
        request.setUser_logins(users);

        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultCredentialsProvider(provider)
                .build()) {
            HttpPost post = new HttpPost("http://localhost:8080/create_chat");
            post.setEntity(new StringEntity(jsonMapper.writeValueAsString(request)));
            post.addHeader("content-type", "application/json");
            CloseableHttpResponse re = client.execute(post);
            return re.getStatusLine().getStatusCode();
        }
    }
}
