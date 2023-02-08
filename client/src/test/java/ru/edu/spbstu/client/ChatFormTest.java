package ru.edu.spbstu.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.base.WindowMatchers;
import org.testfx.service.query.EmptyNodeQueryException;
import ru.edu.spbstu.client.controllers.ChatFormController;
import ru.edu.spbstu.client.controllers.ConfigureChatFormController;
import ru.edu.spbstu.client.exception.InvalidDataException;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.ChatUser;
import ru.edu.spbstu.model.Message;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.internal.JavaVersionAdapter.getWindows;


public class ChatFormTest extends ApplicationTest {
    public static final String GET_CHATS_LOGIN_PAGE_NUMBER_D = "/get_chats\\?login=.*&page_number=\\d+";
    public static final String GET_MESSAGES="/get_messages\\?chat_id=\\d+&page_number=\\d+";
    public static final String GET_IMAGE="/get_profile_photo\\?login=.+";
    public static final String GET_MEMBERS="/get_chat_members\\?chat_id=\\d+";
    private static WireMockServer wireMockServer;

    private static boolean[] intToBooleanArray2(int num) {
        boolean[] res={false,false,false};

        for (int i=0;i<3;i++)
        {
            res[2-i]=num % 2 > 0;
            num/=2;
        }

        return res;
    }
    static Message messageGenerator(Long m_id, Long chat_id, String lg1, String lg2, String text,MessageType type)
    {
        boolean[] arr =intToBooleanArray2(type.value);
        return new Message(m_id,lg1,lg2,chat_id,new Date(),text,arr[0],arr[1],arr[2]);
    }

    enum MessageType {
        SIMPLE(0),
        FORWARDED(1),
        CHANGED(2),
        DELETED(4);

        private final int value;

