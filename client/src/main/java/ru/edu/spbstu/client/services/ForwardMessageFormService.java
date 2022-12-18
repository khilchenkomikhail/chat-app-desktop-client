package ru.edu.spbstu.client.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import ru.edu.spbstu.client.utils.HttpClientFactory;
import ru.edu.spbstu.model.Chat;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ForwardMessageFormService {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private String login;


    public List<Chat> getChats(Integer page) throws IOException {
        String getChatsUrlBlueprint = "http://localhost:8080/get_chats?login=%s&page_number=%d";

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpGet httpGet = new HttpGet(String.format(getChatsUrlBlueprint, URLEncoder.encode(login, StandardCharsets.UTF_8), page));
        HttpResponse re = client.execute(httpGet);
        HttpClientFactory.tryUpdateRememberMe(re);
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

    public void setLogin(String login) {
        this.login = login;
    }
}
