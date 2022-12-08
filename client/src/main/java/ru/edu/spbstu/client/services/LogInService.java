package ru.edu.spbstu.client.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
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
import ru.edu.spbstu.client.utils.AuthScheme;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.request.SignUpRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


public class LogInService {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private CredentialsProvider prov= new BasicCredentialsProvider();
    public CredentialsProvider getProvider()
    {
        return prov;
    }


    public  void register(String login,String password,String email) throws IOException {
        //String image = "image "+ login;//TODO когда будет поддержка изображений его надо добавить сюда

        int regStatus = executeRegister(login, password, email);

        if (regStatus != 200) {
            throw new HttpResponseException(regStatus,"Error while register");
        }


        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
        prov.setCredentials(AuthScope.ANY, credentials);
    }
    public void logIn(String login,String password) throws IOException {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
        prov.setCredentials(AuthScope.ANY, credentials);
        getAllChats(prov,login,1);//TODo нужно сделать login endpoint, чтобы это не тестить так

    }
    public LogInService()
    {

    }
    private  static List<Chat> getAllChats(CredentialsProvider provider, String login, Integer page) throws IOException {

        String getChatsUrlBlueprint = "http://localhost:8080/get_chats?login=%s&page_number=%d";

        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(provider)
                .build()) {
            HttpGet httpGet = new HttpGet(String.format(getChatsUrlBlueprint, login, page));
            CloseableHttpResponse re = client.execute(httpGet);
            String json = EntityUtils.toString(re.getEntity());
            if(re.getStatusLine().getStatusCode()==400)
            {
                return Collections.emptyList();
            }
            return jsonMapper.readValue(json, new TypeReference<>() {});
        }
    }
    private static int executeRegister(String login, String password, String email) throws IOException {
        SignUpRequest signUpRequest = new SignUpRequest(login, password, email);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost signUpReq = new HttpPost("http://localhost:8080/register");
            signUpReq.addHeader("content-type", "application/json");
            signUpReq.setEntity(new StringEntity(jsonMapper.writeValueAsString(signUpRequest), "UTF-8"));
            var temp=client.execute(signUpReq);
            return temp.getStatusLine().getStatusCode();
        }
    }
}