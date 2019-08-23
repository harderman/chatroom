package com.hl.java.client.service;

import com.hl.java.util.CommUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

public class ConnectToServer {
    private static final String IP;
    private static final int PORT;
    static {
        Properties properties = CommUtils.loadProperties("socket.properties");
        IP = properties.getProperty("address");
        PORT = Integer.parseInt(properties.getProperty("port"));
    }
    private Socket client;
    private InputStream is;
    private OutputStream os;

    public ConnectToServer(){
        try {
            client = new Socket(IP,PORT);
            is = client.getInputStream();
            os = client.getOutputStream();
        } catch (IOException e) {
            System.err.println("与服务器连接失败");
            e.printStackTrace();
        }
    }

    public InputStream getIs() {
        return is;
    }

    public OutputStream getOs() {
        return os;
    }
}
