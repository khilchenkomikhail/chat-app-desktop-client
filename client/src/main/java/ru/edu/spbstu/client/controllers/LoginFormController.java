package ru.edu.spbstu.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import ru.edu.spbstu.clientComponents.PasswordTextField;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//import ru.edu.spbstu.backend.
//import ru.edu.spbstu.request.SignUpRequest;
import ru.edu.spbstu.client.services.LogInService;
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

    }


    public void logInButtonPress(ActionEvent actionEvent) {
        if(passwordTextBox.getText().length()<8)
        {
            showError("Поле пароль должно содержать не менее 8 символов");
            return;
        }


        try {
            service.logIn(loginTextBox.getText(),passwordTextBox.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO здесь надо открыть следующую форму и пеhедать туда credentials provider

    }

    public void forgotPasswordButtonPress(ActionEvent actionEvent) {

        /*try {//ToDO пример миши с реистрацией пользователя и созданием для него чатов
            service.init();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        URL fxmlUrl = this.getClass()
                .getResource("/fxmls/forgot_password.fxml");
        if(fxmlUrl==null)
        {
            System.out.println("Not found2");
            return;
        }
        //launch scene
        //Stage stage=new Stage();
       // Parent root = null;
        /*try {
            root = FXMLLoader.load(fxmlUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //stage.setTitle("ForgotPassword");
        //stage.setScene(new Scene(root, 600, 450));

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage=new Stage();
     //  stage.setScene(new Scene(root));
        stage.setScene(new Scene(root, 600, 450));
        ForgotPasswordFormController.setLogin(loginTextBox.getText());//TODO это костыль, я хочу передать логин в форму для забывания парол
        //Чтобы потом, проверить почту, но сейчас я  это могу сделать только через статический метод, что не очень


        stage.show();
        System.out.println("forgot "+loginTextBox.getText());
    }

    public void registerButtonPress(ActionEvent actionEvent) {
        if(regPasswordTextBox.getText().length()<8)
        {
            showError("Поле пароль должно содержать не менее 8 символов");
            return;
        }
         final String EMAIL_PATTERN =
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher m=pattern.matcher(emailTextBox.getText());
        if(!m.matches())
        {
            showError("Содержиоме поля email не соотвествует стандарту!");
            return;
        }

        try {
            service.register(loginTextBox.getText(),passwordTextBox.getText(),emailTextBox.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO здесь надо открыть следующую форму и передать туда credentials provider

    }

    public void updateLogin(KeyEvent keyEvent) {
        if(loginTextBox.getText().length()==0)
        {
            logInButton.setDisable(true);
            return;
        }
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
}
