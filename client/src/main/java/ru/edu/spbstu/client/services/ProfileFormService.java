package ru.edu.spbstu.client.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

public class ProfileFormService {
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private CredentialsProvider prov = new BasicCredentialsProvider();
    private String login;


    public CredentialsProvider getCredentialsProvider()
    {
        return prov;
    }
    public String getLogin()
    {
        return login;
    }

    public void setCredentialsProvider(CredentialsProvider prov,String login)
    {
        this.prov=prov;
        this.login=login;
    }

    public Image getImage() {

        Image image;
        var res=(getClass().getResource("/images/dAvatar.bmp")).getPath().replaceFirst("/","");
        image=new Image(res);//Todo пока не совсем понял что тут делать

        return  image;
    }
}
