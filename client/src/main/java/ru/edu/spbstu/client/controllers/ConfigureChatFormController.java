package ru.edu.spbstu.client.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.http.client.CredentialsProvider;
import ru.edu.spbstu.client.services.ConfigureChatFormService;
import ru.edu.spbstu.clientComponents.ListViewWithButtons;
import ru.edu.spbstu.clientComponents.ListViewWithCheckBoxes;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.ChatUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigureChatFormController {
    public ListViewWithCheckBoxes chatMembersConfigurationLV;
    public ListViewWithButtons<ChatUser> usersToAddListView;

    public TabPane mainTabPanel;
    public TextField loginTextField;
    public Tab tabChatSettings;
    public Tab AddUsersTab;
    public Button addUsersButton;
    public Button AddUserButton;
    public Button confirmSettingsButton;
    public Label chatUsersLabel;
    public Label excludeFromChatLabel;
    public Label makeAdministratorLabel;
    public Label userLoginLabel;
    public Label chatNameLabel;


    private final ConfigureChatFormService service=new ConfigureChatFormService();
    private Stage primaryStage;
    private Stage currStage;
    private ChatUser user=new ChatUser();
    private Chat chatToConfigure;
    private ChatFormController prevController;


    public void setCredentials(CredentialsProvider prov,String login)
    {
        this.service.setCredentialsProvider(prov,login);
    }
    public void setChat(Chat selectedItem) {
        chatToConfigure=selectedItem;
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public void setCurrStage(Stage currStage) {
        this.currStage = currStage;
    }

    @FXML
    void initialize() throws IOException {


    }
    void init() throws IOException {
        List<ChatUser> userList=service.getChatMembers(chatToConfigure);
        for(var elem:userList)
        {
            if(elem.getLogin().equals(service.getLogin()))
            {
                user=elem;
                break;
            }
        }
        if(userList.size()==1)
        {
            userList=new ArrayList<>();
            tabChatSettings.setDisable(true);
        }
        else
        {
            userList.remove(user);
        }
        if(!user.getIs_admin())
        {
            mainTabPanel.getTabs().remove(1);
        }
        chatMembersConfigurationLV.resetList(userList);

        currStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                primaryStage.show();
                currStage.close();
            }
        });
    }

    public void addUsersToChatButtonPress(ActionEvent actionEvent) throws IOException {
        List<String> logins=new ArrayList<>();
        for (var elem: usersToAddListView.getList())
        {
            logins.add(elem.getLogin());
        }
        if(logins.isEmpty())
        {
            return;
        }

        service.addUsersToChat(chatToConfigure,logins);
        tabChatSettings.setDisable(false);
        usersToAddListView.setItems(FXCollections.observableList(new ArrayList<>()));
        update();
    }

    private void update() throws IOException {
        var userList=service.getChatMembers(chatToConfigure);
        if(userList.size()==1)
        {
            userList=new ArrayList<>();
            tabChatSettings.setDisable(true);
        }
        else
        {
            userList.remove(user);
        }
        chatMembersConfigurationLV.resetList(userList);
    }

    public void confirmSettingsButtonPress(ActionEvent actionEvent) throws IOException {
        service.deleteChatUsers(chatToConfigure,chatMembersConfigurationLV.getUsersToDelete());
        service.setChatUsersAdmins(chatToConfigure,chatMembersConfigurationLV.getUsersToMakeAdmins());
        update();
        SingleSelectionModel<Tab> selectionModel =mainTabPanel.getSelectionModel();
        selectionModel.select(0);
        mainTabPanel.setSelectionModel(selectionModel);
    }

    public void AddUserButtonClick(ActionEvent actionEvent) {

        String username= loginTextField.getText();
        ChatUser temp;
        try {
            temp=service.getUser(username);
        }
        catch(IOException e)
        {
            showError("Пользователя с данным логином не существует!");
            return;
        }

        String userLof=service.getLogin();

        if(username.equals(userLof))
        {
            showError("Создателя чата не нужно добавлять в список чата!");
            return;
        }
        ChatUser temp2=new ChatUser(username,false);
        var UserList=chatMembersConfigurationLV.getUsers();
        if(UserList.contains(temp2))
        {
            showError("Данный пользователь уже есть в чате!");
            return;
        }
        if(usersToAddListView.getList().contains(temp2))
        {
            showError("Данный пользователь уже есть в списке на добавление!");
            return;
        }
        usersToAddListView.addInList(temp2);
       // mainTabPanel.getTabs().add(tabChatSettings);//Todo return second tab
    }

    private void showError(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(s);
        alert.show();
    }


    public void setPrevController(ChatFormController prevController) {
        this.prevController = prevController;
    }


}
