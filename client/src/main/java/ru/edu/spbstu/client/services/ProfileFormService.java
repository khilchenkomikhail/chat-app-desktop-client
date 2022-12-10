package ru.edu.spbstu.client.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import ru.edu.spbstu.client.exception.InvalidDataException;
import ru.edu.spbstu.client.utils.AuthScheme;
import ru.edu.spbstu.client.utils.HttpClientFactory;
import ru.edu.spbstu.request.CheckEmailRequest;
import ru.edu.spbstu.request.EmailUpdateRequest;
import ru.edu.spbstu.request.PasswordUpdateRequest;
import ru.edu.spbstu.request.ProfilePhotoUpdateRequest;

import java.io.IOException;
import java.util.Base64;

public class ProfileFormService {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private String login;

    public byte[] getProfilePicture() throws IOException {

        String getProfilePictureUrlBlueprint = "http://localhost:8080/get_profile_photo?login=%s";

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        HttpGet httpGet = new HttpGet(String.format(getProfilePictureUrlBlueprint, login));
        HttpResponse response = client.execute(httpGet);

        var entity = response.getEntity();
        if (entity.getContentType() == null) {
            return getClass().getResourceAsStream("/images/dAvatar.bmp").readAllBytes();
        } else {

            String json = EntityUtils.toString(entity);
            return Base64.getDecoder().decode(json);
        }

//      String imageString = jsonMapper.readValue(json, new TypeReference<>() {});
        // TODO for debugging, remove later
    }

    public void setProfilePicture(byte[] imageBytes) throws IOException {
        String imageString = Base64.getEncoder().encodeToString(imageBytes);
        String setProfilePictureUrlBlueprint = "http://localhost:8080/update_profile_photo";

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        var request = new ProfilePhotoUpdateRequest(login, imageString);
        HttpPost httpPost = new HttpPost(String.format(setProfilePictureUrlBlueprint));
        httpPost.setEntity(new StringEntity(jsonMapper.writeValueAsString(request), "UTF-8"));
        httpPost.addHeader("content-type", "application/json");
        HttpResponse response = client.execute(httpPost);
        String json = EntityUtils.toString(response.getEntity());
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void changeEmail(String oldEmail, String newEmail) throws IOException {
        String compareEmailUrlBlueprint = "http://localhost:8080/check_user_email";
        String updateEmailUrlBlueprint = "http://localhost:8080/update_email";

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        CheckEmailRequest checkRequest = new CheckEmailRequest(login, oldEmail);
        HttpPost httpPost = new HttpPost(compareEmailUrlBlueprint);
        httpPost.setEntity(new StringEntity(jsonMapper.writeValueAsString(checkRequest), "UTF-8"));
        httpPost.addHeader("content-type", "application/json");
        HttpResponse checkResponse = client.execute(httpPost);
        String json = EntityUtils.toString(checkResponse.getEntity());
        Boolean isMatching = jsonMapper.readValue(json, new TypeReference<>() {
        });
        if (isMatching) {
            EmailUpdateRequest updateRequest = new EmailUpdateRequest(login, newEmail);
            httpPost = new HttpPost(updateEmailUrlBlueprint);
            httpPost.setEntity(new StringEntity(jsonMapper.writeValueAsString(updateRequest), "UTF-8"));
            httpPost.addHeader("content-type", "application/json");
            HttpResponse updateResponse = client.execute(httpPost);
        } else {
            throw new InvalidDataException("Неверно указан старый email");
        }
    }

    public int changePassword(String oldPassword, String newPassword) throws IOException {
        String updatePasswordUrlBlueprint = "http://localhost:8080/update_password";

        HttpClient client = HttpClientFactory.getInstance().getHttpClient();
        PasswordUpdateRequest updateRequest = new PasswordUpdateRequest(login, newPassword, oldPassword);
        HttpPost httpPost = new HttpPost(updatePasswordUrlBlueprint);
        httpPost.setEntity(new StringEntity(jsonMapper.writeValueAsString(updateRequest), "UTF-8"));
        httpPost.addHeader("content-type", "application/json");

        HttpResponse checkResponse = client.execute(httpPost);
        if (checkResponse.getStatusLine().getStatusCode() == 200) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, newPassword);
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(AuthScope.ANY, credentials);
            HttpClientFactory.getInstance().setCredentialsProvider(provider);
        }

        return checkResponse.getStatusLine().getStatusCode();
    }
}
