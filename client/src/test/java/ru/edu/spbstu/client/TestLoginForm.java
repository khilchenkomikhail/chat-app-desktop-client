package ru.edu.spbstu.client;

import javafx.scene.Node;
import org.junit.jupiter.api.Test;
import org.testfx.matcher.base.NodeMatchers;

import static org.testfx.api.FxAssert.verifyThat;

public class TestLoginForm extends BasedTest {
    @Test
    void testButtonActivation(){
        verifyThat("#logInButton", Node::isDisable);
        verifyThat("#forgetPasswordButton", Node::isDisable);
        clickOn("#loginTextBox").write("olegoleg");
        verifyThat("#forgetPasswordButton", NodeMatchers.isEnabled());
        clickOn("#passwordTextBox").write("olegoleg");
        verifyThat("#logInButton", NodeMatchers.isEnabled());

    }
    @Test
    void testRegisterButtonActivation() {
        clickOn("#regTab");
        verifyThat("#registerButton", Node::isDisable);
        clickOn("#regLoginTextBox").write("olegoleg");
        clickOn("#emailTextBox").write("olegoleg@gmail.com");
        clickOn("#regPasswordTextBox").write("olegoleg");
        verifyThat("#registerButton", NodeMatchers.isEnabled());
    }
}
