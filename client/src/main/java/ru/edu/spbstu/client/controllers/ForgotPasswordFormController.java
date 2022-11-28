package ru.edu.spbstu.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.edu.spbstu.client.utils.Verificators.isEmail;

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




    public void changePasswordButtonClick(MouseEvent mouseEvent) {
        if(isEmail(emailTextBox.getText()))
        {
            showError("Содержиоме поля email не соотвествует стандарту!");
            return;
        }

        //todo проверить что email совпадает с email учётки
        boolean isValid=true;
        if(isValid) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
}
