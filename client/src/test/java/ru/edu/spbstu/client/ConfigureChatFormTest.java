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
import ru.edu.spbstu.client.controllers.ChatFormController;
import ru.edu.spbstu.client.controllers.ConfigureChatFormController;
import ru.edu.spbstu.client.controllers.ForwardMessageFormController;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testfx.internal.JavaVersionAdapter.getWindows;
import static ru.edu.spbstu.client.ChatFormTest.messageGenerator;
import static ru.edu.spbstu.client.ChatFormTest.stubSuccessful;


public class ConfigureChatFormTest extends ApplicationTest {
    public static final String GET_CHATS_LOGIN_PAGE_NUMBER_D = "/get_chats\\?login=.*&page_number=\\d+";
    public static final String GET_MESSAGES="/get_messages\\?chat_id=\\d+&page_number=\\d+";
    public static final String GET_IMAGE="/get_profile_photo\\?login=.+";
    public static final String GET_MEMBERS="/get_chat_members\\?chat_id=\\d+";
    public static final String DELETE_USERS="/delete_users_from_chat";
    public static final String MAKE_ADMINS="/make_users_admins";
    public static final String SEND_MESSAGE="/send_message";
    public static final String FORWARD_MESSAGE="/forward_message\\?message_id=\\d+&sender_login=.*&chat_id=\\d+";
    public static final String GET_EMAIL="/get_user\\?login=.*";
    public static final String EDIT_MESSAGE="/edit_message";
    public static final String DELETE_MESSAGE="/delete_message\\?message_id=\\d+";
    public static final String FIND_CHATS="/get_chats_by_search\\?login=.*&begin=.*&page_number=\\d+";




    private static final String chatListViewStr = "#chatsListView";
    private static WireMockServer wireMockServer;
    private static  ObjectMapper jsonMapper = new ObjectMapper();

    private static List<Chat> chats = new ArrayList<>(Arrays.asList(new Chat(1L, "first"),
            new Chat(2L, "second"),
            new Chat(3L, "third")));

    private static ArrayList<Message> messages = new ArrayList<>(Arrays.asList(messageGenerator(1L,1L,"login1","login1","message1", ChatFormTest.MessageType.SIMPLE),
            messageGenerator(2L,1L,"olegoleg","olegoleg","message21", ChatFormTest.MessageType.SIMPLE),
            messageGenerator(3L,1L,"olegoleg","olegoleg","message22", ChatFormTest.MessageType.SIMPLE)));
    private final String MESSAGE_LIST_VIEW = "#messagesListView";


    //check chat field checks
    //check add users

    private static void reset_to_defaults() throws JsonProcessingException {
        wireMockServer.resetToDefaultMappings();
        String responce = jsonMapper.writeValueAsString(chats);

        //to open 2 form
        stubSuccessful(GET_CHATS_LOGIN_PAGE_NUMBER_D,"get",responce);

        //to find chats
        stubSuccessful(FIND_CHATS,"get",responce);

        //get messages
        {

          //  String responce2 = jsonMapper.writeValueAsString(messages);
            //stubSuccessful(GET_MESSAGES,"get",responce2);
            stubSuccessful(GET_IMAGE,"get", String.valueOf((byte[]) null));
        }
        List<ChatUser> usserlist=Arrays.asList(new ChatUser("login1",false),
                new ChatUser("olegoleg",true),
                new ChatUser("olegoleg2",false));

        //to config
        stubSuccessful(GET_MEMBERS,"get", jsonMapper.writeValueAsString(usserlist));
        //to exit chat
        {
            stubSuccessful(DELETE_USERS,"patch", jsonMapper.writeValueAsString(usserlist));
            stubSuccessful(MAKE_ADMINS,"patch", jsonMapper.writeValueAsString(usserlist));
        }
        //stubSuccessful(SEND_MESSAGE,"post", String.valueOf((byte[]) null));
       // stubSuccessful(FORWARD_MESSAGE,"post", String.valueOf((byte[]) null));

        stubSuccessful(GET_EMAIL,"get", jsonMapper.writeValueAsString(new User("olegoleg","olegoleg","olegoleg@gmail.com")));
        //stubSuccessful(EDIT_MESSAGE,"patch", jsonMapper.writeValueAsString(new User("olegoleg","olegoleg","olegoleg@gmail.com")));
       // stubSuccessful(DELETE_MESSAGE,"patch", jsonMapper.writeValueAsString(new User("olegoleg","olegoleg","olegoleg@gmail.com")));

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
        ((TextField)find("#loginTextBox")).setText("olegoleg");
        ((TextField)find("#passwordTextBox")).setText("olegoleg");
        Button b=find("#logInButton");
        b.setDisable(false);
        clickOn("#logInButton");
        ListView<Chat> chatListView=find(chatListViewStr);

        clickOnItemInListView(chatListView,0,1);
        clickOn("#ConfigChatButton");
    }

    @Override
    public void start(Stage stage) throws Exception {
       stage.show();
    }
    private static Long getLastMessageId()
    {
        return messages.get(messages.size()-1).getId();
    }
    public <T extends Node> T find(final String query)
    {
        return (T)lookup(query).queryAll().iterator().next();
    }




    @Test
    public void loginSelectWrite()
    {
        clickOn("#loginTextField").write("Llogin");
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
       // wireMockServer.stop();
        wireMockServer.shutdown();
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
