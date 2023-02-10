package ru.edu.spbstu.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.service.query.EmptyNodeQueryException;
import ru.edu.spbstu.client.controllers.ChatFormController;
import ru.edu.spbstu.client.controllers.ConfigureChatFormController;
import ru.edu.spbstu.client.controllers.ForwardMessageFormController;
import ru.edu.spbstu.client.exception.InvalidDataException;
import ru.edu.spbstu.client.utils.ClientProperties;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.ChatUser;
import ru.edu.spbstu.model.Message;
import ru.edu.spbstu.model.User;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.testfx.internal.JavaVersionAdapter.getWindows;
import static ru.edu.spbstu.client.ChatFormTest.messageGenerator;
import static ru.edu.spbstu.client.ChatFormTest.stubSuccessful;

public class ForgotPasswordFormTest  extends ApplicationTest {
    public static final String IS_USER_PRESENT_LOGIN = "/is_user_present\\?login=.*";
    public static final String IS_EMAIL_USED_EMAIL = "/is_email_used\\?email=.*";




    public static final String MESSAGE_TEXT_AREA = "#messageTextArea";
    public static final String SEND_MESSAGE_BUTTON = "#sendMessageButton";
    private static final String chatListViewStr = "#chatsListView";
    private static WireMockServer wireMockServer;
    private static ObjectMapper jsonMapper = new ObjectMapper();






    //check chat field checks
    //check add users

    private static void reset_to_defaults() throws JsonProcessingException {
        wireMockServer.resetToDefaultMappings();

        //to open 2 form
        stubSuccessful(IS_USER_PRESENT_LOGIN,"get",jsonMapper.writeValueAsString(true));

    }
    @BeforeAll
    public static void initServer() throws Exception
    {
        ClientProperties.setProperties("RU");
        ApplicationTest.launch(ClientApplication.class);
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.err), true, "UTF-8"));
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();//start server


        reset_to_defaults();
    }

    @BeforeEach
    public void initSecondStage()
    {
        ObjectMapper jsonMapper = new ObjectMapper();


        clickOn("#loginTextBox").write("olegoleg");

        clickOn("#forgetPasswordButton");
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
    }
    public <T extends Node> T find(final String query)
    {
        return (T)lookup(query).queryAll().iterator().next();
    }




    @Test
    public void loginSelectWrite()
    {
        clickOn("#emailTextBox").write("sampleText");
        sleep(400);
    }

    @AfterEach
    public void HideStages() throws TimeoutException {
        if(getWindows().size()>0) {
            for(int i=0;i<getWindows().size();i++) {
                Scene currectScene = getWindows().get(i).getScene();
                if(currectScene.getUserData()==null)
                {
                    continue;
                }

                try {
                    ChatFormController controller = ((ChatFormController) currectScene.getUserData());
                    controller.timeline.stop();
                }
                catch (ClassCastException ex)
                {
                    try {
                        ConfigureChatFormController controller = ((ConfigureChatFormController) currectScene.getUserData());
                        controller.timeline.stop();
                    }
                    catch (ClassCastException ex2)
                    {
                        try {
                            ForwardMessageFormController controller = ((ForwardMessageFormController) currectScene.getUserData());
                            controller.timeline.stop();
                        }
                        catch (ClassCastException ex3)
                        {

                        }
                    }
                }
            }


        }
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});

    }


    @AfterAll
    public static void destroy() throws TimeoutException {

        if(getWindows().size()>0) {
            for (int i=0;i<getWindows().size();i++) {

                Scene currectScene = getWindows().get(i).getScene();

                if(currectScene.getUserData()==null)
                {
                    continue;
                }

                try {
                    ChatFormController controller = ((ChatFormController) currectScene.getUserData());
                    controller.timeline.stop();
                }
                catch (ClassCastException ex)
                {
                    try {
                        ConfigureChatFormController controller = ((ConfigureChatFormController) currectScene.getUserData());
                        controller.timeline.stop();
                    }
                    catch (ClassCastException ex2)
                    {
                        ForwardMessageFormController controller = ((ForwardMessageFormController) currectScene.getUserData());
                        controller.timeline.stop();
                    }
                }
            }
        }
        FxToolkit.cleanupStages();
        wireMockServer.shutdown();
        //wireMockServer.stop();
        wireMockServer.resetToDefaultMappings();
    }

    private <T> void clickOnItemInListView(ListView<T>listView,int index,int type)
    {
        VirtualFlow<ListCell<T>> virtualFlow = (VirtualFlow)listView.lookup("#virtual-flow");
        ListCell<T> cell = virtualFlow.getCell(index);
        if(type>0)
            rightClickOn(cell);
        else
        {
            clickOn(cell);
        }
    }

    private <T>  ListCell<T> getListCell(ListView<T>listView,int index)
    {
        VirtualFlow<ListCell<T>> virtualFlow = (VirtualFlow)listView.lookup("#virtual-flow");
        return virtualFlow.getCell(index);
    }


}
