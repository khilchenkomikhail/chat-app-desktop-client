package ru.edu.spbstu.client.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.http.client.HttpResponseException;
import ru.edu.spbstu.client.services.ChatFormService;
import ru.edu.spbstu.client.utils.ClientProperties;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.ChatUser;
import ru.edu.spbstu.model.Message;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static ru.edu.spbstu.utils.ImageUtils.clipImageRound;

public class ChatFormController {
    public enum Mode {
        SEND,
        EDIT,
        REPLY,
    }



    private static HashMap<String, String> languageCBtoProperty = HashMap.newHashMap(2);
    public ListView<Chat> chatsListView;
    public TextField findChatTextBox;
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
    public ComboBox<String> LanguageComboBox;
    public Button logOutButton;
    //boolean EditMode = false;
    private Mode sendButtonMode=Mode.SEND;
    private int messageToEdit = -1;
    private Message messageToEditVal;

    boolean findMode = false;//пока я решил, что в заивимости от режима у нас буду заполняться список чатов
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
    private ResourceBundle bundle;

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    private void resetMessages() {
        messageList = new ArrayList<>();
        messagesListView.setItems(FXCollections.observableList(messageList));
        messagesPage = 1;
        messageOffset = 0;
    }

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
                    column3.setMinWidth(100);

                    RowConstraints row1 = new RowConstraints();
                    row1.setMaxHeight(-Double.MAX_VALUE);
                    row1.setValignment(VPos.TOP);
                   // row1.setMinHeight(50);
                    //row1.setPrefHeight(50);//Todo перепроверить
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
                    Image image = null;
                    try {
                        image = service.getImage(message.getSender_login());
                    } catch (IOException e) {
                        image = new Image((getClass().getResource("/images/dAvatar.bmp")).getPath().replaceFirst("/", ""));
                    }
                    pictureImageView.setImage(image);

                    pictureImageView.setFitHeight(40);
                    pictureImageView.setFitWidth(40);
                    clipImageRound(pictureImageView);

                    GridPane.setMargin(imageHbox, new Insets(5, 5, 5, 5));
                    imageHbox.getChildren().add(pictureImageView);
                    imageHbox.setAlignment(Pos.TOP_LEFT);
                    pane.add(imageHbox, 0, 0);

                    String userText = message.getSender_login();

                    if (message.getIs_forwarded()) {
                        userText = userText + " " + bundle.getString("ForwardText")
                                + " " + message.getAuthor_login();
                    }
                    Label username = new Label(userText);

                    //username.setFont(new Font(10));
                    username.setWrapText(true);
                    GridPane.setHalignment(username, HPos.LEFT);
                    GridPane.setValignment(username, VPos.TOP);
                    GridPane.setMargin(username, new Insets(0, 0, 20, 5));
                    pane.add(username, 1, 0);

                    Date dat=message.getDate();

                    LocalDate localDate = dat.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int year  = localDate.getYear();
                    int month = localDate.getMonthValue();
                    int day   = localDate.getDayOfMonth();
                    String minute=(dat.getMinutes()<10)?String.format("0%d",dat.getMinutes()):
                            String.format("%d",dat.getMinutes());
                    String hour=(dat.getHours()<10)?String.format("0%d",dat.getHours()):
                            String.format("%d",dat.getHours());
                    //dat.toString()
                    String date2=(String.format("%d.%d.%d %s:%s",day,month,year,
                            hour,minute));
                    Label date = new Label(date2);
                    GridPane.setHalignment(date, HPos.RIGHT);
                    GridPane.setValignment(date, VPos.TOP);
                    if (message.getIs_forwarded()) {
                      //  date.setFont(new Font(10));
                      //  username.setFont(new Font(10));
                    }
                    GridPane.setMargin(date, new Insets(0, 5, 0, 0));
                    pane.add(date, 2, 0);


                    Label messageContents = new Label(message.getContent());
                    if (message.getIs_deleted()) {
                        messageContents.setText(bundle.getString("MessageDeleted"));
                    }
                    if (message.getIs_edited()) {
                        String newMess = messageContents.getText();
                        newMess += bundle.getString("EditedText");
                        messageContents.setText(newMess);
                    }
                    messageContents.setMinWidth(300);
                    messageContents.setMaxWidth(300);
                    messageContents.setPrefWidth(300.0);
                    messageContents.setWrapText(true);


                    GridPane.setValignment(messageContents, VPos.TOP);
                    int multiplier=userText.length()/60;

