package ru.edu.spbstu.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.http.client.HttpResponseException;
import ru.edu.spbstu.client.ClientApplication;
import ru.edu.spbstu.client.services.LogInService;
import ru.edu.spbstu.clientComponents.PasswordTextField;

import java.io.IOException;

import static ru.edu.spbstu.client.utils.Verifiers.isEmail;

public class LoginFormController {
    public CheckBox rememberMeCheckBox;
    public TextField loginTextBox;
    public PasswordTextField passwordTextBox;
    public TextField emailTextBox;
    public TextField regLoginTextBox;
    public PasswordTextField regPasswordTextBox;


    public Button forgetPasswordButton;
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
        if(passwordTextBox.getText().length()<8||passwordTextBox.getText().length()>128)
        {
            showError("Поле пароль должно содержать не менее 8 символов и не более 128 символов!");
            // showError("Password filed must contain more than 7 symbols and less than 128 symbols!");

            return;
        }

        try {
            service.logIn(loginTextBox.getText(),passwordTextBox.getText());


        }
        catch (HttpResponseException ex)
        {
            if(ex.getStatusCode()==401)
            {
                showError("Неверные логин или пароль!");
               // showError("Invalid login or password!");

            }
        }
        catch (IOException e) {
            showError("Внутренняя ошибка сервера!");
           // showError("Internal server error!");
            return;
        }
        try {
            openChatForm(loginTextBox);

        }
        catch (IOException e) {
            showError("Ошибка при открытии 2-й формы");
            //showError("Error when open second form");
            return;
        }
        regLoginTextBox.setText("");
        registerButton.setDisable(true);
        clear();
        stage.hide();
    }


    public void forgotPasswordButtonPress(ActionEvent actionEvent) {
        //getTheUserFilePath();
        try {
            boolean isPresent=service.isUserPresent(loginTextBox.getText());
            if(!isPresent)
            {
                showError("На данный логин не зарегестрировано учётных записей!");
               // showError("There in no account registered with this login!");
                return;
            }
        }
        catch (IOException e)
        {
            showError("Внутренняя ошибка сервера");
            //showError("Internal server error");
            return;
        }


        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/forgot_password.fxml"));
        Parent window = null;
        try {
            window = (Pane) fmxlLoader.load();
        } catch (IOException e) {
            showError("Ошибка при открытии формы сброса пароля!");
           // showError("Error when open forgotPassword form!");
            return;
        }
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

    public void registerButtonPress(ActionEvent actionEvent) {
        if(regPasswordTextBox.getText().length()<8||regPasswordTextBox.getText().length()>128)
        {
            showError("Поле пароль должно содержать не менее 8 символов и не более 128!");
           // showError("Password filed must contain more than 7 symbols and less than 128 symbols!");
            return;
        }

        if(!isEmail(emailTextBox.getText()))
        {
            showError("Содержимое поля email не соотвествует стандарту!");
           // showError("Invalid email field format!");
            return;
        }
        try {


            boolean res = service.isUserPresent(regLoginTextBox.getText());
            if (res) {
                showError("Учётная запись с данным логином уже существует!");
                //showError("Account with this login already exists!");
                return;
            }
            boolean res2 = service.isEmailUsed(emailTextBox.getText());
            if (res2) {
                showError("На данный email уже зарегестрирована учётная запись!");
                //showError("Account was already registered on this email!");
                return;
            }
            service.register(regLoginTextBox.getText(), regPasswordTextBox.getText(), emailTextBox.getText());
        }
        catch (IOException ex)
        {
            showError("Внутренняя ошибка сервера");
            return;
            //showError("Internal server error");
        }
        try {
            openChatForm(regLoginTextBox);
        }  catch (IOException e) {
            showError("Ошибка при открытии 2-й формы");
            //showError("Error when open second form");
            return;
        }
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
