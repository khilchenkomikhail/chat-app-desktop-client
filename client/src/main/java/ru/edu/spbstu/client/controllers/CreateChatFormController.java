package ru.edu.spbstu.client.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.http.client.CredentialsProvider;
import ru.edu.spbstu.client.services.CreateChatFormService;
import ru.edu.spbstu.clientComponents.ListViewWithButtons;
import ru.edu.spbstu.model.ChatUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateChatFormController {


    public ListViewWithButtons<ChatUser> usersToAddListView;
    public TextField loginTextField;
    public TextField chatNameTextBox;




    public Button createChatButton;
    public Button AddUserButton;
    public Label chatUsersLabel;
    public Label loginLabel;
    public Label ChatNameLabel;

    private CreateChatFormService service=new CreateChatFormService();
    private Stage primaryStage;
    private Stage currStage;
    private List<ChatUser> userList;
    private ChatFormController prevController;

    public void setPrevController(ChatFormController prevController) {
        this.prevController = prevController;
    }
    void showError(String errorText)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(errorText);
        alert.show();
    }
    public void setCredentials(CredentialsProvider prov,String login)
    {
        service.setCredentialsProvider(prov,login);
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
        currStage.setOnCloseRequest(event -> {
            primaryStage.show();
        });
        userList=new ArrayList<>(0);

        currStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                primaryStage.show();
                currStage.close();
            }
        });
    }

    private void update()
    {

        int size=userList.size();
        ArrayList<Image> images= service.getImageList(userList);
        usersToAddListView.resetList(userList,images);
    }

    public void AddUserButtonClick(ActionEvent actionEvent) {
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

        if(usersToAddListView.getList().stream().anyMatch(user -> user.getLogin().equals(username)))
        {
            showError("Данный пользователь уже был добавлен в чат!");
            return;
        }
        ChatUser user=new ChatUser(username,false);
        userList.add(user);
        Image image= service.getImage(user);

        usersToAddListView.addInList(user,image);
    }

    public void createChatButtonClick(ActionEvent actionEvent) throws IOException {

        String name=chatNameTextBox.getText();
        List<String> logins=new ArrayList<>();
        for (var elem: usersToAddListView.getList())
        {
            logins.add(elem.getLogin());
        }

        service.addChat(name,logins);
        //service.addChat(name,userList);
        prevController.addNewChat(name);

        //prevController.update();
        currStage.close();
        primaryStage.show();
    }

}
