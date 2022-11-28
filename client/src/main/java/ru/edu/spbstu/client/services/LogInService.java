package ru.edu.spbstu.client.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import ru.edu.spbstu.request.SignUpRequest;

import java.io.IOException;


public class LogInService {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private CredentialsProvider prov= new BasicCredentialsProvider();
    public CredentialsProvider getProvider()
    {
        return prov;
    }
    //private String lo;

    public  void register(String login,String password,String email) throws IOException {
        String image = "image "+ login;//TODO когда будет поддержка изображений его надо добавить сюда

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
    }
    public LogInService()
    {

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
}