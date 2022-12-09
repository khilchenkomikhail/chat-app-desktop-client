package ru.edu.spbstu.client.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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


    public ListView<Chat> chatsListView;
    public TextField newChatTextBox;
    public Button sendMessageButton;
    public TextArea messageTextArea;
    public ListView<Message> messagesListView;
    private ChatFormService service = new ChatFormService();
    private Stage primaryStage;
    private Stage currStage;
    private List<Chat> chatList;
    private List<Chat> foundChatsList;
    private List<Message> messageList;

    public Button addChatButton;
    public Button profileButton;
    public ComboBox<Button> LanguageComboBox;
    public Button logOutButton;
    boolean findMode=false;//пока я решил, что в заивимости от режима у нас буду заполняться список чатов
    private int chatsPage = 1;
    private int chatsOffset = 0;
    private int chatsFindPage = 1;
    private int chatsFindOffset = 0;
    private int messagesPage = 1;
    private int messageOffset = 0;

    final int MESSAGE_PAGE_SIZE = 50;
    final int CHATS_PAGE_SIZE = 20;

    private final ContextMenu contextMenu = new ContextMenu();
    private final ContextMenu messageMenu = new ContextMenu();
    private final ContextMenu messageMenu2 = new ContextMenu();

    private ListCell<Message> cellListFiller() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean bln) {
                super.updateItem(message, bln);
                setGraphic(null);
                setText(null);

                if (message != null) {

                    HBox box = new HBox();

                    GridPane pane = new GridPane();

                    pane.setPrefWidth(500.0);
                    ColumnConstraints column1 = new ColumnConstraints();

                    column1.setHgrow(Priority.SOMETIMES);
                    column1.setMaxWidth(50);
                    column1.setMinWidth(50);
                    column1.setPrefWidth(50.0);


                    ColumnConstraints column2 = new ColumnConstraints();
                    column2.setHgrow(Priority.NEVER);
                    column2.setMaxWidth(-Double.MAX_VALUE);
                    column2.setMinWidth(-Double.MAX_VALUE);
                    ColumnConstraints column3 = new ColumnConstraints();
                    column3.setHgrow(Priority.SOMETIMES);
                    //column3.setMaxWidth(-Double.MAX_VALUE);
                    column3.setMinWidth(-Double.MAX_VALUE);

                    RowConstraints row1 = new RowConstraints();
                    row1.setMaxHeight(-Double.MAX_VALUE);
                    row1.setMinHeight(-Double.MAX_VALUE);
                    row1.setPrefHeight(50);
                    row1.setVgrow(Priority.SOMETIMES);

                    RowConstraints row2 = new RowConstraints();
                    row2.setMaxHeight(-1);
                    row2.setVgrow(Priority.SOMETIMES);

                    pane.getColumnConstraints().addAll(column1, column2, column3);
                    pane.getRowConstraints().addAll(row1, row2);

                    HBox imageHbox = new HBox();
                    imageHbox.setMaxHeight(40);
                    imageHbox.setMaxWidth(40);
                    imageHbox.setMinHeight(40);
                    imageHbox.setMinWidth(40);
                    ImageView pictureImageView = new ImageView();
                    Image image = service.getImage(message.getAuthor_login());
                    pictureImageView.setImage(image);
                    pictureImageView.setFitHeight(40);
                    pictureImageView.setFitWidth(40);
                    ProfileFormController.cropImageToRound(pictureImageView);

                    GridPane.setMargin(imageHbox, new Insets(5, 5, 5, 5));
                    imageHbox.getChildren().add(pictureImageView);
                    pane.add(imageHbox, 0, 0);


                    Label username = new Label(message.getAuthor_login());
                    GridPane.setHalignment(username, HPos.LEFT);
                    GridPane.setValignment(username, VPos.TOP);
                    GridPane.setMargin(username, new Insets(0, 20, 0, 5));
                    pane.add(username, 1, 0);


                    Label date = new Label(message.getDate().toString());
                    GridPane.setHalignment(date, HPos.LEFT);
                    GridPane.setValignment(date, VPos.TOP);
                    GridPane.setMargin(date, new Insets(0, 20, 0, 5));
                    pane.add(date, 2, 0);


                    Label messageContents = new Label(message.getContent());
                    if (message.getIs_deleted()) {
                        messageContents.setText("Message deleted");
                    }
                    if (message.getIs_edited()) {
                        String newMess = messageContents.getText();
                        newMess += " (ред.)";
                        //newMess+=" (ed.)";
                        messageContents.setText(newMess);
                    }
                    messageContents.setMinWidth(300);
                    messageContents.setMaxWidth(300);
                    messageContents.setPrefWidth(300.0);
                    messageContents.setWrapText(true);


                    GridPane.setValignment(messageContents, VPos.TOP);
                    GridPane.setMargin(messageContents, new Insets(20, 0, 20, 5));
                    pane.add(messageContents, 1, 0, 2, 2);
                    messageContents.setStyle("-fx-background-radius: 5");
                    imageHbox.setStyle("-fx-background-color: white");

                    pane.setStyle("-fx-background-color: #BEBEBE");
                    if (!message.getAuthor_login().equals(service.getLogin())) {
                        HBox.setMargin(pane, new Insets(0, 0, 0, 200));
                    }
                    box.getChildren().add(pane);

                    setGraphic(box);

                }
            }
        };
    }

    private final Callback<ListView<Message>, ListCell<Message>> messageCallback = new Callback<>() {
        @Override
        public ListCell<Message> call(ListView<Message> lv) {
            ListCell<Message> cell = cellListFiller();

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setOnContextMenuRequested(event -> {
                        String s1 = cell.getItem().getAuthor_login();
                        String s2 = service.getLogin();
                        if (cell.getItem().getIs_deleted()) {
                            return;
                        }
                        if (s1.equals(s2)) {
                            messageMenu.show(messagesListView.getScene().getWindow(), event.getScreenX(), event.getScreenY());
                        } else {
                            messageMenu2.show(messagesListView.getScene().getWindow(), event.getScreenX(), event.getScreenY());
                        }
                    });
                }
            });
            return cell;
        }

    };

    public void setCredentials(CredentialsProvider prov, String login) {
        service.setCredentialsProvider(prov, login);
    }

    private void showError(String errorText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(errorText);
        alert.show();
    }

    @FXML
    void initialize() {
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
                    cell.setOnContextMenuRequested(event -> contextMenu.show(chatsListView.getScene().getWindow(), event.getScreenX(), event.getScreenY()));
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

    }

    private void forwardMessageAction(ActionEvent actionEvent) {
        System.out.println("Forward");
        //TODo open forward message form
    }


    private void exitAction(javafx.event.ActionEvent actionEvent) {
        Chat chat=chatsListView.getSelectionModel().getSelectedItem();
        Long chatId = chatsListView.getSelectionModel().getSelectedItem().getId();
        String login = service.getLogin();
        try {
            service.leaveChat(chatId, login);
        } catch (IOException e) {
            showError("Error while delete ! + " + e.getMessage() + " !");
            return;
        }





        //List<Chat>temp;
        /*try {
            chatList = service.getChats(1);
        } catch (IOException e) {
            showError("Error while getChats ! + " + e.getMessage() + " !");
            return;
        }*/

        if(!findMode) {
            chatsListView.setItems(FXCollections.observableList(chatList));
            chatsOffset--;
            if(chatsOffset==-1)
            {
                chatsOffset = CHATS_PAGE_SIZE-1;
                chatsPage--;
            }
            chatList.remove(chat);
            chatsListView.setItems(FXCollections.observableList(chatList));
        }
        else
        {
            chatsFindOffset--;
            if(chatsFindOffset==-1)
            {
                chatsFindOffset = CHATS_PAGE_SIZE-1;
                chatsFindPage--;
            }
            foundChatsList.remove(chat);
            chatsListView.setItems(FXCollections.observableList(foundChatsList));
        }
        messagesListView.setItems(FXCollections.observableList(new ArrayList<>()));
        messageList = new ArrayList<>();
        contextMenu.hide();
    }

    private void configAction(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/configure_chat_form.fxml"));
            Parent window;
            window = fmxlLoader.load();
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
        messageTextArea.setText(messagesListView.getSelectionModel().getSelectedItem().getContent());
        sendMessageButton.setText("Отредактировать");
        sendMessageButton.setOnAction(this::editMessageButtonAction);
    }

    private void editMessageButtonAction(ActionEvent actionEvent) {
        var message = messagesListView.getSelectionModel().getSelectedItem();
        try {
            service.editMessage(message.getId(), messageTextArea.getText());
        } catch (IOException e) {
            showError("Внутрення ошибка сервера!");
            return;
        }

        // try {
        int id = messageList.indexOf(message);
        //Message temp=new Message();


        message.setIs_edited(true);
        message.setContent(messageTextArea.getText());
        messageList.set(id, message);

        //messagesListView.setItems(FXCollections.observableList(service.getMessages(chatsListView.getSelectionModel().getSelectedItem().getId(),1)));
        //todo 1-получить страницу сообщения 2-Узнать индекс в общем массиве, подгрузить(потребуется новый endpoint)
        //3 присвоить новое значение сообщения исходному
        //} catch (IOException e) {
        //     showError("Внутрення ошибка сервера!");
        // }
        sendMessageButton.setText("Отправить");
        messageTextArea.setText("");
        sendMessageButton.setOnAction(this::sendMessageButtonClick);
        messagesListView.setItems(FXCollections.observableList(messageList));
    }

    private void deleteMessageAction(javafx.event.ActionEvent actionEvent) {
        var message = messagesListView.getSelectionModel().getSelectedItem();
        try {
            service.deleteMessage(message.getId());
        } catch (IOException e) {
            showError("Внутрення ошибка сервера!");
            return;
        }
        //try {
        int id = messageList.indexOf(message);

        message.setIs_deleted(true);
        messageList.set(id, message);

        //messagesListView.setItems(FXCollections.observableList(service.getMessages(chatsListView.getSelectionModel().getSelectedItem().getId(),1)));
        //todo 1-получить страницу сообщения 2-Узнать индекс в общем массиве, подгрузить(потребуется новый endpoint)
        //3 присвоить новое значение сообщения исходному
       /* } catch (IOException e) {
            showError("Внутрення ошибка сервера!");
        }*/
        messagesListView.setItems(FXCollections.observableList(messageList));
    }

    void init() {
        try {
            chatList = service.getChats(1);
        } catch (HttpResponseException e) {
            showError(e.getReasonPhrase());
            logOutAction();
        } catch (IOException e) {
            showError("Внутренняя ошибка сервера!");
            //showError("Internal server error!");
            currStage.close();
            return;
        }
        chatsListView.setItems(FXCollections.observableList(chatList));
        chatsListView.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            var curr = chatsListView.getSelectionModel().getSelectedItem();
            if (curr != null) {

                try {
                    messageList = service.getMessages(curr.getId(), 1);
                } catch (IOException e) {
                    showError("Внутрення ошибка сервера!");
                    return;
                }
                messagesPage = 1;
                messageOffset = 0;
                messagesListView.setItems(FXCollections.observableList(messageList));
            } else {
                messagesListView.setItems(FXCollections.observableList(new ArrayList<>()));
            }
        });
        sendMessageButton.setDisable(true);


        messagesListView.setCellFactory(messageCallback);

        currStage.setOnCloseRequest(e -> {
            primaryStage.show();
            currStage.close();
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

    public void logOutMouseClick() {
        logOutAction();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setCurrStage(Stage currStage) {
        this.currStage = currStage;
    }

    public void addNewChat(String name) {
        List<Chat> temp;
        try {
            temp = service.find(name, 1L);
        } catch (IOException e) {
            showError("bad after add find ne w chat");
            return;
        }
        if(!findMode) {
            chatList.add(0, temp.get(0));
            chatsOffset++;
            if (chatsOffset == CHATS_PAGE_SIZE) {
                chatsOffset = 0;
                chatsPage++;
            }
            chatsListView.setItems(FXCollections.observableList(chatList));
        }
        else
        {
            foundChatsList.add(0, temp.get(0));
            chatsFindOffset++;
            if (chatsFindOffset == CHATS_PAGE_SIZE) {
                chatsFindOffset = 0;
                chatsFindPage++;
            }
            chatsListView.setItems(FXCollections.observableList(foundChatsList));
        }

    }

    void update() {
        try {

            chatList = service.getChats(1);//Todo вот тут надо получить(и добавлять к общему списку) последнюю страницу
        } catch (IOException e) {
            showError("Ошибка при получении чатов" + e.getMessage() + " !");
            return;
        }

        chatsListView.setItems(FXCollections.observableArrayList(chatList));
    }

    void loadMessagePage(Long chat_id) {
        try {
            messagesPage++;
            List<Message> temp = service.getMessages(chat_id, messagesPage);
           /* if(temp.size()<MESSAGE_PAGE_SIZE)//Todo page size
            {
                messagesPage--;

            }
            else
            {

            }*/
            if (temp.size() != 0) {
                temp = temp.subList(messageOffset, temp.size());
                messageOffset = 0;
                messageList.addAll(temp);
            } else {
                messagesPage--;
            }
        } catch (IOException e) {
            showError("Internal server error");
            messageList = new ArrayList<>();
        }
        messagesListView.setItems(FXCollections.observableList(messageList));
    }

    void loadChatPage() {
        if (!findMode) {
            try {
                chatsPage++;
                List<Chat> temp = service.getChats(chatsPage);
            /*if(temp.size()<CHATS_PAGE_SIZE)
            {
                chatsPage--;

            }
            else
            {

            }*/
                if (temp.size() != 0) {
                    //int pageOffset=messageOffset/50;
                    // int messOff=messageOffset-pageOffset*50;
                    //  if(messagesPage)
                    temp = temp.subList(chatsOffset, temp.size());//apply offset
                    chatsOffset = 0;
                    chatList.addAll(temp);
                } else {
                    chatsPage--;
                }
            } catch (IOException e) {
                showError("Internal server error");
                chatList = new ArrayList<>();
            }
            chatsListView.setItems(FXCollections.observableList(chatList));
        }
        else
        {
            try {
                chatsFindPage++;
                List<Chat> temp = service.find(newChatTextBox.getText(), (long) chatsFindPage);
            /*if(temp.size()<CHATS_PAGE_SIZE)
            {
                chatsPage--;

            }
            else
            {

            }*/
                if (temp.size() != 0) {
                    //int pageOffset=messageOffset/50;
                    // int messOff=messageOffset-pageOffset*50;
                    //  if(messagesPage)
                    temp = temp.subList(chatsFindOffset, temp.size());//apply offset
                    chatsFindOffset = 0;
                    foundChatsList.addAll(temp);
                } else {
                    chatsFindPage--;
                }
            } catch (IOException e) {
                showError("Internal server error");
               foundChatsList= new ArrayList<>();
            }
            chatsListView.setItems(FXCollections.observableList(foundChatsList));
        }
    }



    public void addChatButtonClick() throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/create_chat_form.fxml"));
        Parent window = fmxlLoader.load();
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
        //chatList = service.getChats(1);
        this.currStage.hide();
    }

    public void findChatsEvent() {
        if(newChatTextBox.getText().length()==0)
        {
            findMode=false;
            foundChatsList=new ArrayList<>();
            chatsOffset=0;
            chatsPage=1;
            try {
                chatList=service.getChats(1);
            } catch (IOException e) {
                showError(e.getMessage());
                return;
            }
            chatsListView.setItems(FXCollections.observableList(chatList));

        }
        else
        {
            findMode=true;
            chatList=new ArrayList<>();
            chatsFindOffset=0;
            chatsFindPage=1;
            try {
                foundChatsList=service.find(newChatTextBox.getText(), 1L);
            } catch (IOException e) {
                showError(e.getMessage());
                return;
            }
            chatsListView.setItems(FXCollections.observableList(foundChatsList));
        }
        /*String name = newChatTextBox.getText();
        if (newChatTextBox.getText().length() == 1) {
            try {
                chatsListView.setItems(FXCollections.observableList(service.getChats(1)));
            } catch (IOException e) {
                showError("Внутренняя ошибка сервера!");
                return;
            }
        }

        List<Chat> temp;
        try {
            temp = service.find(name, 1L);
        } catch (IOException e) {
            showError("Внутренняя ошибка сервера!");
            return;
        }
        chatsListView.setItems(FXCollections.observableList(temp));*/
    }

    public void LanguageCBAction() {
    }

    public void sendMessageButtonClick(ActionEvent actionEvent) {
        var curr = chatsListView.getSelectionModel().getSelectedItem();
        try {
            service.sendMessage(curr.getId(), messageTextArea.getText());
        } catch (IOException e) {

            showError("Внутренняя ошибка сервера!");
            return;
        }

        //try {
        messageList.add(0, service.makeMessage(curr.getId(), messageTextArea.getText()));
        messageOffset++;
        if (messageOffset == MESSAGE_PAGE_SIZE) {
            messageOffset = 0;
            messagesPage++;
        }
        //messageList = service.getMessages(curr.getId(), 1);//Todo вот тут надо получить последнюю страницу
        //} catch (IOException e) {
        //  showError("Внутренняя ошибка сервера!");
        //     return;
        // }
        messagesListView.setItems(FXCollections.observableList(messageList));
        messageTextArea.setText("");
        sendMessageButton.setDisable(true);
    }

    public void scrollMethod(ScrollEvent scrollEvent) {
        if (scrollEvent.getDeltaY() < 0) {
            //messagesPage++;
            loadChatPage();
        }
    }


    public void messagesScroll(ScrollEvent scrollEvent) {
        if (scrollEvent.getDeltaY() < 0) {
            if(chatsListView.getSelectionModel().getSelectedItem()!=null) {
                //messagesPage++;
                loadMessagePage(chatsListView.getSelectionModel().getSelectedItem().getId());
            }
        }
        //System.out.println("message scroll");
    }


    public void textAreaKeyTyped() {
        sendMessageButton.setDisable(messageTextArea.getText().length() == 0);
    }

    public void ProfileButtonMouseClick() throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/profile_form.fxml"));
        Parent window = fmxlLoader.load();
        var conC = fmxlLoader.<ProfileFormController>getController();
        Scene scene = new Scene(window);

        conC.setCredentials(service.getCredentialsProvider(), service.getLogin());
        conC.setPrevController(this);
        Stage nstage = new Stage();
        nstage.setScene(scene);
        nstage.setTitle("Profile");
        conC.setCurrStage(nstage);
        conC.setPrimaryStage(this.currStage);
        conC.init();
        nstage.show();
        this.currStage.hide();
    }


}
