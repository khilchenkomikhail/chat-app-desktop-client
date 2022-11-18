package ru.edu.spbstu.client.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;

import java.util.concurrent.ThreadLocalRandom;

public class LoginFormController {
    public ContextMenu ar;
    public CheckBox s;
    public Button forgetPasswordButton;
    ContextMenu contextMenu = new ContextMenu();

    @FXML
    void leftMouseClick()
    {

        System.out.println("Clicl");
        //boolean state=s.isSelected();
        //Change check box value
        s.setSelected(!s.isSelected());
        //Get screen bounds
        Bounds boundsInScreen = s.localToScreen(s.getBoundsInLocal());

        //show context menu() at th center of th screen
        contextMenu.show(s,boundsInScreen.getCenterX(),boundsInScreen.getCenterY());
    }


    public void leftMouseDrageEnter(MouseEvent mouseEvent) {

        double lx=305.0, ly=172.0;
        //some obscure calculations to move button in random spot on screen
        forgetPasswordButton.setTranslateX(ThreadLocalRandom.current().nextDouble(-lx+50, 550-lx));
        forgetPasswordButton.setTranslateY(ThreadLocalRandom.current().nextDouble(-ly+50, 400-ly));
        double x=forgetPasswordButton.getTranslateX(),y=forgetPasswordButton.getTranslateY();
    }
    @FXML
    void initialize() {
        // create menuitems
        javafx.scene.control.MenuItem menuItem1 = new javafx.scene.control.MenuItem("Some action 1");
        javafx.scene.control.MenuItem menuItem2 = new javafx.scene.control.MenuItem("Some action 2");
        javafx.scene.control.MenuItem menuItem3 = new javafx.scene.control.MenuItem("Some action 3");
        //setting on action callback
        menuItem1.setOnAction(this::menuItem1Action);
        //You can even create callbacks using lambdas https://www.tutorialspoint.com/how-to-add-action-listeners-to-contextmenu-in-javafx
        contextMenu.getItems().add(menuItem1);
        contextMenu.getItems().add(menuItem2);
        contextMenu.getItems().add(menuItem3);
        s.setContextMenu(contextMenu);
    }

    private void menuItem1Action(javafx.event.ActionEvent actionEvent) {
        System.out.println("Some action with menuItem1");
    }

    public void leftMouseExit(MouseEvent mouseEvent) {
        //TODO uncomment to make mouse follow the button
        //Scene scene = forgetPasswordButton.getScene();
        //doublea a=new Robot().mouseMove(;
       // Bounds boundsInScreen = forgetPasswordButton.localToScreen(forgetPasswordButton.getBoundsInLocal());
        //GlassRobot robot = com.sun.glass.ui.Application.GetApplication().createRobot();
        //new Robot().mouseMove((int)(boundsInScreen.getCenterX()), (int)(boundsInScreen.getCenterY()));
        //robot.mouseMove((int)(boundsInScreen.getCenterX()), (int)(boundsInScreen.getCenterY()));
        //forgetPasswordButton.setTranslateX(0);
       // forgetPasswordButton.setTranslateY(0);
    }
}
