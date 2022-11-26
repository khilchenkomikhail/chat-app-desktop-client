package ru.edu.spbstu.client.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import ru.edu.spbstu.request.CreateChatRequest;
import ru.edu.spbstu.request.SignUpRequest;
import ru.edu.spbstu.model.Chat;


import java.io.IOException;
import java.util.Collections;
import java.util.List;



public class LogInService {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private CredentialsProvider prov= new BasicCredentialsProvider();//TODo возможно это потребуется передавать в следующие формы
    CredentialsProvider getProvider()
    {
        return prov;
    }

    public  void register(String login,String password,String email) throws IOException {
        String image = "image "+ login;

        int regStatus = register(login, password, email, image);
        System.out.println(regStatus);

        if (regStatus != 200) {
            throw new HttpResponseException(regStatus,"Error while register");
        }


        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
        prov.setCredentials(AuthScope.ANY, credentials);
    }
    public void logIn(String login,String password) throws IOException {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
        prov.setCredentials(AuthScope.ANY, credentials);

        int reqStatusCreateChat = createChat(prov, "chat3", Collections.emptyList(), login);
        System.out.println(reqStatusCreateChat);

        if (reqStatusCreateChat != 200) {

            throw new HttpResponseException(reqStatusCreateChat,"Error while addChat");
        }
        //TODO в init() у нас спокойно добавляется новый чат, но если сделать это здесь возникнет ошибка авторизации 401

        getAllChats(prov, login, 1).forEach(chat -> System.out.println(chat.getName()));
    }
    public LogInService()
    {

    }
    public void init() throws IOException {

        String login = "login";
        String password = "password";
        String email = "email@email.com";
        String image = "image"; // must be changed later

        int regStatus = register(login, password, email, image);
        System.out.println(regStatus); // is status is 200 OK then everything is fine

        if (regStatus != 200) {
            throw new HttpResponseException(regStatus,"Error while register");
        }

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
        provider.setCredentials(AuthScope.ANY, credentials);

        int reqStatusCreateChat = createChat(provider, "chat1", Collections.emptyList(), login);
        System.out.println(reqStatusCreateChat);

        if (reqStatusCreateChat != 200) {
           // throw new RuntimeException();
            throw new HttpResponseException(regStatus,"Error while addChat");
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