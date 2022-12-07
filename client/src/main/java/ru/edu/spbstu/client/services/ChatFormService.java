package ru.edu.spbstu.client.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import ru.edu.spbstu.client.utils.AuthScheme;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.ChatUser;
import ru.edu.spbstu.model.Message;
import ru.edu.spbstu.request.ChatUpdateRequest;
import ru.edu.spbstu.request.CreateChatRequest;
import ru.edu.spbstu.request.EditMessageRequest;
import ru.edu.spbstu.request.SendMessageRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatFormService {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private CredentialsProvider prov = new BasicCredentialsProvider();
    private String login;


    public CredentialsProvider getCredentialsProvider()
    {
        return prov;
    }
    public String getLogin()
    {
        return login;
    }

    public void setCredentialsProvider(CredentialsProvider prov,String login)
    {
        this.prov=prov;
        this.login=login;
    }
    public List<Chat> getChats(Integer page) throws IOException {
        return getAllChats(prov, login, page);
    }
    public void addChat(String chatName, List<String>users) throws IOException {
        int reqStatusCreateChat = createChat(prov, chatName, users, login);
        System.out.println(reqStatusCreateChat);

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


    public List<Message> getMessages(Long chat_id, int page) throws IOException {
        return getMessages(prov, chat_id, (long) page);
    }
    public void sendMessage(Long chatId,String message) throws IOException {
        int reqStatusCreateChat = sendMessage(prov,login,chatId,message);
        if (reqStatusCreateChat != 200) {
            throw new HttpResponseException(reqStatusCreateChat,"Error while send message");
        }
    }
    public void deleteMessage(Long message_id) throws IOException {
        int reqStatusCreateChat = deleteMessage(prov,message_id);
        if (reqStatusCreateChat != 200) {
            throw new HttpResponseException(reqStatusCreateChat,"Error while delete message");
        }
    }

    public void editMessage(Long message_id,String Newcontents) throws IOException {
        int reqStatusCreateChat = editMessage(prov,message_id,Newcontents);
        if (reqStatusCreateChat != 200) {
            throw new HttpResponseException(reqStatusCreateChat,"Error while delete message");
        }
    }
    public void leaveChat(Long chatId, String login) throws IOException {

        int reqStatusCreateChat = leaveChat(prov,chatId,Collections.singletonList(login));
        if (reqStatusCreateChat != 200) {
            throw new HttpResponseException(reqStatusCreateChat,"Error while send message");
        }

    }

    private static int leaveChat(CredentialsProvider provider, Long chatId, List<String> login) throws IOException {
        ChatUpdateRequest request = new ChatUpdateRequest(chatId,login);


        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(provider)
                .build()) {
            HttpPatch post = new HttpPatch("http://localhost:8080/delete_users_from_chat");
            post.setEntity(new StringEntity(jsonMapper.writeValueAsString(request), "UTF-8"));
            post.addHeader("content-type", "application/json");
            CloseableHttpResponse re = client.execute(post);
            return re.getStatusLine().getStatusCode();
        }
    }


    private static List<Chat> getAllChats(CredentialsProvider provider, String login, Integer page) throws IOException {

        String getChatsUrlBlueprint = "http://localhost:8080/get_chats?login=%s&page_number=%d";

        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(provider)
                .build()) {
            HttpGet httpGet = new HttpGet(String.format(getChatsUrlBlueprint, login, page));
            CloseableHttpResponse re = client.execute(httpGet);
            String json = EntityUtils.toString(re.getEntity());
            if(re.getStatusLine().getStatusCode()!=200)
            {
                if(re.getStatusLine().getStatusCode()==400) {
                    return new ArrayList<>(0);
                }
                throw new HttpResponseException(re.getStatusLine().getStatusCode(),"HttpCode");
            }
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
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(provider)
                .build()) {
            HttpPost post = new HttpPost("http://localhost:8080/create_chat");
            post.setEntity(new StringEntity(jsonMapper.writeValueAsString(request), "UTF-8"));
            post.addHeader("content-type", "application/json");
            CloseableHttpResponse re = client.execute(post);
            return re.getStatusLine().getStatusCode();
        }
    }


    public List<Chat> find(String name) {
        List<Chat>res=Collections.emptyList();

        return res;
    }


    List<Message> getMessages(CredentialsProvider provider, Long chatId, Long page) throws IOException {

        String getMessagesUrlBlueprint = "http://localhost:8080/get_messages?chat_id=%d&page_number=%d";
        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(provider)
                .build()) {

            HttpGet httpGet = new HttpGet(String.format( getMessagesUrlBlueprint, chatId, page));
            CloseableHttpResponse re = client.execute(httpGet);
            String json = EntityUtils.toString(re.getEntity());
            int code=re.getStatusLine().getStatusCode();
            if(code==400)
            {
                return new ArrayList<>();
            }
            return jsonMapper.readValue(json, new TypeReference<>() {});
        }
    }

    private static int sendMessage(CredentialsProvider provider, String login,Long chat_id,String message) throws IOException {
        SendMessageRequest request=new SendMessageRequest(login,login,chat_id,message);
        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(provider)
                .build()) {
            HttpPost post = new HttpPost("http://localhost:8080/send_message");
            post.setEntity(new StringEntity(jsonMapper.writeValueAsString(request), "UTF-8"));
            post.addHeader("content-type", "application/json");
            CloseableHttpResponse re = client.execute(post);
            return re.getStatusLine().getStatusCode();
        }
    }
    private static int deleteMessage(CredentialsProvider provider, Long message_id) throws IOException {

        String getMessagesUrlBlueprint = "http://localhost:8080/delete_message?message_id=%d";
        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(provider)
                .build()) {

            HttpPatch httpGet = new HttpPatch(String.format( getMessagesUrlBlueprint, message_id));
            CloseableHttpResponse re = client.execute(httpGet);
            String json = EntityUtils.toString(re.getEntity());
            int code=re.getStatusLine().getStatusCode();

            return re.getStatusLine().getStatusCode();
        }
    }
    private static int forwardMessage(CredentialsProvider provider, Long message_id, String login,String chat_id) throws IOException {
        String getMessagesUrlBlueprint = "http://localhost:8080/forward_message?message_id=%d?sender_login=%s?chat_id=%d";
        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(provider)
                .build()) {

            HttpPost httpGet = new HttpPost(String.format( getMessagesUrlBlueprint, message_id,login,chat_id));
            CloseableHttpResponse re = client.execute(httpGet);
            String json = EntityUtils.toString(re.getEntity());
            int code=re.getStatusLine().getStatusCode();

            return re.getStatusLine().getStatusCode();
        }
    }
    private static int editMessage(CredentialsProvider provider,Long message_id,String message) throws IOException {
        EditMessageRequest request=new EditMessageRequest(message_id,message);
        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(provider)
                .build()) {
            HttpPatch post = new HttpPatch("http://localhost:8080/edit_message");
            post.setEntity(new StringEntity(jsonMapper.writeValueAsString(request), "UTF-8"));
            post.addHeader("content-type", "application/json");
            CloseableHttpResponse re = client.execute(post);
            return re.getStatusLine().getStatusCode();
        }
    }


}
