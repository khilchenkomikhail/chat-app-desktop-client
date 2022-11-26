package ru.edu.spbstu.client;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlUrl = this.getClass()
                .getResource("/fxmls/login_register.fxml");
        if(fxmlUrl==null)
        {
            System.out.println("Not found");
            return;
        }
        //launch scene
        Parent root = FXMLLoader.load(fxmlUrl);
        primaryStage.setTitle("FirstFormDemo");
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.show();

    }
}





