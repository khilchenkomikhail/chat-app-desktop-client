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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.apache.http.client.CredentialsProvider;
import ru.edu.spbstu.client.services.ProfileFormService;
import ru.edu.spbstu.clientComponents.PasswordTextField;

import java.io.IOException;

public class ProfileFormController {


    private final ProfileFormService service=new ProfileFormService();
    public ImageView profileAvatarImageView;
    public Label userLoginLabel;
    public Label changeEmailLabel;
    public Label oldEmailLabel;
    public Label newEmailLabel;
    public Button changeEmailButton;


    public Button changeAvatarButton;

    public Label changePasswordLabel;
    public Label oldPasswordLabel;
    public Label newPasswordLabel;
    public Label repeatPasswordLabel;
    public Button changePasswordButton;


    public TextField emailTextField;
    public TextField newEmailTextField;
    public PasswordTextField oldPasswordTextField;
    public PasswordTextField newPasswordTextField;
    public PasswordTextField repeatPasswordTextField;


    private Stage primaryStage;
    private Stage currStage;
    private ChatFormController prevController;


    public void setCredentials(CredentialsProvider prov,String login)
    {
        this.service.setCredentialsProvider(prov,login);
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public void setCurrStage(Stage currStage) {
        this.currStage = currStage;
    }
    public void setPrevController(ChatFormController prevController) {
        this.prevController = prevController;
    }

    @FXML
    void initialize() {
        //сюда можно пихать всё, что можно задать без инициализаци инекоторых комнонентнов
        //например названия для label можно задавать здесь

    }
    void init() throws IOException {
        //десь инициализируется всё для чего требуется предварительно задать параметры сервиса, сцену и пр.
        //см. как мы эту форму открываем из главной
        userLoginLabel.setText(service.getLogin());

        profileAvatarImageView.setImage(service.getImage());
        profileAvatarImageView.setFitWidth(160);
        profileAvatarImageView.setFitHeight(160);

        final Rectangle clip = new Rectangle();
        clip.arcWidthProperty().bind(clip.heightProperty().divide(0.1));
        clip.arcHeightProperty().bind(clip.heightProperty().divide(0.1));
        clip.setWidth( profileAvatarImageView.getLayoutBounds().getWidth());
        clip.setHeight( profileAvatarImageView.getLayoutBounds().getHeight());
        profileAvatarImageView.setClip(clip);


        currStage.setOnCloseRequest(e -> {
            prevController.setCredentials(service.getCredentialsProvider(), service.getLogin());//передать учётнеы данные обратно
            primaryStage.show();
            currStage.close();
        });
    }
    private void updateAvatar(Image image)
    {
        profileAvatarImageView.setImage(image);
        profileAvatarImageView.setFitWidth(160);//проверь нужно ли эти 2 параметра задавать заново, или они сохраняются
        profileAvatarImageView.setFitHeight(160);

        cropImageToRound(profileAvatarImageView);
    }

    static void cropImageToRound(ImageView profileAvatarImageView) {
        final Rectangle clip = new Rectangle();
        clip.arcWidthProperty().bind(clip.heightProperty().divide(0.1));
        clip.arcHeightProperty().bind(clip.heightProperty().divide(0.1));
        clip.setWidth( profileAvatarImageView.getLayoutBounds().getWidth());
        clip.setHeight( profileAvatarImageView.getLayoutBounds().getHeight());
        profileAvatarImageView.setClip(clip);
    }


    private void showError(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(s);
        alert.show();
    }



    public void changeEmailButtonPress(ActionEvent actionEvent) {
        //todo сделать в сервисе запрос на смену email, вызвать его здесь
        //если будут исключения лучше обработать тут
    }

    public void changePasswordButtonPress(ActionEvent actionEvent) {
        //проверки можно делать тут
        //todo сделать в сервисе запрос на смену пароля, вызвать его здесь
        //если будут исключения лучше обработать тут
    }

    public void changeAvatarButtonPress(ActionEvent actionEvent) {
        //для получения абсолютного пути к изображению можно использовать FileChooser
        //тут можно проверить требования на размер изображения
        //todo сделать в сервисе запрос на смену аватара, вызвать его здесь

        //вызвать updateAvatar, чтобы новое изображение подгрузить в imageView
        //если будут исключения лучше обработать тут
    }

    public void updateChangeEmailButton(KeyEvent keyEvent) {
        //этот метод вызывается каждый раз как ты, что-то вводишь в text поля для смены мыла(в scene builder сделать легко)
        //тут можно например проверить все поля с email на пустоту и заблокировать/разблокировать кнопку
    }

    public void updateChangePasswordButton(KeyEvent keyEvent) {
        //этот метод вызывается каждый раз как ты, что-то вводишь в text поля для смены пароля(в scene builder сделать легко)
        //тут можно например проверить все поля с password на пустоту и заблокировать/разблокировать кнопку
    }
}
