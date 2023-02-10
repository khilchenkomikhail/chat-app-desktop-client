package ru.edu.spbstu.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.base.Strings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Test;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.service.query.EmptyNodeQueryException;
import ru.edu.spbstu.client.controllers.ChatFormController;
import ru.edu.spbstu.client.controllers.LoginFormController;
import ru.edu.spbstu.client.exception.InvalidDataException;
import ru.edu.spbstu.model.Chat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.sql.DriverManager.getDriver;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.internal.JavaVersionAdapter.getWindows;

public class LoginFormTest extends BasedTest {

    public static final String IS_USER_PRESENT_LOGIN = "/is_user_present\\?login=.*";
    public static final String IS_EMAIL_USED_EMAIL = "/is_email_used\\?email=.*";
    public static final String URL_REGEX = "/register";
    public static final String GET_CHATS_LOGIN_PAGE_NUMBER_D = "/get_chats\\?login=.*&page_number=\\d+";

    @Test
    public void testButtonActivation() {
        verifyThat("#logInButton", Node::isDisable);
        verifyThat("#forgetPasswordButton", Node::isDisable);
        clickOn("#loginTextBox").write("olegoleg");
        verifyThat("#forgetPasswordButton", NodeMatchers.isEnabled());
        clickOn("#passwordTextBox").write("olegoleg");
        verifyThat("#logInButton", NodeMatchers.isEnabled());
    }

    @Test
    public void testRegisterButtonActivation() {
        clickOn("#regTab");
        verifyThat("#registerButton", Node::isDisable);
        clickOn("#regLoginTextBox").write("olegoleg");
        clickOn("#emailTextBox").write("olegoleg@gmail.com");
        clickOn("#regPasswordTextBox").write("olegoleg");
        verifyThat("#registerButton", NodeMatchers.isEnabled());
    }

    @Test
    public void testInvalidLoginSize() {
        TextField temp = find("#loginTextBox");
        temp.setText(Strings.repeat("o", 50));
        clickOn("#loginTextBox").write("o");
        clickOn("#passwordTextBox").write("olegoleg");
        clickOn("#logInButton");
        checkAlertHeaderText("InvalidLoginSizeError");
    }

    @Test
    public void testPasswordTooShort() {
        clickOn("#loginTextBox").write("olegoleg");
        clickOn("#passwordTextBox").write("ooo");
        clickOn("#logInButton");
        checkAlertHeaderText("wrongPasswordLengthError");
    }

    @Test
    public void testPasswordTooLong() {
        clickOn("#loginTextBox").write("olegoleg");
        TextField passwordFiled = find("#passwordTextBox");

        passwordFiled.setText(Strings.repeat("o", 128));
        clickOn("#passwordTextBox").write("o");
        clickOn("#logInButton");
        checkAlertHeaderText("wrongPasswordLengthError");
    }

    @Test
    public void testInvalidLoginFormat() {
        clickOn("#passwordTextBox").write("olegoleg");
        clickOn("#loginTextBox").write("");
        clickOn("#loginTextBox").write("<$fef");
        clickOn("#logInButton");
        checkAlertHeaderText("BadFormatLoginErrorText");
    }

    @Test
    public void testRegLoginTooLong()
    {
        clickOn("#regTab");
        clickOn("#regPasswordTextBox").write("olegoleg");
        clickOn("#emailTextBox").write("olegoleg@gmail.com");
        TextField tx=find("#regLoginTextBox");
        tx.clear();
        tx.setText(Strings.repeat("o", 50));
        clickOn("#regLoginTextBox").write("o");
        clickOn("#registerButton");

        checkAlertHeaderText("InvalidLoginSizeError");
    }

    @Test
    public void testInvalidLoginRegFormat() {

        clickOn("#regTab");
        clickOn("#regLoginTextBox").write("<$fef");

        clickOn("#regPasswordTextBox").write("olegoleg");
        clickOn("#emailTextBox").write("olegoleg@gmail.com");
        clickOn("#registerButton");
        checkAlertHeaderText("BadFormatLoginErrorText");
    }


    @Test
    public void testRegPasswordTooShort() {

        clickOn("#regTab");
        clickOn("#regLoginTextBox").write("olegoleg");
        clickOn("#regPasswordTextBox").write("ooo");
        clickOn("#emailTextBox").write("olegoleg@gmail.com");
        clickOn("#registerButton");
        checkAlertHeaderText("wrongPasswordLengthError");
    }


    @Test
    public void testRegPasswordTooLong() {

        clickOn("#regTab");
        clickOn("#regLoginTextBox").write("olegoleg");
        clickOn("#emailTextBox").write("olegoleg@gmail.com");
        clickOn("#registerButton");
        TextField tx=find("#regPasswordTextBox");
        tx.setText(Strings.repeat("o", 128));
        clickOn("#regPasswordTextBox").write("o");
        clickOn("#registerButton");
        checkAlertHeaderText("wrongPasswordLengthError");
    }

