package ru.edu.spbstu.client.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import ru.edu.spbstu.client.services.ChatFormService;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    ContextMenu contextMenu = new ContextMenu();


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
        javafx.scene.control.MenuItem menuItem1 = new javafx.scene.control.MenuItem("Выйти из чата");
        javafx.scene.control.MenuItem menuItem2 = new javafx.scene.control.MenuItem("Настройки чата");

        menuItem1.setOnAction(this::exitAction);
        menuItem2.setOnAction(this::configAction);
        contextMenu.getItems().add(menuItem1);
        contextMenu.getItems().add(menuItem2);


        chatsListView.setCellFactory(lv -> {

            ListCell<Chat> cell = new ListCell<>();

            cell.textProperty().bind(Bindings.createStringBinding(
                    () -> Objects.toString(cell.getItem(), ""),
                    cell.itemProperty()));

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

                        @Override
                        public void handle(ContextMenuEvent event) {
                            Bounds boundsInScreen = chatsListView.localToScreen(chatsListView.getBoundsInLocal());
                            double y=event.getScreenY();
                            double h=cell.heightProperty().get();
                            y=(h)*Math.round(y/(h+1));

                            contextMenu.show(chatsListView, boundsInScreen.getMaxX() , y);
                        }
                    });
                }
            });
            return cell ;
        });




    }
    private void exitAction(javafx.event.ActionEvent actionEvent) {
        Long chatId=chatsListView.getSelectionModel().getSelectedItem().getId();

        String login=service.getLogin();
        try {
            service.leaveChat(chatId,login);
        } catch (IOException e) {
            showError("Error while delete ! + "+ e.getMessage()+" !");
            return;
        }
        try {
            chatList=service.getChats(1);
        } catch (IOException e) {
            showError("Error while getChats ! + "+ e.getMessage()+" !");
            return;
        }
        chatsListView.setItems(FXCollections.observableList(chatList));
        messagesListView.setItems(FXCollections.observableList(new ArrayList<>()));
        contextMenu.hide();


    }

    private void configAction(javafx.event.ActionEvent actionEvent) {
        try {
        System.out.println("Edit chat from chat");
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/configure_chat_form.fxml"));
        Parent window = null;

            window = (Pane) fmxlLoader.load();

        ConfigureChatFormController confController = fmxlLoader.getController();
        Scene scene = new Scene(window,700,700);

        confController.setCredentials(this.service.getCredentialsProvider(),this.service.getLogin());
        confController.setChat(chatsListView.getSelectionModel().getSelectedItem());

        Stage nstage= new Stage();
        nstage.setScene(scene);
        nstage.setTitle("ChatConfiguration");
        confController.setCurrStage(nstage);
        confController.setPrimaryStage(this.currStage);
        confController.init();

        nstage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        contextMenu.hide();

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


        chatsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                var curr=chatsListView.getSelectionModel().getSelectedItem();
                if(curr!=null) {

                    try {

                        messageList = service.getMessages(curr.getId(), 1);

                    } catch (IOException e) {
                        messageList = new ArrayList<>();
                    }
                    messagesListView.setItems(FXCollections.observableList(messageList));
                }
                else
                {
                    messagesListView.setItems(FXCollections.observableList(new ArrayList<>()));
                }
            }
        });
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
            showError("Ошибка при поулчении чатов" +e.getMessage()+" !");
            return;
        }

        chatsListView.setItems(FXCollections.observableArrayList(chatList));
    }

    public void addChatButtonClick(ActionEvent actionEvent) throws IOException {

        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/create_chat_form.fxml"));
        Parent window = (Pane) fmxlLoader.load();
        var conC = fmxlLoader.<CreateChatFormController>getController();
        Scene scene = new Scene(window,700,700);

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
        //TODo поиск чатов сделать
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
