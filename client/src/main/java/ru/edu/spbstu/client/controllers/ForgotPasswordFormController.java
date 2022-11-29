package ru.edu.spbstu.client.controllers;

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

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


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
        if(isEmail(emailTextBox.getText()))
        {
            showError("Содержимое поля email не соотвествует стандарту!");
            return;
        }

        //todo проверить что email совпадает с email учётки
        boolean isValid=true;
        if(isValid) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            forgotAction();
            alert.setTitle("Пароль успешно сброшен! Временный пароль был отпрален на почту!");
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
            emailTextBox.setText("попробуйте повторить запрос!");
            alert.show();
        }
    }
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private void forgotAction() throws IOException {

        int sendStatus;


        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPatch signUpReq = new HttpPatch("http://localhost:8080/send-tmp-password");
            signUpReq.addHeader("content-type", "application/json");
            signUpReq.setEntity(new StringEntity(login));//TODo This must work but after sending letter something bad happens
            sendStatus= client.execute(signUpReq).getStatusLine().getStatusCode();
        }

        if (sendStatus != 200) {
            throw new HttpResponseException(sendStatus,"Error while register");
        }


    }
}