    @Test
    public void testEmailTooShort() {

        clickOn("#regTab");
        clickOn("#regLoginTextBox").write("olegoleg");

        clickOn("#regPasswordTextBox").write("olegoleg");
        clickOn("#emailTextBox").write("ooo");
        clickOn("#registerButton");
        checkAlertHeaderText("InvalidEmailSizeError");

    }
    @Test
    public void testEmailTooLong() {

        clickOn("#regTab");
        clickOn("#regLoginTextBox").write("olegoleg");
        clickOn("#regPasswordTextBox").write("olegoleg");

        TextField tx=find("#emailTextBox");
        tx.clear();
        tx.setText(Strings.repeat("o", 128));
        clickOn("#emailTextBox").write("o");
        clickOn("#registerButton");

        checkAlertHeaderText("InvalidEmailSizeError");
    }
    @Test
    public void testInvalidEmailFormat()
    {
        clickOn("#regTab");
        clickOn("#regLoginTextBox").write("olegoleg");
        clickOn("#regPasswordTextBox").write("olegoleg");
        clickOn("#emailTextBox").write("o@c.");
        clickOn("#registerButton");
        checkAlertHeaderText("BadFormatEmailErrorText");
    }
    @Test
    public void testRegisterWithoutServer()
    {
        clickOn("#regTab");
        clickOn("#regLoginTextBox").write("olegoleg");
        clickOn("#regPasswordTextBox").write("olegoleg");
        clickOn("#emailTextBox").write("olegoleg@gmail.com");
        clickOn("#registerButton");
        checkAlertHeaderText("InternalErrorText");
    }

    @Test
    public void testOpenForgotPasswordFormWithoutServer(){
        clickOn("#loginTextBox").write("olegoleg");
        //verifyThat("#forgetPasswordButton", NodeMatchers.isEnabled());
        clickOn("#forgetPasswordButton");
        checkAlertHeaderText("InternalErrorText");
    }


    @Test
    public void testOpenNewFormWithNoServer(){
        clickOn("#loginTextBox").write("olegoleg");
        verifyThat("#forgetPasswordButton", NodeMatchers.isEnabled());
        clickOn("#passwordTextBox").write("olegoleg");
        clickOn("#logInButton");
        checkAlertHeaderText("InternalErrorText");
    }


    private WireMockServer wireMockServer;

    @Test
    public void testNoAccountToResetPassword() throws Exception {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        ObjectMapper jsonMapper = new ObjectMapper();


        String responce = jsonMapper.writeValueAsString(false);

        stubFor(get(urlMatching(IS_USER_PRESENT_LOGIN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responce)));

        clickOn("#loginTextBox").write("olegoleg");
        clickOn("#forgetPasswordButton");
        checkAlertHeaderText("NoAccountForLoginError");
        //wireMockServer.stop();
        wireMockServer.shutdown();
    }

    @Test
    public void testOpenForgotPasswordForm() throws Exception {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        ObjectMapper jsonMapper = new ObjectMapper();


        clickOn("#loginTextBox").write("olegoleg");

        stubFor(get(urlMatching(IS_USER_PRESENT_LOGIN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonMapper.writeValueAsString(true))));
        clickOn("#forgetPasswordButton");
        try {
            lookup("#changePasswordButton").queryButton();
        } catch (EmptyNodeQueryException ex) {
            throw new InvalidDataException("Expected behaviour: forgot password form opens. Resulting behaviour: forgot password form is not opened");
        }
        //wireMockServer.stop();
        wireMockServer.shutdown();
    }

