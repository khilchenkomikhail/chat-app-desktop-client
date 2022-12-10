package ru.edu.spbstu.client.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import ru.edu.spbstu.client.utils.HttpClientFactory;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.ChatUser;
import ru.edu.spbstu.model.Message;
import ru.edu.spbstu.request.ChatUpdateRequest;
import ru.edu.spbstu.request.EditMessageRequest;
import ru.edu.spbstu.request.SendMessageRequest;

import java.io.IOException;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ChatFormService {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private String login;

    public String getLogin()
    {
        return login;
    }

    public void setCredentialsProvider(String login)
    {
        this.login=login;
    }
    public List<Chat> getChats(Integer page) throws IOException {
        String getChatsUrlBlueprint = "http://localhost:8080/get_chats?login=%s&page_number=%d";

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpGet httpGet = new HttpGet(String.format(getChatsUrlBlueprint, login, page));
        HttpResponse re = client.execute(httpGet);
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


    public List<Message> getMessages(Long chat_id, int page) throws IOException {
        String getMessagesUrlBlueprint = "http://localhost:8080/get_messages?chat_id=%d&page_number=%d";

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpGet httpGet = new HttpGet(String.format(getMessagesUrlBlueprint, chat_id, page));
        HttpResponse re = client.execute(httpGet);
        String json = EntityUtils.toString(re.getEntity());
        int code = re.getStatusLine().getStatusCode();
        if (code == 400) {
            return new ArrayList<>();
        }
        return jsonMapper.readValue(json, new TypeReference<>() {
        });
    }
    public Message makeMessage(Long idMessage,Long chatId, String messageContent)
    {
        Message message = new Message();
        message.setId(idMessage);
        message.setDate(new Date());
        message.setChat_id(chatId);
        message.setAuthor_login(login);
        message.setSender_login(login);
        message.setContent(messageContent);
        message.setIs_deleted(false);
        message.setIs_edited(false);
        message.setIs_forwarded(false);
        return  message;
    }
    public void sendMessage(Long chatId,String message) throws IOException {
        int reqStatusCreateChat ;
        SendMessageRequest request=new SendMessageRequest(login,login,chatId,message);

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpPost post = new HttpPost("http://localhost:8080/send_message");
        post.setEntity(new StringEntity(jsonMapper.writeValueAsString(request), "UTF-8"));
        post.addHeader("content-type", "application/json");
        HttpResponse re = client.execute(post);
        reqStatusCreateChat= re.getStatusLine().getStatusCode();
        if (reqStatusCreateChat != 200) {
            throw new HttpResponseException(reqStatusCreateChat,"Error while send message");
        }
    }
    public void deleteMessage(Long message_id) throws IOException {
        int reqStatusCreateChat;
        String getMessagesUrlBlueprint = "http://localhost:8080/delete_message?message_id=%d";

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpPatch httpGet = new HttpPatch(String.format( getMessagesUrlBlueprint, message_id));
        HttpResponse re = client.execute(httpGet);
        String json = EntityUtils.toString(re.getEntity());
        reqStatusCreateChat=re.getStatusLine().getStatusCode();
        if (reqStatusCreateChat != 200) {
            throw new HttpResponseException(reqStatusCreateChat,"Error while delete message");
        }
    }

    public void editMessage(Long message_id,String Newcontents) throws IOException {
        int reqStatusCreateChat;
        EditMessageRequest request=new EditMessageRequest(message_id,Newcontents);

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpPatch post = new HttpPatch("http://localhost:8080/edit_message");
        post.setEntity(new StringEntity(jsonMapper.writeValueAsString(request), "UTF-8"));
        post.addHeader("content-type", "application/json");
        HttpResponse re = client.execute(post);
        reqStatusCreateChat = re.getStatusLine().getStatusCode();
        if (reqStatusCreateChat != 200) {
            throw new HttpResponseException(reqStatusCreateChat,"Error while edit message");
        }
    }
    public void leaveChat(Long chatId, String login) throws IOException {
        int reqStatusCreateChat;
        ChatUpdateRequest request = new ChatUpdateRequest(chatId, Collections.singletonList(login));

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpPatch post = new HttpPatch("http://localhost:8080/delete_users_from_chat");
        post.setEntity(new StringEntity(jsonMapper.writeValueAsString(request), "UTF-8"));
        post.addHeader("content-type", "application/json");
        HttpResponse re = client.execute(post);
        reqStatusCreateChat=re.getStatusLine().getStatusCode();

        if (reqStatusCreateChat != 200) {
            throw new HttpResponseException(reqStatusCreateChat,"Error while leave chat");
        }

    }
    public List<ChatUser> getChatMembers(Chat chatToConfigure) throws IOException {
        String getChatsUrlBlueprint = "http://localhost:8080/get_chat_members?chat_id=%d";

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpGet httpGet = new HttpGet(String.format(getChatsUrlBlueprint,chatToConfigure.getId()));
        HttpResponse re = client.execute(httpGet);
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



    public List<Chat> find(String name,Long page) throws IOException {
        String getMessagesUrlBlueprint = "http://localhost:8080/get_chats_by_search?login=%s&begin=%s&page_number=%d";

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpGet httpGet = new HttpGet(String.format( getMessagesUrlBlueprint,login,name,page));
        HttpResponse re = client.execute(httpGet);
        String json = EntityUtils.toString(re.getEntity());
        int code=re.getStatusLine().getStatusCode();
        if(code!=200)
        {
            if (code==400)
            {
                return new ArrayList<>();
            }
            else
            {
                throw new HttpResponseException(code,"Error while find");
            }
        }

        return jsonMapper.readValue(json, new TypeReference<>() {
        });
    }


    private static int forwardMessage(Long message_id, String login,String chat_id) throws IOException {
        String getMessagesUrlBlueprint = "http://localhost:8080/forward_message?message_id=%d?sender_login=%s?chat_id=%d";

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpPost httpGet = new HttpPost(String.format( getMessagesUrlBlueprint, message_id,login,chat_id));
        HttpResponse re = client.execute(httpGet);
        String json = EntityUtils.toString(re.getEntity());
        int code=re.getStatusLine().getStatusCode();

        return re.getStatusLine().getStatusCode();
    }


    public ArrayList<Image> getImageList(List<String> userList) {
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
  
    public Image getImage(String userList) {

        Image image;
        var res=(getClass().getResource("/images/dAvatar.bmp")).getPath().replaceFirst("/","");
        image=new Image(res);

        return  image;
    }


}
