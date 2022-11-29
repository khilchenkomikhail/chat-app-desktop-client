package ru.edu.spbstu.client;

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
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.request.CreateChatRequest;
import ru.edu.spbstu.request.SignUpRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class fillServer {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private CredentialsProvider prov= new BasicCredentialsProvider();
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
        //getAllChats(prov, login, page).forEach(chat -> System.out.println(chat.getName()))
        return getAllChats(prov, login, page);
    }
    public void addChat(String chatName, List<String>users) throws IOException {
        int reqStatusCreateChat = createChat(prov, chatName, users, login);
        //System.out.println(reqStatusCreateChat);

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

    private static final List<String> loggins = Arrays.asList("olegoleg","Llananoker", "Relldastr", "Onadelestr","Jusarorol");
    private static final List<String> paswwords = Arrays.asList("olegoleg","Llananoker", "Relldastr", "Onadelestr","Jusarorol");

    public static void main(String[] args) throws IOException {
        fillServer fs=new fillServer();
        fs.start();

    }
    public void start() throws IOException {

        //List<String> emails=loggins.forEach( elem->{return elem+"@gmail.com";});
        ArrayList<String>emails=new ArrayList<>(0);
        for(var elem:loggins)
        {
            emails.add(elem+"@gmail.com");
        }

        for (int i=0;i<loggins.size();i++)
        {
            register(loggins.get(i),paswwords.get(i),emails.get(i));
        }
        logIn(loggins.get(0),paswwords.get(0));
        List<String> temp1=loggins;
        addChat("AllInChat",temp1);

        for (Integer i=1;i<loggins.size()-1;i++)
        {
            temp1=loggins.subList(0,loggins.size()-i);
            addChat("chat"+i.toString(),temp1);
        }
        //Todo add some messages





    }

    //private String lo;

    public  void register(String login,String password,String email) throws IOException {
        String image = "image "+ login;//TODO когда будет поддержка изображений его надо добавить сюда

        int regStatus = register(login, password, email, image);
        //System.out.println(regStatus);

        if (regStatus != 200) {
            throw new HttpResponseException(regStatus,"Error while register");
        }


        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
        prov.setCredentials(AuthScope.ANY, credentials);
    }
    public void logIn(String login,String password) throws IOException {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
        prov.setCredentials(AuthScope.ANY, credentials);
        this.login=login;
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


    public List<Chat> find(String name) {
        List<Chat>res=Collections.emptyList();

        return res;
    }
}
