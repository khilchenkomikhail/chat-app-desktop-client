package ru.edu.spbstu.clientComponents;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ru.edu.spbstu.model.ChatUser;

public class HboxCellWithCheckboxes extends HBox {

    public static class MyCheckBox extends CheckBox {
        protected int structNum;
        MyCheckBox()
        {
            super();
        }
        MyCheckBox(int num)
        {
            super();
            structNum=num;
        }

    }
    Label label=new Label();
    MyCheckBox deleteCB=new MyCheckBox();
    MyCheckBox adminCB=new MyCheckBox();

    public HboxCellWithCheckboxes()
    {
        super();
        label = new Label();
        deleteCB=new MyCheckBox();
        adminCB=new MyCheckBox();
    }

    HboxCellWithCheckboxes(String labelText, int number) {
        super();

        label.setText(labelText);
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);

        deleteCB.structNum=number;
        adminCB.structNum=number;

        label.setAlignment(Pos.CENTER_LEFT);
        deleteCB.setAlignment(Pos.CENTER_RIGHT);
        adminCB.setAlignment(Pos.CENTER_RIGHT);
        HboxCellWithCheckboxes.setMargin(deleteCB,new Insets(0,120,0,0));
        HboxCellWithCheckboxes.setMargin(adminCB,new Insets(0,40,0,0));
        this.getChildren().addAll(label,deleteCB,adminCB);

    }
    public String getLabelText()
    {
        return label.getText();
    }
}
