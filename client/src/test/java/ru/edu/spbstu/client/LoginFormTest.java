package ru.edu.spbstu.client;

import com.google.common.base.Strings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Test;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.service.finder.WindowFinder;
import ru.edu.spbstu.client.controllers.LoginFormController;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Verify.verify;
import static org.testfx.api.FxAssert.verifyThat;

public class LoginFormTest extends BasedTest {

    @Test
    public void testButtonActivation() throws Exception {
        verifyThat("#logInButton", Node::isDisable);
        verifyThat("#forgetPasswordButton", Node::isDisable);
        clickOn("#loginTextBox").write("olegoleg");
        verifyThat("#forgetPasswordButton", NodeMatchers.isEnabled());
        clickOn("#passwordTextBox").write("olegoleg");
        verifyThat("#logInButton", NodeMatchers.isEnabled());
    }
    @Test
    public void testRegisterButtonActivation() throws Exception {
        clickOn("#regTab");
        verifyThat("#registerButton", Node::isDisable);
        clickOn("#regLoginTextBox").write("olegoleg");
        clickOn("#emailTextBox").write("olegoleg@gmail.com");
        clickOn("#regPasswordTextBox").write("olegoleg");
        verifyThat("#registerButton", NodeMatchers.isEnabled());
    }
    @Test
    public void testInvalidFormatFieldsInput() throws Exception {
        TextField temp=find("#loginTextBox");
        temp.setText(Strings.repeat("o",50));
        clickOn("#loginTextBox").write("o");
        clickOn("#passwordTextBox").write("olegoleg");
        clickOn("#logInButton");
        checkAlertHeaderText("InvalidLoginSizeError");


        temp.clear();
        clickOn("#loginTextBox").write("");
        clickOn("#loginTextBox").write("<fef");
        clickOn("#logInButton");
        checkAlertHeaderText("BadFormatLoginErrorText");
        temp.setText("olegoleg");

        TextField passwordFiled=find("#passwordTextBox");
        passwordFiled.clear();
        clickOn("#passwordTextBox").write("ooooooo");

        clickOn("#logInButton");
        checkAlertHeaderText("wrongPasswordLengthError");

        passwordFiled.clear();
        clickOn("#passwordTextBox").write("");
        passwordFiled.setText(Strings.repeat("o",128));
        clickOn("#passwordTextBox").write("o");
        clickOn("#logInButton");
        checkAlertHeaderText("wrongPasswordLengthError");
    }
    @Test
    public void testOpenNewFormWithNoServer() throws Exception {
        clickOn("#loginTextBox").write("olegoleg");
        verifyThat("#forgetPasswordButton", NodeMatchers.isEnabled());
        clickOn("#passwordTextBox").write("olegoleg");
        clickOn("#logInButton");
        checkAlertHeaderText("InternalErrorText");
    }


    private void checkAlertHeaderText(String bundledMessageId)
    {
        DialogPane dialog;
        try {
            dialog = (DialogPane) lookup(".alert").queryAll().iterator().next();
        }
        catch (NoSuchElementException e)
        {
            throw new NoAlertFoundException();
        }

        String alertTitle=dialog.getHeaderText();
        clickOn("OK");

        //Interesting way to get loaclization for our application(via getting the fxmloader that was store previously)
        LoginFormController concC=((FXMLLoader)find("#forgetPasswordButton").getScene().getUserData()).getController();
        String expectedMessage=(concC.getBundle().getString(bundledMessageId));
        if(!expectedMessage.equals(alertTitle))
        {
            String errtext= (String.format("Expected alert with title \"%s\", got alert with header \"%s\"",expectedMessage,alertTitle));
            throw new AssertionError(errtext);
        }
    }

    @Deprecated
    private String getAlertTitle() throws NoAlertFoundException {
        for (Window w : this.listWindows()) {
            if (!((Stage) w).getModality().toString().equals("NONE")) {
                String headerText = ((DialogPane) w.getScene().getRoot()).getHeaderText();
                clickOn("OK");
                return headerText;
            }
        }
        throw new NoAlertFoundException();
    }

}
