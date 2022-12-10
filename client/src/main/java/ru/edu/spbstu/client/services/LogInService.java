package ru.edu.spbstu.client.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
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
import ru.edu.spbstu.client.utils.HttpClientFactory;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.request.SignUpRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


public class LogInService {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public  void register(String login,String password,String email) throws IOException {
        int regStatus = registerImplementation(login, password, email);

        if (regStatus != 200) {
            throw new HttpResponseException(regStatus,"Error while register");
        }

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, credentials);
        HttpClientFactory.getInstance().setCredentialsProvider(provider);
    }
    public void logIn(String login,String password, boolean isRememberMeChecked) throws IOException {

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, credentials);
        HttpClientFactory.getInstance().setCredentialsProvider(provider);
        getAllChats(login,1, isRememberMeChecked);
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

    public Boolean isEmailUsed(String email) throws IOException {
        String getChatsUrlBlueprint = "http://localhost:8080/is_email_used?email=%s";

        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .build()) {
            HttpGet httpGet = new HttpGet(String.format(getChatsUrlBlueprint, email));
            CloseableHttpResponse re = client.execute(httpGet);
            String json = EntityUtils.toString(re.getEntity());
            return jsonMapper.readValue(json, new TypeReference<>() {});
        }
    }

    private  static List<Chat> getAllChats(String login, Integer page, boolean isRememberMeChecked) throws IOException {

        String getChatsUrlBlueprint = "http://localhost:8080/get_chats?login=%s&page_number=%d";
        if (isRememberMeChecked) {
            getChatsUrlBlueprint += "&remember-me=on";
        }

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpGet httpGet = new HttpGet(String.format(getChatsUrlBlueprint, login, page));
        HttpResponse re = client.execute(httpGet);
        String json = EntityUtils.toString(re.getEntity());
        if(re.getStatusLine().getStatusCode()!=200) {
            if (re.getStatusLine().getStatusCode() == 400) {
                if (isRememberMeChecked) {
                    updateRememberMe(re);
                }
                return Collections.emptyList();
            } else {
                throw new HttpResponseException(re.getStatusLine().getStatusCode(), "Error while getAllChats");
            }
        }

        if (isRememberMeChecked) {
            updateRememberMe(re);
        }
        return jsonMapper.readValue(json, new TypeReference<>() {});
    }

    private static void updateRememberMe(HttpResponse re) {
        Header[] cookies = re.getHeaders("Set-Cookie");
        String rememberMeToken = null;
        for (Header cookie : cookies) {
            String content = cookie.getValue();
            if (content.substring(0, content.indexOf('=')).trim().equals("remember-me")) {
                rememberMeToken = content.substring(content.indexOf('=') + 1, content.indexOf(';'));
                break;
            }
        }
        if (rememberMeToken == null) {
            throw new RuntimeException();
        }
        HttpClientFactory.getInstance().setRememberToken(rememberMeToken);
    }

    private int registerImplementation(String login, String password, String email) throws IOException {
        SignUpRequest signUpRequest = new SignUpRequest(login, password, email);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost signUpReq = new HttpPost("http://localhost:8080/register");
            signUpReq.addHeader("content-type", "application/json");
            signUpReq.setEntity(new StringEntity(jsonMapper.writeValueAsString(signUpRequest), "UTF-8"));
            var temp= client.execute(signUpReq);
            return temp.getStatusLine().getStatusCode();
        }
    }
}