                    GridPane.setMargin(messageContents, new Insets(35+multiplier*20, 0, 20, 5));
                    pane.add(messageContents, 1, 0, 2, 2);
                    messageContents.setStyle("-fx-background-radius: 5");
                    imageHbox.setStyle("-fx-background-color: white");

                    pane.setStyle("-fx-background-color: #BEBEBE");
                    if (!message.getSender_login().equals(service.getLogin())) {
                        HBox.setMargin(pane, new Insets(0, 0, 0, 180));
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
                        if (s1.equals(s2)&&!cell.getItem().getIs_forwarded()) {
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

    public void setLogin(String login) {
        service.setCredentialsProvider(login);
    }

    private void showError(String errorText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(bundle.getString("Error"));
        alert.setHeaderText(errorText);
        alert.show();
    }

    @FXML
    void initialize() {
    }

    private void forwardMessageAction(ActionEvent actionEvent) {
        if (sendButtonMode!=Mode.SEND) {
            switchToSendMode();
        }
        System.out.println("Forward");
        //TODo open forward message form
    }


    private void exitAction(javafx.event.ActionEvent actionEvent) {
        if (sendButtonMode!=Mode.SEND) {
            switchToSendMode();
        }
        Chat chat = chatsListView.getSelectionModel().getSelectedItem();
        Long chatId = chatsListView.getSelectionModel().getSelectedItem().getId();
        String login = service.getLogin();
        List<ChatUser> userlist;
        try {
            userlist = service.getChatMembers(chat);
        } catch (IOException e) {
            showError(bundle.getString("InternalErrorText"));
            return;
        }
        try {
            if (userlist.size() <= 1) {
                service.deleteChat(chat);
            }
            else {
                var admins=userlist.stream().filter(ChatUser::getIs_admin).toList();
                var users=userlist.stream().filter(user-> !user.getIs_admin()).toList();
                boolean userL=admins.contains(new ChatUser(login,true));

                if(!userL) {
                    service.leaveChat(chatId, login);
                }
                else
                {
                    if(admins.size()==1)
                    {
                        Random rand = new Random();
                        ChatUser randomUser = users.get(rand.nextInt(users.size()));
                        service.setChatUsersAdmins(chat,randomUser.getLogin());
                    }
                    service.leaveChat(chatId, login);
                }
            }
        } catch (IOException e) {
            showError(bundle.getString("InternalErrorText"));
            return;
        }

        if (!findMode) {
            chatsListView.setItems(FXCollections.observableList(chatList));
            chatsOffset--;
            if (chatsOffset == -1) {
                chatsOffset = CHATS_PAGE_SIZE - 1;
                chatsPage--;
            }
            chatList.remove(chat);
            chatsListView.setItems(FXCollections.observableList(chatList));
        } else {
            chatsFindOffset--;
            if (chatsFindOffset == -1) {
                chatsFindOffset = CHATS_PAGE_SIZE - 1;
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
        if (sendButtonMode!=Mode.SEND) {
            switchToSendMode();
        }
        try {
            FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/configure_chat_form.fxml"), bundle);
            Parent window;
            window = fmxlLoader.load();
            var conC = fmxlLoader.<ConfigureChatFormController>getController();
            conC.setBundle(bundle);
            Scene scene = new Scene(window, 700, 500);
            conC.setLogin(this.service.getLogin());
            Stage nstage = new Stage();
            nstage.setScene(scene);

            nstage.setTitle(bundle.getString("ConfigChatFormName"));
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
    private void replyMessageAction(ActionEvent actionEvent) {
        if(sendButtonMode!=Mode.SEND)
        {
            switchToSendMode();
        }
        sendMessageButton.setText(bundle.getString("AnswerMessageButton"));
        sendMessageButton.setOnAction(this::replyMessageButtonAction);
        messageToEdit = messagesListView.getSelectionModel().getSelectedIndex();
        messageToEditVal = messagesListView.getSelectionModel().getSelectedItem();
        sendButtonMode=Mode.REPLY;
    }

    private void replyMessageButtonAction(ActionEvent actionEvent) {
        if(messageTextArea.getText().length()>512)
        {
            showError(bundle.getString("MessageToBigError"));
            return;
        }
        var currChat=chatsListView.getSelectionModel().getSelectedItem();
        try {
            service.forwardMessage(messageToEditVal.getId(),service.getLogin(),currChat.getId());
        }
        catch (IOException ex)
        {
            showError(bundle.getString("InternalErrorText"));
            return;
        }

        List<Message> temp;
        try {
            temp = service.getMessages(currChat.getId(), 1);
        } catch (IOException e) {

            showError(bundle.getString("InternalErrorText"));
            return;
        }



        messageList.add(0, temp.get(0));
        messageOffset++;
        if (messageOffset == MESSAGE_PAGE_SIZE) {
            messageOffset = 0;
            messagesPage++;
        }
        switchToSendMode();
        sendMessageButtonClick(actionEvent);
    }


    private void editMessageAction(javafx.event.ActionEvent actionEvent) {
        if(sendButtonMode!=Mode.SEND)
        {
            switchToSendMode();
        }
        messageTextArea.setText(messagesListView.getSelectionModel().getSelectedItem().getContent());
        sendMessageButton.setText(bundle.getString("editMessageButton"));
        sendMessageButton.setOnAction(this::editMessageButtonAction);
        messageToEdit = messagesListView.getSelectionModel().getSelectedIndex();
        messageToEditVal = messagesListView.getSelectionModel().getSelectedItem();
        sendButtonMode=Mode.EDIT;
        //EditMode = true;
    }
    private void switchToSendMode() {
        sendMessageButton.setText(bundle.getString("SendMessageButton"));
        if(sendButtonMode==Mode.EDIT)
            messageTextArea.setText("");
        sendMessageButton.setDisable(true);
        sendMessageButton.setOnAction(this::sendMessageButtonClick);

        //EditMode = false;
        sendButtonMode=Mode.SEND;
        messageToEdit = -1;
        messageToEditVal = null;
    }

    private void editMessageButtonAction(ActionEvent actionEvent) {
        if(messageTextArea.getText().length()>512)
        {
            showError(bundle.getString("MessageToBigError"));
            return;
        }
        Message message = messageToEditVal;
        try {
            service.editMessage(message.getId(), messageTextArea.getText());
        } catch (IOException e) {
            showError(bundle.getString("InternalErrorText"));
            return;
        }
        int id = messageList.indexOf(message);
        message.setIs_edited(true);
        message.setContent(messageTextArea.getText());
        messageList.set(id, message);
        switchToSendMode();
        messagesListView.setItems(FXCollections.observableList(messageList));

    }

    private void deleteMessageAction(javafx.event.ActionEvent actionEvent) {
        if (sendButtonMode!=Mode.SEND) {
            switchToSendMode();
        }
        var message = messagesListView.getSelectionModel().getSelectedItem();
        try {
            service.deleteMessage(message.getId());
        } catch (IOException e) {
            showError(bundle.getString("InternalErrorText"));
            return;
        }
        int id = messageList.indexOf(message);

        message.setIs_deleted(true);
        messageList.set(id, message);
        messagesListView.setItems(FXCollections.observableList(messageList));
    }

    void init() {
        languageCBtoProperty.put(bundle.getString("RusLangOption"), "RU");
        languageCBtoProperty.put(bundle.getString("EngLangOption"), "EN");
        LanguageComboBox.getItems().add(bundle.getString("RusLangOption"));
        LanguageComboBox.getItems().add(bundle.getString("EngLangOption"));
        javafx.scene.control.MenuItem menuItem1 = new javafx.scene.control.MenuItem(bundle.getString("ExitChatButton"));
        javafx.scene.control.MenuItem menuItem2 = new javafx.scene.control.MenuItem(bundle.getString("ConfigChat"));
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


        javafx.scene.control.MenuItem menuItem21 = new javafx.scene.control.MenuItem(bundle.getString("EditMessageButton"));
        javafx.scene.control.MenuItem menuItem22 = new javafx.scene.control.MenuItem(bundle.getString("DeleteMessageButton"));
        javafx.scene.control.MenuItem menuItem23 = new javafx.scene.control.MenuItem(bundle.getString("ForwardMessageButton"));
        javafx.scene.control.MenuItem menuItem24 = new javafx.scene.control.MenuItem(bundle.getString("AnswerMessageButton"));
        menuItem21.setOnAction(this::editMessageAction);
        menuItem22.setOnAction(this::deleteMessageAction);
        menuItem23.setOnAction(this::forwardMessageAction);
        menuItem24.setOnAction(this::replyMessageAction);
        messageMenu.getItems().addAll(menuItem21,menuItem22,menuItem23,menuItem24);


        javafx.scene.control.MenuItem menu31 = new javafx.scene.control.MenuItem(bundle.getString("ForwardMessageButton"));
        javafx.scene.control.MenuItem menu32 = new javafx.scene.control.MenuItem(bundle.getString("AnswerMessageButton"));
        menu31.setOnAction(this::forwardMessageAction);
        menu32.setOnAction(this::replyMessageAction);
        messageMenu2.getItems().addAll(menu31,menu32);

        try {
            chatList = service.getChats(1);
        } catch (HttpResponseException e) {
            showError(bundle.getString("InternalErrorText") + "Http code = " + e.getStatusCode() + "!");
            logOutAction();
        } catch (IOException e) {
            showError(bundle.getString("InternalErrorText"));
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
                    showError(bundle.getString("InternalErrorText"));
                    return;
                }
                messagesPage = 1;
                messageOffset = 0;
                messagesListView.setItems(FXCollections.observableList(messageList));
                if (sendButtonMode!=Mode.SEND) {
                    switchToSendMode();
                }
            } else {
                messagesListView.setItems(FXCollections.observableList(new ArrayList<>()));
            }
        });
        sendMessageButton.setDisable(true);

        currStage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                if (sendButtonMode!=Mode.SEND) {
                    switchToSendMode();
                }
            }
        });

        messagesListView.setCellFactory(messageCallback);

        currStage.setOnCloseRequest(e -> {
            primaryStage.show();
            currStage.close();
        });
       // sendMessageButton.setDisable(true);
        /*currStage.setOnCloseRequest(e -> {
            primaryStage.show();
            currStage.close();
        });*/
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
            showError(bundle.getString("InternalErrorText"));
            return;
        }
        if (!findMode) {
            chatList.add(0, temp.get(0));
            chatsOffset++;
            if (chatsOffset == CHATS_PAGE_SIZE) {
                chatsOffset = 0;
                chatsPage++;
            }
            chatsListView.setItems(FXCollections.observableList(chatList));
        } else {
            foundChatsList.add(0, temp.get(0));
            chatsFindOffset++;
            if (chatsFindOffset == CHATS_PAGE_SIZE) {
                chatsFindOffset = 0;
                chatsFindPage++;
            }
            chatsListView.setItems(FXCollections.observableList(foundChatsList));
        }

    }

    @Deprecated
    void update() {
        try {

            chatList = service.getChats(1);
        } catch (IOException e) {
            bundle.getString("InternalErrorText");
            //showError("Ошибка при получении чатов" + e.getMessage() + " !");
            return;
        }

        chatsListView.setItems(FXCollections.observableArrayList(chatList));
    }

    void loadMessagePage(Long chat_id) {
        try {
            messagesPage++;
            List<Message> temp = service.getMessages(chat_id, messagesPage);
            if (temp.size() != 0) {
                temp = temp.subList(messageOffset, temp.size());
                messageOffset = 0;
                messageList.addAll(temp);
            } else {
                messagesPage--;
            }
        } catch (IOException e) {
            showError(bundle.getString("InternalErrorText"));
            messageList = new ArrayList<>();
        }
        messagesListView.setItems(FXCollections.observableList(messageList));
    }

    void loadChatPage() {
        if (!findMode) {
            try {
                chatsPage++;
                List<Chat> temp = service.getChats(chatsPage);
                if (temp.size() != 0) {
                    temp = temp.subList(chatsOffset, temp.size());//apply offset
                    chatsOffset = 0;
                    chatList.addAll(temp);
                } else {
                    chatsPage--;
                }
            } catch (IOException e) {
                showError(bundle.getString("InternalErrorText"));
                chatList = new ArrayList<>();
            }
            chatsListView.setItems(FXCollections.observableList(chatList));
        } else {
            try {
                chatsFindPage++;
                List<Chat> temp = service.find(findChatTextBox.getText(), (long) chatsFindPage);
                if (temp.size() != 0) {
                    temp = temp.subList(chatsFindOffset, temp.size());//apply offset
                    chatsFindOffset = 0;
                    foundChatsList.addAll(temp);
                } else {
                    chatsFindPage--;
                }
            } catch (IOException e) {
                showError(bundle.getString("InternalErrorText"));
                foundChatsList = new ArrayList<>();
            }
            chatsListView.setItems(FXCollections.observableList(foundChatsList));
        }
    }


    public void addChatButtonClick() throws IOException {
        if (sendButtonMode!=Mode.SEND) {
            switchToSendMode();
        }
        resetMessages();
        chatsListView.getSelectionModel().select(-1);
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/create_chat_form.fxml"), bundle);
        Parent window = fmxlLoader.load();
        var conC = fmxlLoader.<CreateChatFormController>getController();
        conC.setBundle(bundle);
        Scene scene = new Scene(window, 700, 500);
        conC.setLogin(this.service.getLogin());

        Stage nstage = new Stage();
        nstage.setScene(scene);
        nstage.setTitle(bundle.getString("ChatCreationForm"));
        conC.setCurrStage(nstage);
        conC.setPrevController(this);
        conC.setPrimaryStage(this.currStage);
        conC.init();

        nstage.show();
        this.currStage.hide();
    }

    public void findChatsEvent() {
        if (findChatTextBox.getText().length() == 0) {//todo пробелы в названиях?
            findMode = false;
            foundChatsList = new ArrayList<>();
            chatsOffset = 0;
            chatsPage = 1;
            try {
                chatList = service.getChats(1);
            } catch (IOException e) {
                showError(e.getMessage());
                return;
            }
            chatsListView.setItems(FXCollections.observableList(chatList));

        } else {
            findMode = true;
            chatList = new ArrayList<>();
            chatsFindOffset = 0;
            chatsFindPage = 1;
            try {
                foundChatsList = service.find(findChatTextBox.getText(), 1L);
            } catch (IOException e) {
                showError(e.getMessage());
                return;
            }
            chatsListView.setItems(FXCollections.observableList(foundChatsList));
        }
    }


    public void LanguageCBAction() {
        String prevLang = bundle.getLocale().getCountry();
        boolean rus = prevLang.equals("RU");
        String selected = LanguageComboBox.getSelectionModel().getSelectedItem();
        String nextLanguage = languageCBtoProperty.get(selected);
        if (!nextLanguage.equals(prevLang)) {
            try {
                ClientProperties.setProperties(nextLanguage);
            } catch (IOException e) {
                showError(bundle.getString("InternalErrorText"));
                return;
            }
            showError(bundle.getString("LanguageChange"));
        }
    }

    public void sendMessageButtonClick(ActionEvent actionEvent) {
        if(messageTextArea.getText().length()>512)
        {
            showError(bundle.getString("MessageToBigError"));
            return;
        }
        var curr = chatsListView.getSelectionModel().getSelectedItem();
        try {
            service.sendMessage(curr.getId(), messageTextArea.getText());
        } catch (IOException e) {

            showError(bundle.getString("InternalErrorText"));
            return;
        }
        List<Message> temp;
        try {
            temp = service.getMessages(curr.getId(), 1);
        } catch (IOException e) {

            showError(bundle.getString("InternalErrorText"));
            return;
        }



        messageList.add(0, temp.get(0));
        messageOffset++;
        if (messageOffset == MESSAGE_PAGE_SIZE) {
            messageOffset = 0;
            messagesPage++;
        }
        messagesListView.setItems(FXCollections.observableList(messageList));
        messageTextArea.setText("");
        sendMessageButton.setDisable(true);
        chatList.remove(chatsListView.getSelectionModel().getSelectedIndex());
        chatList.add(0,curr);
        chatsListView.setItems(FXCollections.observableList(chatList));
        chatsListView.getSelectionModel().select(0);
    }

    public void scrollMethod(ScrollEvent scrollEvent) {
        if (scrollEvent.getDeltaY() < 0) {
            loadChatPage();
        }
    }


    public void messagesScroll(ScrollEvent scrollEvent) {
        if (scrollEvent.getDeltaY() < 0) {
            if (chatsListView.getSelectionModel().getSelectedItem() != null) {
                loadMessagePage(chatsListView.getSelectionModel().getSelectedItem().getId());
            } else {
                resetMessages();
            }
        }
    }


    public void textAreaKeyTyped() {
        if (chatsListView.getSelectionModel().getSelectedIndex() < 0) {
            messageTextArea.setText("");
            return;
        }
        if (sendButtonMode==Mode.EDIT)
            if (messageTextArea.getText().equals(messageToEditVal.getContent())) {
                sendMessageButton.setDisable(true);
                return;
            }
        sendMessageButton.setDisable(messageTextArea.getText().length() == 0);
    }


    public void profileButtonPress(ActionEvent actionEvent) throws IOException {
        if (sendButtonMode!=Mode.SEND) {
            switchToSendMode();
        }
        resetMessages();
        chatsListView.getSelectionModel().select(-1);
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/fxmls/profile_form.fxml"), bundle);
        Parent window = fmxlLoader.load();
        ProfileFormController profileFormController = fmxlLoader.getController();
        profileFormController.setBundle(bundle);
        Scene scene = new Scene(window);

        profileFormController.setLogin(service.getLogin());
        Stage nstage = new Stage();
        nstage.setScene(scene);
        nstage.setTitle("Profile");
        nstage.setMinHeight(500);
        nstage.setMinWidth(700);
        nstage.setMaxHeight(750);
        nstage.setMaxWidth(1050);


        profileFormController.setCurrentStage(nstage);
        profileFormController.setPrimaryStage(currStage);
        profileFormController.setPrevController(this);
        profileFormController.init();

        nstage.show();
        currStage.hide();
    }
}
