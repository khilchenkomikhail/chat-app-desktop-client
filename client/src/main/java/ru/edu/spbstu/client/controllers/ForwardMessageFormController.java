package ru.edu.spbstu.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.edu.spbstu.client.services.ForwardMessageFormService;
import ru.edu.spbstu.model.Chat;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class ForwardMessageFormController {
    private List<Chat> chatList;

    @FXML
    private ListView<Chat> chatsListView;

    @FXML
    private Button forwardButton;

    @FXML
    private TextField searchTextField;

    private final ForwardMessageFormService service = new ForwardMessageFormService();

    private Stage primaryStage;

    private Stage currentStage;

    private ChatFormController prevController;

    private ResourceBundle bundle;

    public void initialize() {
        forwardButton.disableProperty()
                .bind(chatsListView.getSelectionModel().selectedItemProperty().isNull());

    }

    public void init() {
        try {
            // TODO all pages?
            chatsListView.getItems().setAll(service.getChats(1));
        } catch (IOException e) {
            showError(e.getMessage());
        }
        currentStage.setOnCloseRequest(e -> {
            close();
        });
    }

    private void showError(String errorText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(bundle.getString("Error"));
        alert.setHeaderText(errorText);
        alert.show();
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public void setLogin(String login) {
        service.setLogin(login);
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setPrevController(ChatFormController prevController) {
        this.prevController = prevController;
    }

    public void forwardMessageButtonPress(ActionEvent event) {
        Chat chat = chatsListView.getSelectionModel().getSelectedItem();
        prevController.forwardNewMessage(chat);
        close();
    }

    private void close() {
        primaryStage.show();
        currentStage.close();
    }

}
