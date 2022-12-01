package ru.edu.spbstu.clientComponents;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    Label label=new Label();
    public MyButton button=new MyButton();
    public HBoxCell()
    {
        super();
        button= new MyButton();
        Label label = new Label();

    }

    HBoxCell(T labelText, String buttonText,int number) {
        super();

        label.setText(labelText.toString());
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);

        button.setText(buttonText);
        button.structNum=number;

        this.getChildren().addAll(label, button);
    }
    public String getLabelText()
    {
        return label.getText();
    }
}
