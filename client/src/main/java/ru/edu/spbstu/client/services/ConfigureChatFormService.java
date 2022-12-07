package ru.edu.spbstu.client.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import ru.edu.spbstu.client.utils.AuthScheme;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.ChatUser;
import ru.edu.spbstu.request.ChatUpdateRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigureChatFormService {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private CredentialsProvider prov= new BasicCredentialsProvider();
    private String login;

    public String getLogin() {
        return login;
    }

    CredentialsProvider getCredentialsProvider()
    {
        return prov;
    }

    public void setCredentialsProvider(CredentialsProvider prov,String login)
    {
        this.prov=prov;
        this.login=login;
    }

    public List<ChatUser> getChatMembers(Chat chatToConfigure) throws IOException {

        String getChatsUrlBlueprint = "http://localhost:8080/get_chat_members?chat_id=%d";

        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(prov)
                .build()) {
            HttpGet httpGet = new HttpGet(String.format(getChatsUrlBlueprint,chatToConfigure.getId()));
            CloseableHttpResponse re = client.execute(httpGet);
            String json = EntityUtils.toString(re.getEntity());
            int code=re.getStatusLine().getStatusCode();
            if(code!=200) {
                if (code == 400) {
                    return new ArrayList<>();
                } else {
                    throw new HttpResponseException(code, "Error when get chat members!");
                }
            }
            return jsonMapper.readValue(json, new TypeReference<>() {});
        }
    }

    public void deleteChatUsers(Chat chatToConfigure,List<String>logins) throws IOException {
        ChatUpdateRequest request=new ChatUpdateRequest(chatToConfigure.getId(),logins);

        int code=0;
        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(prov)
                .build()) {
            HttpPatch post = new HttpPatch("http://localhost:8080/delete_users_from_chat");
            post.setEntity(new StringEntity(jsonMapper.writeValueAsString(request), "UTF-8"));
            post.addHeader("content-type", "application/json");
            CloseableHttpResponse re = client.execute(post);
            code=re.getStatusLine().getStatusCode();
        }


    }
    public void setChatUsersAdmins(Chat chatToConfigure,List<String>logins) throws IOException {
        ChatUpdateRequest request=new ChatUpdateRequest(chatToConfigure.getId(),logins);
        int code=0;
        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(prov)
                .build()) {
            HttpPatch post = new HttpPatch("http://localhost:8080/make_users_admins");
            post.setEntity(new StringEntity(jsonMapper.writeValueAsString(request), "UTF-8"));
            post.addHeader("content-type", "application/json");
            CloseableHttpResponse re = client.execute(post);
            code = re.getStatusLine().getStatusCode();
        }
    }
    public void addUsersToChat(Chat chatToConfigure,List<String>logins) throws IOException {

        ChatUpdateRequest request=new ChatUpdateRequest(chatToConfigure.getId(),logins);
        int code=0;
        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(prov)
                .build()) {
            HttpPatch post = new HttpPatch("http://localhost:8080/add_users_to_chat");
            post.setEntity(new StringEntity(jsonMapper.writeValueAsString(request), "UTF-8"));
            post.addHeader("content-type", "application/json");
            CloseableHttpResponse re = client.execute(post);
            code = re.getStatusLine().getStatusCode();
        }
    }

    public ArrayList<Image> getImageList(List<ChatUser> userList) {
        int size=userList.size();
        ArrayList<Image> images= new ArrayList<Image>();
        for (int i=0;i<size;i++)
        {
            var resourse=getClass().getResource("/images/dAvatar.bmp");
            var res=(getClass().getResource("/images/dAvatar.bmp")).getPath().replaceFirst("/","");
            Image temp=new Image(res);
            images.add(temp);
            //images.set(i,temp);
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
