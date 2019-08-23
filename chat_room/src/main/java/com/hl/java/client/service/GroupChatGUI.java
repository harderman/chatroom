package com.hl.java.client.service;

import com.hl.java.util.CommUtils;
import com.hl.java.vo.MessageVO;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

public class GroupChatGUI {
    private JPanel groupPanel;
    private JTextArea readFromSever;
    private JTextField sendToSever;
    private JPanel friendsPanel;
    private JFrame frame;

    private String groupName;
    private Set<String> friends;
    private String myName;
    private ConnectToServer connectToServer;

    public GroupChatGUI(final String groupName, Set<String> friends,
                        final String myName, final ConnectToServer connectToServer){
        this.groupName = groupName;
        this.friends = friends;
        this.myName = myName;
        this.connectToServer = connectToServer;
        frame= new JFrame("groupName");
        frame.setContentPane(groupPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(400,400);
        frame.setVisible(true);
        //加载群中好友列表
        friendsPanel.setLayout(new BoxLayout(friendsPanel,BoxLayout.Y_AXIS));
        Iterator<String> iterator = friends.iterator();
        while(iterator.hasNext()){
            String labelName = iterator.next();
            JLabel jLabel = new JLabel(labelName);
            friendsPanel.add(jLabel);
        }
        //监听键盘
        sendToSever.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                StringBuilder sb = new StringBuilder();
                sb.append(sendToSever.getText());
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    String strToSever = sb.toString();
                    //type:4
                    //content:myName-msg
                    //to:groupName
                    MessageVO messageVO = new MessageVO();
                    messageVO.setType("4");
                    messageVO.setContent(myName+"-"+strToSever);
                    messageVO.setTo(groupName);
                    try {
                        PrintStream out = new PrintStream(connectToServer.getOs(),true,"UTF-8");
                        out.println(CommUtils.objectToJson(messageVO));
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
    public void readFromSever (String msg){
        readFromSever.append(msg+"\n");
    }

    public JFrame getFrame() {
        return frame;
    }
}