    @Test
    public void testOpenSecondForm() throws Exception {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        ObjectMapper jsonMapper = new ObjectMapper();

        List<Chat> chats = Arrays.asList(new Chat(1L, "first"),
                new Chat(2L, "second"),
                new Chat(3L, "third"));
        String responce = jsonMapper.writeValueAsString(chats);


        stubFor(get(urlMatching(GET_CHATS_LOGIN_PAGE_NUMBER_D))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responce)));

        clickOn("#loginTextBox").write("olegoleg");
        clickOn("#passwordTextBox").write("olegoleg");
        clickOn("#logInButton");
        try {
            lookup("#addChatButton").queryButton();
        } catch (EmptyNodeQueryException ex) {
            throw new InvalidDataException("Expected behaviour: second form opens. Resulting behaviour: second form is not opened");
        }

        Scene currectScene = getWindows().get(0).getScene();

        ChatFormController controller = ((ChatFormController) currectScene.getUserData());
        controller.timeline.stop();
        //wireMockServer.stop();
        wireMockServer.shutdown();
    }

    @Test
    public void testInvalidCredentialsAlertShow() throws Exception {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();//start server
        ObjectMapper jsonMapper = new ObjectMapper();

        List<Chat> chats = Arrays.asList(new Chat(1L, "first"),
                new Chat(2L, "second"),
                new Chat(3L, "third"));
        String responce = jsonMapper.writeValueAsString(chats);


        stubFor(get(urlMatching(GET_CHATS_LOGIN_PAGE_NUMBER_D))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responce)));

        clickOn("#loginTextBox").write("olegoleg");
        clickOn("#passwordTextBox").write("olegoleg");
        clickOn("#logInButton");
        checkAlertHeaderText("InvalidLogPasswordError");
        //wireMockServer.stop();
        wireMockServer.shutdown();
    }

    @Test
    public void testUserAlreadyExistWhenRegister() throws JsonProcessingException {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        ObjectMapper jsonMapper = new ObjectMapper();
        String responce = jsonMapper.writeValueAsString(true);

        stubFor(get(urlMatching(IS_USER_PRESENT_LOGIN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responce)));

        clickOn("#regTab");
        clickOn("#regLoginTextBox").write("olegoleg");

        clickOn("#regPasswordTextBox").write("olegoleg");
        clickOn("#emailTextBox").write("olegoleg@gmail.com");
        clickOn("#registerButton");
        checkAlertHeaderText("AccountWithLoginExistsError");
        //wireMockServer.stop();
        wireMockServer.shutdown();
    }

    @Test
    public void testEmailAlreadyExists() throws JsonProcessingException {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();//start server
        ObjectMapper jsonMapper = new ObjectMapper();
        String responce = jsonMapper.writeValueAsString(true);


        clickOn("#regTab");
        clickOn("#regLoginTextBox").write("olegoleg");

        clickOn("#regPasswordTextBox").write("olegoleg");
        clickOn("#emailTextBox").write("olegoleg@gmail.com");

        stubFor(get(urlMatching(IS_USER_PRESENT_LOGIN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonMapper.writeValueAsString(false))));

        stubFor(get(urlMatching(IS_EMAIL_USED_EMAIL))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonMapper.writeValueAsString(true))));
        clickOn("#registerButton");
        checkAlertHeaderText("EmailInAlreadyUsedError");
        //wireMockServer.stop();
        wireMockServer.shutdown();
    }

    @Test
    public void testOpenSecondFormViaRegister() throws JsonProcessingException {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();//start server
        ObjectMapper jsonMapper = new ObjectMapper();
        String responce = jsonMapper.writeValueAsString(true);


        clickOn("#regTab");
        clickOn("#regLoginTextBox").write("olegoleg");

        clickOn("#regPasswordTextBox").write("olegoleg");
        clickOn("#emailTextBox").write("olegoleg@gmail.com");

        stubFor(get(urlMatching(IS_USER_PRESENT_LOGIN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonMapper.writeValueAsString(false))));

        stubFor(get(urlMatching(IS_EMAIL_USED_EMAIL))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonMapper.writeValueAsString(false))));

        stubFor(post(urlMatching(URL_REGEX))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonMapper.writeValueAsString(false))));

        List<Chat> chats = Arrays.asList(new Chat(1L, "first"),
                new Chat(2L, "second"),
                new Chat(3L, "third"));
        responce = jsonMapper.writeValueAsString(chats);


        stubFor(get(urlMatching(GET_CHATS_LOGIN_PAGE_NUMBER_D))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responce)));


        clickOn("#registerButton");

        try {
            lookup("#addChatButton").queryButton();
        } catch (EmptyNodeQueryException ex) {
            throw new InvalidDataException("Expected behaviour: second form opens. Resulting behaviour: second form is not opened");
        }

        Scene currectScene = getWindows().get(0).getScene();

        ChatFormController controller = ((ChatFormController) currectScene.getUserData());
        controller.timeline.stop();

        //wireMockServer.stop();
        wireMockServer.shutdown();
    }


    private void checkAlertHeaderText(String bundledMessageId) {
        DialogPane dialog;
        List<DialogPane> lst = lookup(".alert").queryAll().stream()
                .map(node -> (DialogPane) node)
                .collect(Collectors.toList());

        if (lst.size() < 1) {
            throw new NoAlertFoundException();
        }
        dialog = lst.get(lst.size()-1);
        //dialog = (DialogPane) lookup(".alert").queryAll().iterator().next();

        String alertTitle = dialog.getHeaderText();
        clickOn("OK");

        //Interesting way to get loaclization for our application(via getting the fxmloader that was store previously)
        LoginFormController concC = ((FXMLLoader) find("#forgetPasswordButton").getScene().getUserData()).getController();
        String expectedMessage = (concC.getBundle().getString(bundledMessageId));
        if (!expectedMessage.equals(alertTitle)) {
            String errtext = (String.format("Expected alert with title \"%s\", got alert with header \"%s\"", expectedMessage, alertTitle));
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
