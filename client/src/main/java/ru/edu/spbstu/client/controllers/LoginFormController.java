package ru.edu.spbstu.client.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import ru.edu.spbstu.client.ClientApplication;
import ru.edu.spbstu.clientComponents.PasswordTextField;

import java.io.IOException;




import ru.edu.spbstu.client.services.LogInService;
import ru.edu.spbstu.request.SignUpRequest;

import static ru.edu.spbstu.client.utils.Verifiers.isEmail;

public class LoginFormController {
    public CheckBox rememberMeCheckBox;
    public Button forgetPasswordButton;
    public TextField loginTextBox;
    public PasswordTextField passwordTextBox;
    public TextField emailTextBox;
    public TextField regLoginTextBox;
    public PasswordTextField regPasswordTextBox;
    public Button logInButton;
    public Button registerButton;

    private LogInService service;
    private Stage stage;


    void showError(String errorText)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(errorText);
        alert.show();
    }

    @FXML
    void initialize() {
        service=new LogInService();
        logInButton.setDisable(true);
        registerButton.setDisable(true);
        forgetPasswordButton.setDisable(true);
        //regLoginTextBox.setText("olegoleg");
        //emailTextBox.setText("вставь свой email");
        //regPasswordTextBox.setText("olegoleg");
        stage= ClientApplication.getStage();

    }

    private void clear()
    {
        passwordTextBox.setText("");
        emailTextBox.setText("");
        regPasswordTextBox.setText("");
        passwordTextBox.setText("");
        logInButton.setDisable(true);
        registerButton.setDisable(true);
    }


    public void logInButtonPress(ActionEvent actionEvent)  {
        if(passwordTextBox.getText().length()<8)
        {
            showError("Поле пароль должно содержать не менее 8 символов");
            return;
        }

        try {
            service.logIn(loginTextBox.getText(),passwordTextBox.getText());


        } catch (IOException e) {
            showError("Учётной записи с данным логином и паролем не существует");
            return;
        }
        try {
            openChatForm(loginTextBox);

        } catch (IOException e) {
            showError("Ошибка при открытии 2-й формы");
            return;
        }
        regLoginTextBox.setText("");
        registerButton.setDisable(true);
        clear();
        stage.hide();
    }


    public void forgotPasswordButtonPress(ActionEvent actionEvent) throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/forgot_password.fxml"));
        Parent window = (Pane) fmxlLoader.load();
        ForgotPasswordFormController conF = fmxlLoader.<ForgotPasswordFormController>getController();
        Scene scene = new Scene(window);
        conF.setLogin(loginTextBox.getText());
        Stage stage= new Stage();
        stage.setScene(scene);
        clear();
        regLoginTextBox.setText("");
        registerButton.setDisable(true);
        stage.show();
    }

    public void registerButtonPress(ActionEvent actionEvent) throws IOException {
        if(regPasswordTextBox.getText().length()<8)
        {
            showError("Поле пароль должно содержать не менее 8 символов");
            return;
        }

        if(!isEmail(emailTextBox.getText()))
        {
            showError("Содержимое поля email не соотвествует стандарту!");
            return;
        }
        service.register(regLoginTextBox.getText(),regPasswordTextBox.getText(),emailTextBox.getText());
        openChatForm(regLoginTextBox);
        clear();
        loginTextBox.setText("");
        logInButton.setDisable(true);
        forgetPasswordButton.setDisable(true);
        stage.hide();

    }

    private void openChatForm(TextField regLoginTextBox) throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/chat_form.fxml"));
        Parent window = (Pane) fmxlLoader.load();
        // private Scene scene;
        ChatFormController conC = fmxlLoader.<ChatFormController>getController();
        Scene scene = new Scene(window,900,700);
        conC.setCredentials(this.service.getProvider(), regLoginTextBox.getText());
        Stage nstage= new Stage();
        nstage.setScene(scene);
        nstage.setTitle("Chats");
        conC.setCurrStage(nstage);
        conC.setPrimaryStage(this.stage);
        conC.init();
        nstage.show();
    }

    public void updateLogin(KeyEvent keyEvent) {
        if(loginTextBox.getText().length()==0)
        {
            logInButton.setDisable(true);
            forgetPasswordButton.setDisable(true);
            return;
        }
        forgetPasswordButton.setDisable(false);
        if(passwordTextBox.getText().length()==0)
        {
            logInButton.setDisable(true);
            return;
        }
        logInButton.setDisable(false);
    }
    public void updateRegisterButton(KeyEvent keyEvent)
    {
        if(emailTextBox.getText().length()==0)
        {
            registerButton.setDisable(true);
            return;
        }
        if(regLoginTextBox.getText().length()==0)
        {
            registerButton.setDisable(true);
            return;
        }

        if(regPasswordTextBox.getText().length()==0)
        {
            registerButton.setDisable(true);
            return;
        }

        registerButton.setDisable(false);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


}
