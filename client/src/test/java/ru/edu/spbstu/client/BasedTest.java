package ru.edu.spbstu.client;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeoutException;

public class BasedTest extends ApplicationTest {
    @BeforeEach
    public void setUpClass() throws Exception {
        ApplicationTest.launch(ClientApplication.class);
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.err), true, "UTF-8"));
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
    }
    @AfterEach
    public void afterEachTest() throws TimeoutException {

        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
    public <T extends Node> T find(final String query)
    {
        return (T)lookup(query).queryAll().iterator().next();
    }

}
