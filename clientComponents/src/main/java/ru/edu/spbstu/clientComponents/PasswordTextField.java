package ru.edu.spbstu.clientComponents;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PasswordTextField extends TextField {
    public PasswordTextField() {
        super();
    }
    //TODO починить регулярку
    String numberRegEx = "\\b[A-Za-z0-9A-Яа-я]+\\b";
    @Override
    public void replaceText(int start, int end, String text) {
        String oldValue = getText();
        if ((validate(text))&&text.length()<=10) {
            super.replaceText(start, end, text);
            String newText = super.getText();
            if (!validate(newText)) {
                super.setText(oldValue);
            }
        }
    }

    @Override
    public void replaceSelection(String text) {
        String oldValue = getText();
        if (validate(text)) {
            super.replaceSelection(text);
            String newText = super.getText();
            if (!validate(newText)) {
                super.setText(oldValue);
            }
        }
    }

    private boolean validate(String text) {
        return ("".equals(text) || text.matches(numberRegEx));
    }
}
