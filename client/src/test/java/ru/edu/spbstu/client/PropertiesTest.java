package ru.edu.spbstu.client;

import org.assertj.core.internal.bytebuddy.matcher.EqualityMatcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import ru.edu.spbstu.client.utils.ClientProperties;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidClassException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.testfx.api.FxAssert.verifyThat;

public class PropertiesTest  extends ApplicationTest {
    static String[] languages=new String[2];
    @BeforeAll
    static void start()
    {
       languages[0]= "RU";
       languages[1]= "EN";
    }
    @Test
    void testCreateProperties() throws IOException {
        String lang=languages[1];
        ClientProperties.setProperties(lang);
        var props=ClientProperties.getProperties();
        String gotten=props.getProperty("Language");
        if(!gotten.equals(lang))
        {
            throw new InvalidClassException("Expected language "+ lang + String.format("! Stored language %s!",gotten));
        }
    }

    @Test
    void testDefaultProperties() throws IOException {

        File file = new File(String.valueOf(Path.of("properties.prop")));
        if (file.delete()) {
            System.out.println("File deleted successfully");
        } else {
            System.out.println("Failed to delete the file");
        }

        var props=ClientProperties.getProperties();
        String gotten=props.getProperty("Language");
        if(!gotten.equals(languages[0]))
        {
            throw new InvalidClassException("Expected language "+ languages[0] + String.format("! Stored language %s!",gotten));
        }
    }

}
