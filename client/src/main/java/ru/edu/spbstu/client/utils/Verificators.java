package ru.edu.spbstu.client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Verificators {
    public static boolean isEmail(String email)
    {
        final String EMAIL_PATTERN =
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher m=pattern.matcher(email);
        return m.matches();
    }
}
