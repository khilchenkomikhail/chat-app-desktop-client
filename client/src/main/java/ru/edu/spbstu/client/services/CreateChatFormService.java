package ru.edu.spbstu.client.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.ChatUser;
import ru.edu.spbstu.model.User;
import ru.edu.spbstu.request.CreateChatRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CreateChatFormService {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private CredentialsProvider prov= new BasicCredentialsProvider();

    public String getLogin() {
        return login;
    }

    private String login;


    public CredentialsProvider getCredentialsProvider()
    {
        return prov;
    }

    public void setCredentialsProvider(CredentialsProvider prov,String login)
    {
        this.prov=prov;
        this.login=login;
    }
    public List<Chat> getChats(Integer page) throws IOException {
        //getAllChats(prov, login, page).forEach(chat -> System.out.println(chat.getName()))
        return getAllChats(prov, login, page);
    }
    public void addChat(String chatName, List<String>users) throws IOException {
        int reqStatusCreateChat = createChat(prov, chatName, users, login);
       // System.out.println(reqStatusCreateChat);

        if (reqStatusCreateChat != 200) {
            throw new HttpResponseException(reqStatusCreateChat,"Error while addChat");
        }
    }
    public void addChat(String chatName) throws IOException {
        int reqStatusCreateChat = createChat(prov, chatName, Collections.emptyList(), login);
        if (reqStatusCreateChat != 200) {
            throw new HttpResponseException(reqStatusCreateChat,"Error while addChat");
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
    public ChatUser getUser(String text) throws IOException {
        return getUser(prov,text);
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


    public boolean checkUser(String username) {
        return true;
    }

    private static ChatUser getUser(CredentialsProvider provider, String login) throws IOException {

        String getChatsUrlBlueprint = "http://localhost:8080/get_chat_user?login=%s";

        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultCredentialsProvider(provider)
                .build()) {
            HttpGet httpGet = new HttpGet(String.format(getChatsUrlBlueprint, login));
            CloseableHttpResponse re = client.execute(httpGet);
            String json = EntityUtils.toString(re.getEntity());
            int code=re.getStatusLine().getStatusCode();
            {
                if(code!=200)
                {
                    //return new ChatUser();
                    throw new HttpResponseException(code,"Error while getting user");
                }
            }
            return jsonMapper.readValue(json, new TypeReference<>() {});
        }
    }
}
