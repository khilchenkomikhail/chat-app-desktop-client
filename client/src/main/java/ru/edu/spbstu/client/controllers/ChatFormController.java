package ru.edu.spbstu.client.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import ru.edu.spbstu.client.services.ChatFormService;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.Message;

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
    public Button sendMessageButton;
    public TextArea messageTextArea;
    public ListView<Message> messagesListView;
    private ChatFormService service=new ChatFormService();
    private Stage primaryStage;
    private Stage currStage;
    private int ChatPage=1;
    private int SelectedChat=0;
    private List<Chat> chatList;
    private List<Message> messageList;

    public void setCredentials(CredentialsProvider prov,String login)
    {
        service.setCredentialsProvider(prov,login);
    }
    private void showError(String errorText)
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
        chatsListView.setItems(FXCollections.observableList(chatList));

        /*chatsListView.resetList(chatList);*/

        chatsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Chat>() {//Todo переместить в лругое место
            @Override
            public void changed(ObservableValue<? extends Chat> observableValue, Chat item, Chat t1) {
                var curr=chatsListView.getSelectionModel().getSelectedItem();
                //System.out.println(curr.toString());

                try {
                    messageList=service.getMessages(curr.getId(),1);
                } catch (IOException e) {
                    messageList=new ArrayList<>();
                }
                messagesListView.setItems(FXCollections.observableList(messageList));

                //TODO update messageListView
            }
        });
        //chatsListView.init();

        //chatsListView.setItems(FXCollections.observableArrayList(chatList));

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
    void update() {
        try {
            chatList = service.getChats(1);
        }
        catch(IOException e)
        {
            showError("Ошибка при получении чатов" + e.getMessage() + " !");
            return;
        }

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
        nstage.setTitle("Create_chat");
        conC.setCurrStage(nstage);
        conC.setPrevController(this);
        conC.setPrimaryStage(this.currStage);
        conC.init();

        nstage.show();
        chatList=service.getChats(1);
        //update();
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

        conC.setCredentials(service.getCredentialsProvider() ,service.getLogin());
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
        var curr=chatsListView.getSelectionModel().getSelectedItem();
        try {
            service.sendMessage(curr.getId(),messageTextArea.getText());
        }
        catch (IOException e)
        {
            showError("Error occured during send message! "+e.getMessage()+" !");
            return;
        }


        //System.out.println(curr.toString());

        try {
            messageList=service.getMessages(curr.getId(),1);
        } catch (IOException e) {
            messageList=new ArrayList<>();
        }
        messagesListView.setItems(FXCollections.observableList(messageList));
    }

    public void scrollMethod(ScrollEvent scrollEvent) {
    }
}
