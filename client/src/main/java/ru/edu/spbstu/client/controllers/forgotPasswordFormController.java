package ru.edu.spbstu.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class forgotPasswordFormController {
    private static String login;
    @FXML
    private Button changePasswordButton;
    public TextField emailTextBox;
    public static void setLogin(String llog)
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

        //todo проерить что email совпадает с email учётки
        boolean isValid=true;
        if(isValid) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Пароль успешно сброшен! Временный пароль был отпрален на почту!");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    Stage stage = (Stage) changePasswordButton.getScene().getWindow();
                    //Stage stage  = (Stage) source.getScene().getWindow();
                    stage.close();
                }
            });
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Почта введена неверно попробуйте повторить запрос!");
            emailTextBox.setText("");
            alert.show();
        }
    }
}
