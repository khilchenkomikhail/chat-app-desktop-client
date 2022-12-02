package ru.edu.spbstu.client.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import org.apache.http.client.CredentialsProvider;
import ru.edu.spbstu.client.services.ChatFormService;
import ru.edu.spbstu.clientComponents.PasswordTextField;
import ru.edu.spbstu.model.Chat;

import java.io.IOException;
import java.util.List;

public class ConfigureChatFormController {
    public TextField regLoginTextBox;
    public PasswordTextField regPasswordTextBox;
    public Button logOutButton;
    public Button registerButton;
    public TextField emailTextBox;
    public ListView<Chat> chatsListView;
    public TextField newChatTextBox;
    public Button addChatButton;
    private ChatFormService service=new ChatFormService();
    private Stage primaryStage;
    private Stage currStage;
    private int ChatPage=1;
    private int SelectedChat=0;
    private Chat ChatToConfigure;
    private List<Chat> chatList;
    public void setCredentials(CredentialsProvider prov,String login)
    {
        this.service.setCredentialsProvider(prov,login);
    }
    public void setChat(Chat selectedItem) {
        ChatToConfigure=selectedItem;
    }

    @FXML
    void initialize() throws IOException {


    }
    void init() throws IOException {
        chatList=service.getChats(1);
        chatsListView.setItems(FXCollections.observableArrayList(chatList));

    }



    public void logOutMouseClick(ActionEvent actionEvent) {
        currStage.close();
        primaryStage.show();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setCurrStage(Stage currStage) {
        this.currStage = currStage;
    }
    private void update()
    {
        chatsListView.setItems(FXCollections.observableArrayList(chatList));
    }

    public void addChatButtonClick(ActionEvent actionEvent) throws IOException {
        service.addChat(newChatTextBox.getText());
        chatList=service.getChats(1);
        update();
    }

    public void scrollMethod(ScrollEvent scrollEvent) {
        var index =chatsListView.getSelectionModel().getSelectedIndices();
        System.out.println(chatList.get(index.get(0)));
    }


}
