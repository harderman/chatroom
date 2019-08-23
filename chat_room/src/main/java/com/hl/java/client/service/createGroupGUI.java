package com.hl.java.client.service;

import com.hl.java.util.CommUtils;
import com.hl.java.vo.MessageVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class createGroupGUI {
    private JPanel CreateGroupPanel;
    private JTextField groupNameText;
    private JButton conformBtn;
    private JPanel friendLabelPanel;

    private String myName;
    private Set<String> friends;
    private ConnectToServer connectToServer;
    private FriendsList friendsList;
//TODO:为何是final
    public createGroupGUI(final String myName, Set friends,
                          final ConnectToServer connectToServer, final FriendsList friendsList) {
        this.myName = myName;
        this.friends = friends;
        this.connectToServer = connectToServer;
        final JFrame frame = new JFrame("创建群组");
        frame.setContentPane(CreateGroupPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,300);
        frame.setLocationRelativeTo(null);
       // frame.pack();
        frame.setVisible(true);
        //将在线好友以chackBox展示到界面中
        //纵向排列
        friendLabelPanel.setLayout(new BoxLayout(friendLabelPanel,BoxLayout.Y_AXIS));
        JCheckBox[] checkBoxes = new JCheckBox[friends.size()];
        Iterator<String> iterator = friends.iterator();
        int i  =0;
        while(iterator.hasNext()){
            String labelName = iterator.next();
            checkBoxes[i] = new JCheckBox(labelName);
            friendLabelPanel.add(checkBoxes[i]);
            i++;
        }
        friendLabelPanel.revalidate();
        //点击提交按钮提交信息到服务端
        conformBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Set<String> selectedFriends = new HashSet<String>();
                //1.判断哪些好友选中加入群聊   JComponent有所有组件
                Component[] comps =  friendLabelPanel.getComponents();
                for(Component comp:comps){
                    JCheckBox checkBox = (JCheckBox) comp;
                    if(checkBox.isSelected()){
                        String labelName = checkBox.getText();
                        selectedFriends.add(labelName);
                    }
                }
                //自己建群默认包含自己
                selectedFriends.add(myName);
                //2.获取群名输入框输入的群名称
                String groupName = groupNameText.getText();
                //3.将群名+选中好友信息发送到服务端
                //type:3
                //content:groupName
                //to:[user1,user2,user3...]
                MessageVO messageVO = new MessageVO();
                messageVO.setType("3");
                messageVO.setContent(groupName);
                messageVO.setTo(CommUtils.objectToJson(selectedFriends));
                try {
                    PrintStream out = new PrintStream(connectToServer.getOs(),
                            true,"UTF-8");
                    out.println(CommUtils.objectToJson(messageVO));
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                //4.将当前建群界面隐藏，刷新好友列表界面的群列表
                frame.setVisible(false);
                //this
                friendsList.addGroup(groupName,selectedFriends);
                friendsList.loadGroupList();
            }
        });
    }
}
