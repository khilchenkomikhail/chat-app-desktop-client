package ru.edu.spbstu.client.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.edu.spbstu.client.services.ConfigureChatFormService;
import ru.edu.spbstu.clientComponents.ListViewWithButtons;
import ru.edu.spbstu.clientComponents.ListViewWithCheckBoxes;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.ChatUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
    private ResourceBundle bundle;


    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
    
    public void setLogin(String login)
    {
        this.service.setLogin(login);
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
    void initialize() {


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

        ArrayList<Image> images=service.getImageList(userList);
        chatMembersConfigurationLV.resetList(userList,images);

        currStage.setOnCloseRequest(e -> {
            primaryStage.show();
            currStage.close();
        });
    }

    public void addUsersToChatButtonPress() throws IOException {
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
            SingleSelectionModel<Tab> selectionModel =mainTabPanel.getSelectionModel();
            selectionModel.select(0);
            mainTabPanel.setSelectionModel(selectionModel);
        }
        else
        {
            userList.remove(user);
        }
        ArrayList<Image> images=service.getImageList(userList);
        chatMembersConfigurationLV.resetList(userList,images);
    }

    public void confirmSettingsButtonPress() throws IOException {
        service.deleteChatUsers(chatToConfigure,chatMembersConfigurationLV.getUsersToDelete());
        service.setChatUsersAdmins(chatToConfigure,chatMembersConfigurationLV.getUsersToMakeAdmins());
        update();
    }

    public void AddUserButtonClick() {

        String username= loginTextField.getText();
        try {
            boolean pres=service.isUserPresent(username);
            if(!pres)
            {
                showError("Пользователя с данным логином не существует!");
                return;
            }
        }
        catch(IOException e)
        {
            showError("Внутренняя ошибка сервера!");
            return;
        }

        String userLof=service.getLogin();

        if(username.equals(userLof))
        {
            showError("Создателя чата не нужно добавлять в список чата!");
            return;
        }
        var userList=chatMembersConfigurationLV.getUsers();
        if(userList.stream().anyMatch(user -> user.getLogin().equals(username)))
        {
            showError("Данный пользователь уже есть в чате!");
            return;
        }
        if(usersToAddListView.getList().stream().anyMatch(user -> user.getLogin().equals(username)))
        {
            showError("Данный пользователь уже есть в списке на добавление!");
            return;
        }
        ChatUser user=new ChatUser(username,false);
        Image image= null;
        try {
            image = service.getImage(user.getLogin());
        } catch (IOException e) {
            image=new Image((getClass().getResource("/images/dAvatar.bmp")).getPath().replaceFirst("/",""));
        }
        usersToAddListView.addInList(user,image);
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
