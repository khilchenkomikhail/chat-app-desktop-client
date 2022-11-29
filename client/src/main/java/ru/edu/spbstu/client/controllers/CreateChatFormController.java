package ru.edu.spbstu.client.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import org.apache.http.client.CredentialsProvider;
import ru.edu.spbstu.client.services.ChatFormService;
import ru.edu.spbstu.client.services.CreateChatFormService;
import ru.edu.spbstu.clientComponents.PasswordTextField;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateChatFormController {
    public Label chatUsersLabel;
    public Label loginLabel;
    public Label ChatNameLabel;

    public ListView<String> usersToAddListView;
    public TextField loginTextField;
    public TextField chatNameTextBox;

    public Button createChatButton;
    public Button AddUserButton;
    private CreateChatFormService service=new CreateChatFormService();
    private Stage primaryStage;
    private Stage currStage;
    private List<String> userList;
    void showError(String errorText)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(errorText);
        alert.show();
    }
    public void setCredentials(CredentialsProvider prov,String login)
    {
        this.service.setCredentialsProvider(prov,login);
    }

    @FXML
    void initialize() throws IOException {


    }
    void init() throws IOException {
        currStage.setOnCloseRequest(event -> {
            //currStage.close();
            primaryStage.show();
        });
        userList=new ArrayList<>(0);

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
        usersToAddListView.setItems(FXCollections.observableArrayList(userList));
    }




    public void scrollAction(ScrollEvent scrollEvent) {
        var index =usersToAddListView.getSelectionModel().getSelectedIndices();
        System.out.println(userList.get(index.get(0)));
    }

    public void AddUserButtonClick(ActionEvent actionEvent) {
        String username= loginTextField.getText();
        boolean check= service.checkUser(username);
        if(check) {
            userList.add(username);
            //service.add

            update();
        }
        else
        {
            showError("Пользователя с данным логином не существует!");
        }

    }

    public void createChatButtonClick(ActionEvent actionEvent) throws IOException {

        String name=chatNameTextBox.getText();


        service.addChat(name,userList);

        currStage.close();
        //TODo refresh prev thing
        primaryStage.show();



    }
}
