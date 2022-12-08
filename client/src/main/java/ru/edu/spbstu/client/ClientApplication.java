package ru.edu.spbstu.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ClientApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws IOException {


        stage=primaryStage;
        URL fxmlUrl = this.getClass()
                .getResource("/fxmls/login_register.fxml");
        if(fxmlUrl==null)
        {
            System.out.println("Not found");
            return;
        }

        Parent root = FXMLLoader.load(fxmlUrl);
        primaryStage.setTitle("Authorization");
        primaryStage.setScene(new Scene(root, 520, 240));
        primaryStage.show();
    }
    public static Stage getStage()
    {
        return stage;
    }
}





