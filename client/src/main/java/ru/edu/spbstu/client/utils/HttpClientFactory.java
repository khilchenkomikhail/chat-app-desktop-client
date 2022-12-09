package ru.edu.spbstu.client.utils;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.IOException;
import java.util.Properties;

public class HttpClientFactory {

    private static volatile HttpClientFactory httpClientManagementFactory;

    private volatile HttpClient client;

    private String token;

    private CredentialsProvider credentialsProvider;

    private HttpClientFactory() {
    }

    public static HttpClientFactory getInstance() {
        if (httpClientManagementFactory == null) {
            synchronized (HttpClientFactory.class) {
                if (httpClientManagementFactory == null) {
                    httpClientManagementFactory = new HttpClientFactory();
                }
            }
        }
        return httpClientManagementFactory;
    }

    public void setRememberToken(String token) {
        this.token = token;
        try {
            Properties properties = ClientProperties.getProperties();
            properties.setProperty("token", token);
            ClientProperties.saveProperties(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client = null;
    }

    public void  setCredentialsProvider(CredentialsProvider provider) {
        this.credentialsProvider = provider;
        client = null;
    }

    public HttpClient getHttpClient() throws IOException {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    if (token == null) {
                        Properties properties = ClientProperties.getProperties();
                        String token1 = properties.getProperty("token");
                        if (token1 != null) {
                            token = token1;
                        }
                    }
                    if (token != null) {
                        BasicCookieStore cookieStore = new BasicCookieStore();
                        BasicClientCookie cookie = new BasicClientCookie("remember-me", token);
                        cookie.setDomain("localhost");
                        cookie.setPath("/");
                        cookieStore.addCookie(cookie);

                        client = HttpClientBuilder
                                .create()
                                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                                .setDefaultCookieStore(cookieStore)
                                .build();
                    } else if (credentialsProvider != null) {
                        client = HttpClientBuilder
                                .create()
                                .setDefaultAuthSchemeRegistry(AuthScheme.getAuthScheme())
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .build();
                    } else {
                        throw new RuntimeException("can't build client, specify credential provider or token");
                    }
                }
            }
        }

        return client;
    }
}
