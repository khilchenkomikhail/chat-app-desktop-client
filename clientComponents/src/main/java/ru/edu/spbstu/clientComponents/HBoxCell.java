package ru.edu.spbstu.clientComponents;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class HBoxCell<T> extends HBox {
    public static class MyButton extends Button {
        protected int structNum;
        MyButton()
        {
            super();
        }
        MyButton(String buttonText,int num)
        {
            super();
            this.setText(buttonText);
            structNum=num;

        }

    }
    ImageView imageView;
    Label label;
    public MyButton button;
    public HBoxCell()
    {
        super();
        imageView=new ImageView();
        button= new MyButton();
        label = new Label();

    }

    HBoxCell(T labelText, String buttonText, int number, Image image) {
        this();


        imageView.setImage(image);
        imageView.setFitHeight(40);
        imageView.setFitWidth(40);


        label.setAlignment(Pos.CENTER_LEFT);
        button.setAlignment(Pos.CENTER_RIGHT);
        HboxCellWithCheckboxes.setMargin(label,new Insets(0,0,0,20));
        HboxCellWithCheckboxes.setMargin(imageView,new Insets(5,0,5,0));
        HboxCellWithCheckboxes.setMargin(button,new Insets(0,40,0,0));



        label.setText(labelText.toString());
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);

        button.setText(buttonText);
        button.structNum=number;

        this.getChildren().addAll(imageView,label, button);
        this.setAlignment(Pos.CENTER_LEFT);
    }
    public String getLabelText()
    {
        return label.getText();
    }
}
