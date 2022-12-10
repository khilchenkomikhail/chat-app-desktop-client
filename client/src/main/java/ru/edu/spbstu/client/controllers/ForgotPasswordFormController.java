package ru.edu.spbstu.client.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import ru.edu.spbstu.client.utils.HttpClientFactory;
import ru.edu.spbstu.model.Language;
import ru.edu.spbstu.request.CheckEmailRequest;
import ru.edu.spbstu.request.SendTemporaryPasswordRequest;

import java.io.IOException;
import java.util.ResourceBundle;

import static ru.edu.spbstu.client.utils.Verifiers.isEmail;

public class ForgotPasswordFormController {
    private  String login;
    @FXML
    private Button changePasswordButton;
    public TextField emailTextBox;
    private ResourceBundle bundle;


    public  void setLogin(String sLogin)
    {
        login= sLogin;
    }
    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    void showError(String errorText)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(bundle.getString("Error"));
        alert.setHeaderText(errorText);
        alert.show();
    }




    public void changePasswordButtonClick(MouseEvent mouseEvent) {
        if(!isEmail(emailTextBox.getText()))
        {
            showError(bundle.getString("BadFormatEmailErrorText"));
           // showError("Invalid email format!");
            return;
        }

        boolean isValid;
        String email=emailTextBox.getText();
        CheckEmailRequest signUpRequest = new CheckEmailRequest(login,email);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost signUpReq = new HttpPost("http://localhost:8080/check_user_email");
            signUpReq.addHeader("content-type", "application/json");
            signUpReq.setEntity(new StringEntity(jsonMapper.writeValueAsString(signUpRequest), "UTF-8"));
            var temp=client.execute(signUpReq);
            int code=temp.getStatusLine().getStatusCode();
            String json = EntityUtils.toString(temp.getEntity());
            isValid=jsonMapper.readValue(json, new TypeReference<>() {});
        }
        catch (IOException ex)
        {
            showError(bundle.getString("InternalErrorText"));
            return;
        }
        if(isValid) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            forgotAction();
            alert.setHeaderText(bundle.getString("InformationHeader"));
            alert.setTitle(bundle.getString("MessageSendSuccess"));
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
            alert.setTitle(bundle.getString("Error"));
            alert.setContentText(bundle.getString("BadEmailErrorText"));
           // alert.setContentText("Invalid mail address entered! Try to repeat  request!");
            alert.show();
        }
    }

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private void forgotAction() {

        int sendStatus;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPatch signUpReq = new HttpPatch("http://localhost:8080/send-tmp-password");
            signUpReq.addHeader("content-type", "application/json");

            SendTemporaryPasswordRequest sendTemporaryPasswordRequest = new SendTemporaryPasswordRequest();
            sendTemporaryPasswordRequest.setLogin(login);
            sendTemporaryPasswordRequest.setLanguage(shittyCrutch());

            signUpReq.setEntity(new StringEntity(jsonMapper.writeValueAsString(sendTemporaryPasswordRequest), "UTF-8"));
            sendStatus= client.execute(signUpReq).getStatusLine().getStatusCode();
            if (sendStatus != 200) {
                throw new HttpResponseException(sendStatus,"Error while register");
            }
            HttpClientFactory.getInstance().invalidateToken();
        }
        catch (IOException e)
        {
            showError( bundle.getString("MessageErrorText"));
        }
    }

    // Надо по хорошему сделать нормально через проперти или какой-то систменый флаг, но и так сойдет
    private Language shittyCrutch() {
        String s = bundle.getString("Error");
        if (s.equals("Error")) {
            return Language.ENGLISH;
        } else {
            return Language.RUSSIAN;
        }
    }
}
