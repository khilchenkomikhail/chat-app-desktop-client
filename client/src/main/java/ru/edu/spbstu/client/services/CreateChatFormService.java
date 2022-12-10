package ru.edu.spbstu.client.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import ru.edu.spbstu.client.utils.HttpClientFactory;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.ChatUser;
import ru.edu.spbstu.request.CreateChatRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateChatFormService {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    public String getLogin() {
        return login;
    }
    private String login;

    public void setLogin(String login)
    {
        this.login=login;
    }

    public void addChat(String chatName, List<String>users) throws IOException {
        int reqStatusCreateChat = createChat(chatName, users, login);
        if (reqStatusCreateChat != 200) {
            throw new HttpResponseException(reqStatusCreateChat,"Error while addChat");
        }
    }
    public void addChat(String chatName) throws IOException {
        int reqStatusCreateChat = createChat(chatName, Collections.emptyList(), login);
        if (reqStatusCreateChat != 200) {
            throw new HttpResponseException(reqStatusCreateChat,"Error while addChat");
        }
    }

    private  static List<Chat> getAllChats(CredentialsProvider provider, String login, Integer page) throws IOException {

        String getChatsUrlBlueprint = "http://localhost:8080/get_chats?login=%s&page_number=%d";

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpGet httpGet = new HttpGet(String.format(getChatsUrlBlueprint, login, page));
        HttpResponse re = client.execute(httpGet);
        String json = EntityUtils.toString(re.getEntity());
        return jsonMapper.readValue(json, new TypeReference<>() {});
    }

    private static int createChat(String chatName, List<String> users, String admin) throws IOException {
        CreateChatRequest request = new CreateChatRequest();
        request.setAdmin_login(admin);
        request.setChat_name(chatName);
        request.setUser_logins(users);

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpPost post = new HttpPost("http://localhost:8080/create_chat");
        post.setEntity(new StringEntity(jsonMapper.writeValueAsString(request), "UTF-8"));
        post.addHeader("content-type", "application/json");
        HttpResponse re = client.execute(post);
        return re.getStatusLine().getStatusCode();
    }

    public boolean checkUser(String username) {
        return true;
    }

    public ArrayList<Image> getImageList(List<ChatUser> userList) {
        int size=userList.size();
        ArrayList<Image> images= new ArrayList<Image>();
        for (int i=0;i<size;i++)
        {
            var res=(getClass().getResource("/images/dAvatar.bmp")).getPath().replaceFirst("/","");
            Image temp=new Image(res);
            images.add(temp);
        }
        return  images;
    }
    public Image getImage(ChatUser userList) {
        Image image;
        var res=(getClass().getResource("/images/dAvatar.bmp")).getPath().replaceFirst("/","");
        image=new Image(res);

        return  image;
    }
    public Boolean isUserPresent(String login) throws IOException {
        String getChatsUrlBlueprint = "http://localhost:8080/is_user_present?login=%s";

        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .build()) {
            HttpGet httpGet = new HttpGet(String.format(getChatsUrlBlueprint, login));
            CloseableHttpResponse re = client.execute(httpGet);
            String json = EntityUtils.toString(re.getEntity());
            return jsonMapper.readValue(json, new TypeReference<>() {});
        }
    }
}
