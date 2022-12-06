package ru.edu.spbstu.client.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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


    public ListView<Chat> chatsListView;
    public TextField newChatTextBox;
    public Button sendMessageButton;
    public TextArea messageTextArea;
    public ListView<Message> messagesListView;
    private ChatFormService service = new ChatFormService();
    private Stage primaryStage;
    private Stage currStage;
    private List<Chat> chatList;
    private List<Message> messageList;

    public Button addChatButton;
    public Button profileButton;
    public ComboBox<Button> LanguageComboBox;
    public Button logOutButton;

    private final ContextMenu contextMenu = new ContextMenu();
    private final ContextMenu messageMenu=new ContextMenu();
    private final ContextMenu messageMenu2=new ContextMenu();

    public void setCredentials(CredentialsProvider prov, String login) {
        service.setCredentialsProvider(prov, login);
    }

    private void showError(String errorText) {
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
                            contextMenu.show(chatsListView.getScene().getWindow(), event.getScreenX(), event.getScreenY());
                        }
                    });
                }
            });
            return cell;
        });

        javafx.scene.control.MenuItem menuItem21 = new javafx.scene.control.MenuItem("Отредактировать сообщение");
        javafx.scene.control.MenuItem menuItem22 = new javafx.scene.control.MenuItem("Удалить сообщение");
        javafx.scene.control.MenuItem menuItem23 = new javafx.scene.control.MenuItem("Переслать сообщение");
        menuItem21.setOnAction(this::editMessageAction);
        menuItem22.setOnAction(this::deleteMessageAction);
        menuItem23.setOnAction(this::forwardMessageAction);
        messageMenu.getItems().add(menuItem21);
        messageMenu.getItems().add(menuItem22);
        messageMenu.getItems().add(menuItem23);

        javafx.scene.control.MenuItem menu3 = new javafx.scene.control.MenuItem("Переслать сообщение");
        menu3.setOnAction(this::forwardMessageAction);
        messageMenu2.getItems().add(menu3);

        messagesListView.setCellFactory(lv -> {

            ListCell<Message> cell = new ListCell<>();

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
                            String s1=cell.getItem().getAuthor_login();
                            String s2=service.getLogin();
                            if(s1.equals(s2)) {
                                messageMenu.show(messagesListView.getScene().getWindow(), event.getScreenX(), event.getScreenY());
                            }
                            else
                            {
                                messageMenu2.show(messagesListView.getScene().getWindow(), event.getScreenX(), event.getScreenY());
                            }
                        }
                    });
                }
            });
            return cell;
        });


    }

    private void forwardMessageAction(ActionEvent actionEvent) {
        System.out.println("Forward");
        //TODo open forward message form
    }


    private void exitAction(javafx.event.ActionEvent actionEvent) {
        Long chatId = chatsListView.getSelectionModel().getSelectedItem().getId();
        String login = service.getLogin();
        try {
            service.leaveChat(chatId, login);
        } catch (IOException e) {
            showError("Error while delete ! + " + e.getMessage() + " !");
            return;
        }
        try {
            chatList = service.getChats(1);
        } catch (IOException e) {
            showError("Error while getChats ! + " + e.getMessage() + " !");
            return;
        }
        chatsListView.setItems(FXCollections.observableList(chatList));
        messagesListView.setItems(FXCollections.observableList(new ArrayList<>()));
        contextMenu.hide();
    }

    private void configAction(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/configure_chat_form.fxml"));
            Parent window = null;
            window = (Pane) fmxlLoader.load();
            var conC = fmxlLoader.<ConfigureChatFormController>getController();
            Scene scene = new Scene(window, 700, 500);
            conC.setCredentials(this.service.getCredentialsProvider(), this.service.getLogin());
            Stage nstage = new Stage();
            nstage.setScene(scene);
            nstage.setTitle("Chat configuration");
            var curr = chatsListView.getSelectionModel().getSelectedItem();
            conC.setChat(curr);
            conC.setCurrStage(nstage);
            conC.setPrimaryStage(this.currStage);
            conC.init();
            nstage.show();
            this.currStage.hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
        contextMenu.hide();

    }


    private void editMessageAction(javafx.event.ActionEvent actionEvent) {
        System.out.println("Edit message!");
        messageTextArea.setText(messagesListView.getSelectionModel().getSelectedItem().getContent());
        sendMessageButton.setText("Отредактировать");
        sendMessageButton.setOnAction(this::editMessageButtonAction);

        try {
            service.deleteMessage(messagesListView.getSelectionModel().getSelectedItem().getId());
        }
        catch (IOException e)
        {
            showError("Error while delete message! "+e.getMessage());
        }

    }

    private void editMessageButtonAction(ActionEvent actionEvent) {
        try {
            service.editMessage(messagesListView.getSelectionModel().getSelectedItem().getId(),messageTextArea.getText());
        } catch (IOException e) {
            showError("Error while edit message button");
        }
        sendMessageButton.setText("Отправить");
        messageTextArea.setText("");
        sendMessageButton.setOnAction(this::sendMessageButtonClick);
        try {
            messagesListView.setItems(FXCollections.observableList(service.getMessages(chatsListView.getSelectionModel().getSelectedItem().getId(),1)));
        } catch (IOException e) {
            showError("Error while delete meessage! "+e.getMessage());
            return;
        }
    }

    private void deleteMessageAction(javafx.event.ActionEvent actionEvent) {
        System.out.println("Delete message");
        try {
            service.deleteMessage(messagesListView.getSelectionModel().getSelectedItem().getId());
        }
        catch (IOException e)
        {
            showError("Error while delete meessage! "+e.getMessage());
            return;
        }
        try {
            messagesListView.setItems(FXCollections.observableList(service.getMessages(chatsListView.getSelectionModel().getSelectedItem().getId(),1)));
        } catch (IOException e) {
            showError("Error while delete meessage! "+e.getMessage());
            return;
        }
    }

    void init() {
        try {
            chatList = service.getChats(1);
        } catch (HttpResponseException e) {
            showError(e.getReasonPhrase());
            logOutAction();
        } catch (IOException e) {
            showError("Internal server error!");
        }
        chatsListView.setItems(FXCollections.observableList(chatList));
        chatsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getButton()!= MouseButton.PRIMARY)
                {
                    //messagesListView.setItems(FXCollections.observableList(new ArrayList<>()));
                    return;
                }
                var curr = chatsListView.getSelectionModel().getSelectedItem();
                if (curr != null) {

                    try {
                        messageList = service.getMessages(curr.getId(), 1);
                    } catch (IOException e) {
                        messageList = new ArrayList<>();
                    }
                    messagesListView.setItems(FXCollections.observableList(messageList));
                } else {
                    messagesListView.setItems(FXCollections.observableList(new ArrayList<>()));
                }
            }
        });
        sendMessageButton.setDisable(true);
        currStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                primaryStage.show();
                currStage.close();
            }
        });
    }


    private void logOutAction() {
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
        } catch (IOException e) {
            showError("Ошибка при получении чатов" + e.getMessage() + " !");
            return;
        }

        chatsListView.setItems(FXCollections.observableArrayList(chatList));
    }

    public void addChatButtonClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/create_chat_form.fxml"));
        Parent window = (Pane) fmxlLoader.load();
        var conC = fmxlLoader.<CreateChatFormController>getController();
        Scene scene = new Scene(window, 700, 500);
        conC.setCredentials(this.service.getCredentialsProvider(), this.service.getLogin());

        Stage nstage = new Stage();
        nstage.setScene(scene);
        nstage.setTitle("Chat creation");
        conC.setCurrStage(nstage);
        conC.setPrevController(this);
        conC.setPrimaryStage(this.currStage);
        conC.init();

        nstage.show();
        chatList = service.getChats(1);
        this.currStage.hide();
    }


    public void findChatsEvent(KeyEvent keyEvent) {
        String name = newChatTextBox.getText();

        List<Chat> temp = service.find(name);
    }

    public void LanguageCBAction(ActionEvent actionEvent) {
    }

    public void sendMessageButtonClick(ActionEvent actionEvent) {
        var curr = chatsListView.getSelectionModel().getSelectedItem();
        try {
            service.sendMessage(curr.getId(), messageTextArea.getText());
        } catch (IOException e) {
            showError("Error occurred when sending the message! " + e.getMessage() + " !");
            return;
        }

        try {
            messageList = service.getMessages(curr.getId(), 1);
        } catch (IOException e) {
            messageList = new ArrayList<>();
        }
        messagesListView.setItems(FXCollections.observableList(messageList));
        messageTextArea.setText("");
        sendMessageButton.setDisable(true);
    }

    public void scrollMethod(ScrollEvent scrollEvent) {
    }

    public void textAreaKeyTyped(KeyEvent keyEvent) {
        if (messageTextArea.getText().length() == 0) {
            sendMessageButton.setDisable(true);
        } else {
            sendMessageButton.setDisable(false);
        }
    }

    public void ProfileButtonMouseClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/profile_form.fxml"));
        Parent window = (Pane) fmxlLoader.load();
        var conC = fmxlLoader.<ChatFormController>getController();//TODO поменять
        Scene scene = new Scene(window, 700, 700);

        conC.setCredentials(service.getCredentialsProvider(), service.getLogin());
        conC.init();

        Stage nstage = new Stage();
        nstage.setScene(scene);
        nstage.setTitle("Profile");
        conC.setCurrStage(nstage);
        conC.setPrimaryStage(this.currStage);

        nstage.show();
    }
}
