package ru.edu.spbstu.client.controllers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import ru.edu.spbstu.request.CheckEmailRequest;
import ru.edu.spbstu.request.SignUpRequest;


import java.io.IOException;

import static ru.edu.spbstu.client.utils.Verifiers.isEmail;

public class ForgotPasswordFormController {
    private  String login;
    @FXML
    private Button changePasswordButton;
    public TextField emailTextBox;



    public  void setLogin(String llog)
    {
        login=llog;
    }

    void showError(String errorText)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(errorText);
        alert.show();
    }




    public void changePasswordButtonClick(MouseEvent mouseEvent) throws IOException {
        if(!isEmail(emailTextBox.getText()))
        {
            showError("Содержимое поля email не соотвествует стандарту!");
           // showError("Invalid email format!");
            return;
        }

        //todo проверить что email совпадает с email учётки
        boolean isValid=true;
        String email=emailTextBox.getText();
        CheckEmailRequest signUpRequest = new CheckEmailRequest(login,email);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost signUpReq = new HttpPost("http://localhost:8080/check_user_email");
            signUpReq.addHeader("content-type", "application/json");
            signUpReq.setEntity(new StringEntity(jsonMapper.writeValueAsString(signUpRequest)));
            var temp=client.execute(signUpReq);
            int code=temp.getStatusLine().getStatusCode();
            String json = EntityUtils.toString(temp.getEntity());
            isValid=jsonMapper.readValue(json, new TypeReference<>() {});
        }
        catch (IOException ex)
        {
            showError("Внутренняя ошибка сервера");
           // showError("Internal server error!");
            return;
        }
        if(isValid) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            forgotAction();
            alert.setTitle("Пароль успешно сброшен! Временный пароль был отпрален на почту!");
            //alert.setTitle("Password was reset successfully! Temporary password was sent to your mail!");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    Stage stage = (Stage) changePasswordButton.getScene().getWindow();
                    stage.close();
                }
            });
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setContentText("Введена неверная почта! Попробуйте повторить запрос!");
           // alert.setContentText("Invalid mail address entered! Try to repeat  request!");
            alert.show();
        }
    }

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private void forgotAction() throws IOException {

        int sendStatus;


        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPatch signUpReq = new HttpPatch("http://localhost:8080/send-tmp-password");
            signUpReq.addHeader("content-type", "application/json");
            signUpReq.setEntity(new StringEntity(login));
            sendStatus= client.execute(signUpReq).getStatusLine().getStatusCode();
            if (sendStatus != 200) {
                throw new HttpResponseException(sendStatus,"Error while register");
            }
        }
        catch (IOException e)
        {
            showError("Ошибка во время оправки письма!");
           // showError("Error occurred during email send!");
        }




    }

}