        MessageType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @BeforeAll
    public static void initServer() throws Exception
    {
        //ApplicationTest.launch(ClientApplication.class);
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.err), true, "UTF-8"));
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();//start server
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

        List<Message> messages = Arrays.asList(messageGenerator(1L,1L,"login1","login1","message1",MessageType.SIMPLE),
                messageGenerator(2L,1L,"olegoleg","olegoleg","message2",MessageType.SIMPLE),
                messageGenerator(2L,1L,"olegoleg","olegoleg","message2",MessageType.SIMPLE));

        String responce2 = jsonMapper.writeValueAsString(messages);
        stubFor(get(urlMatching(GET_MESSAGES))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responce2)));

        stubFor(get(urlMatching(GET_IMAGE))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody((byte[]) null)));

        List<ChatUser> usserlist=Arrays.asList(new ChatUser("login1",false),
                new ChatUser("olegoleg",true),
                new ChatUser("olegoleg2",false));

        stubFor(get(urlMatching(GET_MEMBERS))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonMapper.writeValueAsString(usserlist))));


    }
    @Override
    public void start(Stage stage) throws Exception {
        new ClientApplication().start(stage);
       // stage.show();
    }
    public <T extends Node> T find(final String query)
    {
        return (T)lookup(query).queryAll().iterator().next();
    }
    @BeforeEach
    public void initSecodStage()
    {
        ((TextField)find("#loginTextBox")).setText("olegoleg");
        ((TextField)find("#passwordTextBox")).setText("olegoleg");
        Button b=find("#logInButton");
        b.setDisable(false);
        clickOn("#logInButton");
    }

    @Test
    public void testChatSelect() throws Exception {

        ListView<Chat> chatLst= find("#chatsListView");

        clickOnItemInListView(chatLst,2,0);
    }
    @Test
    public void testButtonsStateDuringStartup()
    {
        verifyThat("#sendMessageButton", NodeMatchers.isDisabled());
    }

    @Test
    public void  testTextInputWithNoChat()
    {
        clickOn("#messageTextArea").write("sampleMessage");
        TextArea tx=find("#messageTextArea");
        assertEquals("",tx.getText());
    }
    @Test
    public void testSendMessage()
    {
        ListView<Chat> chatLst= find("#chatsListView");
        clickOnItemInListView(chatLst,1,0);

        ListView<Message> messageListView=find("#messagesListView");
        clickOnItemInListView(messageListView,1,1);

        Button sendButton=find("#sendMessageButton");
        clickOn( "#messageTextArea").write("message");
        clickOn(sendButton);

    }
    @Test
    public void testExitEditMode()
    {
        ListView<Chat> chatLst= find("#chatsListView");
        clickOnItemInListView(chatLst,1,0);

        ListView<Message> messageListView=find("#messagesListView");
        clickOnItemInListView(messageListView,1,1);
        clickOn("#EditMessageButton");
        Button sendButton=find("#sendMessageButton");

        Scene currectScene = getWindows().get(0).getScene();
        ChatFormController controller = ((ChatFormController) currectScene.getUserData());
        assertEquals(controller.getBundle().getString("editMessageButton") ,sendButton.getText());
        TextArea messageTexAread=find("#messageTextArea");
        var cell=getListCell(messageListView,1);

        assertEquals(cell.getItem().getContent() ,messageTexAread.getText());
        press(KeyCode.ESCAPE);
        release(KeyCode.ESCAPE);
        assertEquals(controller.getBundle().getString("SendMessageButton") ,sendButton.getText());
        assertEquals("",messageTexAread.getText());
    }


    @Test
    public void testExitReplyMode(){
        ListView<Chat> chatLst= find("#chatsListView");
        clickOnItemInListView(chatLst,1,0);

        ListView<Message> messageListView=find("#messagesListView");
        clickOnItemInListView(messageListView,1,1);
        clickOn("#AnswerMessageButton");
        Button sendButton=find("#sendMessageButton");


        Scene currectScene = getWindows().get(0).getScene();
        ChatFormController controller = ((ChatFormController) currectScene.getUserData());
        assertEquals(controller.getBundle().getString("AnswerMessageButton") ,sendButton.getText());
        press(KeyCode.ESCAPE);
        release(KeyCode.ESCAPE);
        assertEquals(controller.getBundle().getString("SendMessageButton") ,sendButton.getText());
    }

    @Test
    public void testContextMenu()
    {
        ListView<Chat> chatLst= find("#chatsListView");
        clickOnItemInListView(chatLst,1,1);
        clickOn("#ConfigChatButton");
        Scene currectScene = getWindows().get(0).getScene();

        ConfigureChatFormController controller = ((ConfigureChatFormController) currectScene.getUserData());
        controller.timeline.stop();
    }


    @AfterEach
    public void HideStages() throws TimeoutException {
        if(getWindows().size()>0) {
            Scene currectScene = getWindows().get(0).getScene();
            try {
                ChatFormController controller = ((ChatFormController) currectScene.getUserData());
                controller.timeline.stop();
            }
            catch (ClassCastException ex)
            {
                ConfigureChatFormController controller = ((ConfigureChatFormController) currectScene.getUserData());
                controller.timeline.stop();
            }


        }
        FxToolkit.cleanupStages();
         release(new KeyCode[]{});
         release(new MouseButton[]{});

    }


    @AfterAll
    public static void destroy() throws TimeoutException {

        if(getWindows().size()>0) {
            for (int i=0;i<getWindows().size();i++) {

                Scene currectScene = getWindows().get(i).getScene();

                try {
                    ChatFormController controller = ((ChatFormController) currectScene.getUserData());
                    controller.timeline.stop();
                }
                catch (ClassCastException ex)
                {
                    ConfigureChatFormController controller = ((ConfigureChatFormController) currectScene.getUserData());
                    if(controller!=null)
                    controller.timeline.stop();
                }
            }
        }
        FxToolkit.cleanupStages();
        wireMockServer.stop();
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
