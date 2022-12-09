package ru.edu.spbstu.client.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.CredentialsProvider;
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
import ru.edu.spbstu.request.CheckEmailRequest;
import ru.edu.spbstu.request.EmailUpdateRequest;
import ru.edu.spbstu.request.PasswordUpdateRequest;
import ru.edu.spbstu.request.ProfilePhotoUpdateRequest;

import java.io.IOException;
import java.util.Base64;

public class ProfileFormService {
    private final ObjectMapper jsonMapper = new ObjectMapper();

    private CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    private String login;

    public byte[] getProfilePicture() throws IOException {

        // TODO check return code
        String gerProfilePictureUrlBlueprint = "http://localhost:8080/get_profile_photo?login=%s";

        // TODO handle exception
        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(credentialsProvider)
                .build()) {
            HttpGet httpGet = new HttpGet(String.format(gerProfilePictureUrlBlueprint, login));
            CloseableHttpResponse response = client.execute(httpGet);

            var entity = response.getEntity();
            if (entity.getContentType() == null) {
                return getClass().getResourceAsStream("/images/dAvatar.bmp").readAllBytes();
            } else {

                String json = EntityUtils.toString(entity);
                return Base64.getDecoder().decode(json);
            }

//            String imageString = jsonMapper.readValue(json, new TypeReference<>() {});
            // TODO for debugging, remove later
        }
    }

    public void setProfilePicture(byte[] imageBytes) throws IOException {
        String imageString = Base64.getEncoder().encodeToString(imageBytes);
        String setProfilePictureUrlBlueprint = "http://localhost:8080/update_profile_photo";
        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(credentialsProvider)
                .build()) {

            var request = new ProfilePhotoUpdateRequest(login, imageString);
            HttpPost httpPost = new HttpPost(String.format(setProfilePictureUrlBlueprint));
            httpPost.setEntity(new StringEntity(jsonMapper.writeValueAsString(request), "UTF-8"));
            httpPost.addHeader("content-type", "application/json");
            CloseableHttpResponse response = client.execute(httpPost);
            String json = EntityUtils.toString(response.getEntity());
        }
    }

    public void setProviderAndLogin(CredentialsProvider credentialsProvider, String login) {
        this.credentialsProvider = credentialsProvider;
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public void changeEmail(String oldEmail, String newEmail) throws IOException {
        String compareEmailUrlBlueprint = "http://localhost:8080/check_user_email";
        String updateEmailUrlBlueprint = "http://localhost:8080/update_email";

        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(credentialsProvider)
                .build()) {

            CheckEmailRequest checkRequest = new CheckEmailRequest(login, oldEmail);
            HttpPost httpPost = new HttpPost(compareEmailUrlBlueprint);
            httpPost.setEntity(new StringEntity(jsonMapper.writeValueAsString(checkRequest), "UTF-8"));
            httpPost.addHeader("content-type", "application/json");
            CloseableHttpResponse checkResponse = client.execute(httpPost);
            String json = EntityUtils.toString(checkResponse.getEntity());
            Boolean isMatching = jsonMapper.readValue(json, new TypeReference<>() {});
            if (isMatching) {
                EmailUpdateRequest updateRequest = new EmailUpdateRequest(login, newEmail);
                httpPost = new HttpPost(updateEmailUrlBlueprint);
                httpPost.setEntity(new StringEntity(jsonMapper.writeValueAsString(updateRequest), "UTF-8"));
                httpPost.addHeader("content-type", "application/json");
                CloseableHttpResponse updateResponse = client.execute(httpPost);
            } else {
                throw new InvalidDataException("Неверно указан старый email");
            }
        }
    }

    public int changePassword(String oldPassword, String newPassword) throws IOException {
        String updatePasswordUrlBlueprint = "http://localhost:8080/update_password";

        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                .setDefaultCredentialsProvider(credentialsProvider)
                .build()) {

            PasswordUpdateRequest updateRequest = new PasswordUpdateRequest(login, newPassword, oldPassword);
            HttpPost httpPost = new HttpPost(updatePasswordUrlBlueprint);
            httpPost.setEntity(new StringEntity(jsonMapper.writeValueAsString(updateRequest), "UTF-8"));
            httpPost.addHeader("content-type", "application/json");
            CloseableHttpResponse checkResponse = client.execute(httpPost);
            return checkResponse.getStatusLine().getStatusCode();
        }
    }
}
