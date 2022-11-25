package ru.edu.spbstu.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import ru.edu.spbstu.clientComponents.PasswordTextField;

public class LoginFormController {
    public CheckBox s;
    public Button forgetPasswordButton;
    public TextField loginTextBox;
    public PasswordTextField passwordTextBox;
    public TextField emailTextBox;
    public TextField regLoginTextBox;
    public PasswordTextField regPasswordTextBox;
    public Button logInButton;
    public Button registerButton;

    ContextMenu contextMenu = new ContextMenu();

    @FXML
    void initialize() {


        //setting on action callback
        logInButton.setDisable(true);
        registerButton.setDisable(true);
        //You can even create callbacks using lambdas https://www.tutorialspoint.com/how-to-add-action-listeners-to-contextmenu-in-javafx
    }


    public void logInButtonLeftMouseClick(MouseEvent mouseEvent) {

        System.out.println("login "+loginTextBox.getText()+" "+passwordTextBox.getText());
    }

    public void forgotPasswordButtonClick(MouseEvent mouseEvent) {
        System.out.println("forgot "+loginTextBox.getText());
    }

    public void registerButtonMouseClick(MouseEvent mouseEvent) {
        System.out.println("register "+regLoginTextBox.getText()+" "+emailTextBox.getText()+" "+regPasswordTextBox.getText());
    }

    public void updateLogin(KeyEvent keyEvent) {
        if(loginTextBox.getText().equals(""))
        {
            logInButton.setDisable(true);
            return;
        }
        if(passwordTextBox.getText().equals(""))
        {
            logInButton.setDisable(true);
            return;
        }
        logInButton.setDisable(false);
    }
    public void updateRegisterButton(KeyEvent keyEvent)
    {
        if(emailTextBox.getText().equals(""))
        {
            registerButton.setDisable(true);
            return;
        }
        if(regLoginTextBox.getText().equals(""))
        {
            registerButton.setDisable(true);
            return;
        }

        if(regPasswordTextBox.getText().equals(""))
        {
            registerButton.setDisable(true);
            return;
        }

        registerButton.setDisable(false);
    }
}
