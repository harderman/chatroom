package com.hl.java.client.service;

import com.hl.java.util.CommUtils;
import com.hl.java.vo.MessageVO;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class PrivateChatGUI {
    private JPanel privateChatPanel;
    private JTextArea readFromSever;
    private JTextField sendToSever;
    private  JFrame frame;

    private String friendName;
    private String  myName;
    private ConnectToServer connectToServer;
    private PrintStream out;

    public PrivateChatGUI(final String friendName, final String myName,
                          ConnectToServer connectToServer){
        this.friendName = friendName;
        this.myName = myName;
        this.connectToServer = connectToServer;
        try {
            out = new PrintStream(connectToServer.getOs(),true,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        frame = new JFrame("与"+ friendName +"私聊中...");
        frame.setContentPane(privateChatPanel);
        //设置窗口关闭的操作，将其设置为隐藏
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400,400);
        frame.setVisible(true);

        //捕捉输入框的键盘输入
        sendToSever.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                StringBuilder sb = new StringBuilder();
                sb.append(sendToSever.getText());
                //1.当捕捉到按下Enter
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    //2.将当前信息发送到服务端
                    String msg = sb.toString();
                    MessageVO messageVO = new MessageVO();
                    messageVO.setType("2");
                    messageVO.setContent(myName+"-"+msg);
                    messageVO.setTo(friendName);
                    PrivateChatGUI.this.out.println(CommUtils.objectToJson(messageVO));
                    //3.将自己发送的信息展示到当前私聊页面
                    readFormSeverMethod(myName+"说："+msg);
                    sendToSever.setText("");
                }
            }
        });
    }
    public void readFormSeverMethod(String msg){
        readFromSever.append(msg+"\n");
    }

    public JFrame getFrame(){
        return frame;
    }
}

