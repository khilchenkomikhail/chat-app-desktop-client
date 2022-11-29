package ru.edu.spbstu.client.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.BasicCredentialsProvider;
import ru.edu.spbstu.client.services.ChatFormService;
import ru.edu.spbstu.client.services.LogInService;
import ru.edu.spbstu.clientComponents.PasswordTextField;
import ru.edu.spbstu.model.Chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatFormController {

    public Button logOutButton;

    public ListView<Chat> chatsListView;
    public TextField newChatTextBox;
    public Button addChatButton;
    public Button profileButton;
    public ComboBox LanguageComboBox;
    public ListView chatsListView1;
    public Button sendMessageButton;
    public TextArea messageTextArea;
    private ChatFormService service=new ChatFormService();
    private Stage primaryStage;
    private Stage currStage;
    private int ChatPage=1;
    private int SelectedChat=0;
    private List<Chat> chatList;
    public void setCredentials(CredentialsProvider prov,String login)
    {
        this.service.setCredentialsProvider(prov,login);
    }
    void showError(String errorText)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(errorText);
        alert.show();
    }

    @FXML
    void initialize() throws IOException {


    }
    void init() {
        try {
            chatList = service.getChats(1);
        }
        catch (HttpResponseException e)
        {
            showError(e.getReasonPhrase());
            logOutAction();
        } catch (IOException e) {
            showError("Internal server error!");
        }

        chatsListView.setItems(FXCollections.observableArrayList(chatList));

    }


    private void logOutAction()
    {
        currStage.close();
        primaryStage.show();
    }
    public void logOutMouseClick(ActionEvent actionEvent) {
        logOutAction();
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

       // service.addChat(newChatTextBox.getText());
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/create_chat_form.fxml"));
        Parent window = (Pane) fmxlLoader.load();
        var conC = fmxlLoader.<CreateChatFormController>getController();
        Scene scene = new Scene(window,800,800);

        conC.setCredentials(this.service.getCredentialsProvider(),this.service.getLogin());


        Stage nstage= new Stage();
        nstage.setScene(scene);
        nstage.setTitle("Profile");
        conC.setCurrStage(nstage);
        conC.setPrimaryStage(this.currStage);
        conC.init();

        nstage.show();
        chatList=service.getChats(1);
        update();
    }



    public void scrollMethod(ScrollEvent scrollEvent) {
        var index =chatsListView.getSelectionModel().getSelectedIndices();
        System.out.println(chatList.get(index.get(0)));
    }

    public void findChatsEvent(KeyEvent keyEvent) {
        String name=newChatTextBox.getText();

        List<Chat> temp =service.find(name);
    }



    public void ProfileMouseClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/profile_form.fxml"));
        Parent window = (Pane) fmxlLoader.load();
        var conC = fmxlLoader.<ChatFormController>getController();//TODO поменять
        Scene scene = new Scene(window,700,700);

        conC.setCredentials(this.service.getCredentialsProvider(),this.service.getLogin());
        conC.init();

        Stage nstage= new Stage();
        nstage.setScene(scene);
        nstage.setTitle("Profile");
        conC.setCurrStage(nstage);
        conC.setPrimaryStage(this.currStage);

        nstage.show();
    }

    public void LanguageCBAction(ActionEvent actionEvent) {
    }

    public void sendMessageButtonClick(ActionEvent actionEvent) {
    }
}
