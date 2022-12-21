package ru.edu.spbstu.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.HttpStatus;
import ru.edu.spbstu.client.exception.InvalidDataException;
import ru.edu.spbstu.client.services.ProfileFormService;
import ru.edu.spbstu.client.utils.Verifiers;
import ru.edu.spbstu.clientComponents.PasswordTextField;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ResourceBundle;

import static ru.edu.spbstu.client.utils.Verifiers.checkEmail;

public class ProfileFormController {
    @FXML
    private Button changePasswordButton;

    @FXML
    private ImageView profilePictureImageView;

    @FXML
    private Button changeProfilePictureButton;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField newEmailTextField;

    @FXML
    private Button changeEmailButton;

    @FXML
    public Label userLoginLabel;

    @FXML
    private PasswordTextField oldPasswordTextField;
    @FXML
    private PasswordTextField newPasswordTextField;
    @FXML
    private PasswordTextField repeatPasswordTextField;


    private ProfileFormService profileFormService;

    private Stage primaryStage;
    private Stage currentStage;
    private ChatFormController prevController;
    private ResourceBundle bundle;


    public void initialize() {
        GridPane.setFillWidth(changePasswordButton, true);
        GridPane.setFillWidth(profilePictureImageView, true);
        GridPane.setFillHeight(profilePictureImageView, true);
        changeEmailButton.setDisable(true);
        changePasswordButton.setDisable(true);
    }

    void init() {
        userLoginLabel.setText(profileFormService.getLogin());
        try {
            byte[] imageBytes = profileFormService.getProfilePicture();
            Image image = new Image(new ByteArrayInputStream(imageBytes),160,160,false,false);
            profilePictureImageView.setImage(image);
        } catch (IOException e) {
            showError(e.getMessage());
        }


        currentStage.setOnCloseRequest(e -> {
            prevController.setLogin(profileFormService.getLogin());
            primaryStage.show();
            prevController.resumeLoad();
            currentStage.close();
        });
    }

    public ProfileFormController() {
        profileFormService = new ProfileFormService();
    }


    private void showError(String errorText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(errorText);
        alert.show();
    }

    public void changeProfilePictureButtonPress(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите аватар");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG/JPEG", "*.png", "*.jpeg","*.jpg"));
        File file = fileChooser.showOpenDialog(currentStage);
        if (file == null) {
            return;
        }
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            Image image = new Image(new ByteArrayInputStream(fileContent));
            if (image.getHeight() > 1000 || image.getWidth() > 1000) {
                showError("Ширина или высота изображения превышает максимальный размер (1000)");
                return;
            }
            if (image.getHeight() < 200 || image.getWidth() < 200) {
                showError("Ширина или высота изображения не достигает минимального размера (200)");
                return;
            }
            profileFormService.setProfilePicture(fileContent);
            profilePictureImageView.setPreserveRatio(false);
            profilePictureImageView.setImage(image);
        } catch (IOException e) {
            showError(e.getMessage());
        }
    }

    public void changeEmailTextFieldChanged(KeyEvent keyEvent) {
        changeEmailButton.setDisable(emailTextField.getText().isBlank() || newEmailTextField.getText().isBlank());
    }

    public void changeEmailButtonPress(ActionEvent actionEvent) {
        /*if (!Verifiers.checkEmail(newEmailTextField.getText())) {
            showError("Новый email не соответствует стандарту!");
            return;
        }*/
        try {
            checkEmail(newEmailTextField.getText());
        }
        catch (InvalidDataException ex)
        {
            showError(bundle.getString(ex.getMessage()));
            return;
        }
        if (newEmailTextField.getText().equals(emailTextField.getText())) {
            showError("Указанные новый и старый email совпадают");
            return;
        }
        try {
            profileFormService.changeEmail(emailTextField.getText(), newEmailTextField.getText());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Email успешно изменен");
            alert.setTitle("Email изменен");
            alert.show();
            emailTextField.setText("");
            newEmailTextField.setText("");
            changeEmailButton.setDisable(true);
        } catch (InvalidDataException | IOException e) {
            showError(e.getMessage());
        }
    }

    public void setLogin(String login) {
        profileFormService.setLogin(login);
    }

    public void setCurrentStage(Stage stage) {
        currentStage = stage;
    }

    public void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public void setPrevController(ChatFormController prevController) {
        this.prevController = prevController;
    }

    public void passwordTextFieldsChanged(KeyEvent keyEvent) {
        changePasswordButton.setDisable(oldPasswordTextField.getText().isEmpty() ||
                newPasswordTextField.getText().isEmpty() || repeatPasswordTextField.getText().isEmpty());
    }

    public void changePasswordButtonPress(ActionEvent actionEvent) {
        if (!newPasswordTextField.getText().equals(repeatPasswordTextField.getText())) {
            showError("Введенные пароли не совпадают");
            return;
        }
        if (newPasswordTextField.getText().length() < 8 || newPasswordTextField.getText().length() > 128) {
            showError("Поле пароль должно содержать не менее 8 символов и не более 128 символов!");
            return;
        }
        try {
            int code = profileFormService.changePassword(oldPasswordTextField.getText(), newPasswordTextField.getText());
            if (code == HttpStatus.SC_OK) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Пароль успешно изменен");
                alert.setTitle("Пароль изменен");
                alert.show();
                oldPasswordTextField.setText("");
                newPasswordTextField.setText("");
                repeatPasswordTextField.setText("");
                changePasswordButton.setDisable(true);
            } else if (code == HttpStatus.SC_BAD_REQUEST) {
                showError("Старый пароль введен неверно");
            }
        } catch (IOException e) {
            showError(e.getMessage());
        }
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
}
