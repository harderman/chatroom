package com.hl.java.client.service;

import com.hl.java.util.CommUtils;
import com.hl.java.vo.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FriendsList {
    private JPanel friendsPanel;
    private JScrollPane friendsList;
    private JButton createGroupBtn;
    private JScrollPane groupListPanel;
    private JFrame frame;

    private String userName;
    //存储所有在线好友
    private Set<String> users;
    //存储所有群，及群成员，每一个群名对应“一串”好友
    private Map<String ,Set<String>> groupList = new ConcurrentHashMap<String, Set<String>>();
//    private Set<String> friends;

    private ConnectToServer connectToServer;
    //将已经创建好的私聊列表缓存，key是好友名，value是私聊窗口对象
    private Map<String,PrivateChatGUI> privateChatGUIMap = new ConcurrentHashMap<String, PrivateChatGUI>();
    //缓存所有群聊界面
    private Map<String,GroupChatGUI> groupChatGUIMap = new ConcurrentHashMap<String, GroupChatGUI>();

    //好友列表后台任务，不断监听服务器发来的消息
    //包括好友上线信息、私聊信息，群聊
    private class DaemonTask implements Runnable{
        private Scanner in = new Scanner(connectToServer.getIs());
        @Override
        public void run() {
            while(true){
                //收到服务器发来的信息
                if(in.hasNextLine()){
                    String strFormSever = in.nextLine();
                    //服务器发来json字符串
                    if(strFormSever.startsWith("{")){
                        //json反序列化
                        MessageVO messageVO = (MessageVO) CommUtils.JsonToObject(strFormSever,MessageVO.class);
                        if(messageVO.getType().equals("2")){
                            String friendName = messageVO.getContent().split("-")[0];
                            String msg = messageVO.getContent().split("-")[1];
                            //判断此私聊是否是第一次创建
                            if(privateChatGUIMap.containsKey(friendName)){
                                PrivateChatGUI privateChatGUI = privateChatGUIMap.get(friendName);
                                privateChatGUI.getFrame().setVisible(true);
                                privateChatGUI.readFormSeverMethod(friendName+"说"+msg);
                            }else{
                                PrivateChatGUI privateChatGUI = new PrivateChatGUI(friendName,userName,connectToServer);
                                privateChatGUIMap.put(friendName,privateChatGUI);
                                privateChatGUI.readFormSeverMethod(friendName+"说"+msg);
                            }
                        }else if(messageVO.getType().equals("4")){
                            //收到服务器发来的群聊消息
                            //type：4
                            //content：sender-msg
                            //to：groupName-[1,2,3,...]
                            String groupName = messageVO.getTo().split("-")[0];
                            String senderName = messageVO.getContent().split("-")[0];
                            String groupMsg = messageVO.getContent().split("-")[1];
                            //若此群名称在群聊列表
                            if(groupList.containsKey(groupName)){
                                if(groupChatGUIMap.containsKey(groupName)){
                                    //只需弹出群聊界面
                                    GroupChatGUI groupChatGUI = groupChatGUIMap.get(groupName);
                                    groupChatGUI.getFrame().setVisible(true);
                                    groupChatGUI.readFromSever(senderName+"说"+groupMsg);
                                }else{
                                    Set<String> names = groupList.get(groupName);
                                    GroupChatGUI groupChatGUI = new GroupChatGUI(groupName,names,userName,connectToServer);
                                    groupChatGUIMap.put(groupName,groupChatGUI);
                                    groupChatGUI.readFromSever(senderName+"说"+groupMsg);
                                }
                            }else{
                                //若群成员第一次收到群聊消息
                                //1.将群名称以及群成员保存到当前客户端群聊列表
                                Set<String> friends = (Set<String>) CommUtils.JsonToObject(messageVO.getTo().split("-")[1],
                                        Set.class);
                                groupList.put(groupName,friends);
                                loadGroupList();
                                //2.弹出群聊界面
                                GroupChatGUI groupChatGUI = new GroupChatGUI(groupName,friends,userName,connectToServer);
                                groupChatGUIMap.put(groupName,groupChatGUI);
                                groupChatGUI.readFromSever(senderName+"说"+groupMsg);

                            }

                        }
                    }else{
                        //此时发来的是newLogin:userName
                        if(strFormSever.startsWith("newLogin:")){
                            String newFriendsName = strFormSever.split(":")[1];
                            users.add(newFriendsName);
                            //弹框提示用户上线
                            JOptionPane.showMessageDialog(frame,newFriendsName+"上线了",
                                          "上线提醒",JOptionPane.INFORMATION_MESSAGE);
                            //刷新好友列表
                            loadUsers();
                        }
                    }
                }
            }
        }
    }
//私聊点击
    private class PrivateLabelAction implements MouseListener{
        private String labelName;

        public PrivateLabelAction(String labelName) {
            this.labelName = labelName;
        }

        //鼠标点击执行事件
        @Override
        public void mouseClicked(MouseEvent e) {
            //判断好友列表私聊界面缓存是否已经有指定标签
            if(privateChatGUIMap.containsKey(labelName)){
                PrivateChatGUI privateChatGUI = privateChatGUIMap.get(labelName);
                privateChatGUI.getFrame().setVisible(true);
            }else{
                //第一次点击，创建私聊界面
                PrivateChatGUI privateChatGUI = new PrivateChatGUI(labelName,userName,connectToServer);
                privateChatGUIMap.put(labelName,privateChatGUI);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

//群聊点击
    private class GroupLabelActon implements MouseListener{
        private String groupName;

    public GroupLabelActon(String groupName) {
        this.groupName = groupName;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
       if(groupChatGUIMap.containsKey(groupName)){
           GroupChatGUI groupChatGUI = groupChatGUIMap.get(groupName);
           groupChatGUI.getFrame().setVisible(true);
       }else{
           Set<String> names = groupList.get(groupName);
           GroupChatGUI groupChatGUI = new GroupChatGUI(groupName,names,userName,connectToServer);
           groupChatGUIMap.put(groupName,groupChatGUI);
       }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

    public FriendsList(final String userName, final Set<String> users,
                       ConnectToServer connect2Server) {
        this.userName = userName;
        this.users = users;
        this.connectToServer = connect2Server;
        frame = new JFrame(userName);
        frame.setContentPane(friendsPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,300);
        frame.setLocationRelativeTo(null);
       // frame.pack();
        frame.setVisible(true);
        loadUsers();
        //启动后台线程不断监听服务器发来的消息
        Thread daemonThread = new Thread(new DaemonTask());
        //置为后台线程
        daemonThread.setDaemon(true);
        daemonThread.start();

        //创建群组
        createGroupBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new createGroupGUI(userName,users,connectToServer,
                        FriendsList.this);
            }
        });
    }

    //加载所有在线的用户信息
    public void loadUsers(){
        JLabel[] userLabels = new JLabel[users.size()];
        JPanel friends = new JPanel();
        //set遍历
        friends.setLayout(new BoxLayout(friends,BoxLayout.Y_AXIS));
        Iterator<String> iterator = users.iterator();
        int i = 0;
        while(iterator.hasNext()){
            String userName = iterator.next();
            userLabels[i] = new JLabel(userName);
            //添加标签点击事件
            userLabels[i].addMouseListener(new PrivateLabelAction(userName));
            friends.add(userLabels[i]);
            i++;
        }
        friendsList.setViewportView(friends);
        //设置滚动条垂直滚动
        friendsList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //刷新界面
        friends.revalidate();
        friendsList.revalidate();
    }
    //刷新群列表
    public void loadGroupList(){
        //存储所有群名称标签的JPanel
        JPanel groupNamePanel = new JPanel();
        groupNamePanel.setLayout(new BoxLayout(groupNamePanel,BoxLayout.Y_AXIS));
        JLabel[] labels = new JLabel[groupList.size()];
        //Map集合的遍历
        Set<Map.Entry<String,Set<String>>> entries = groupList.entrySet();
        Iterator<Map.Entry<String,Set<String>>> iterator = entries.iterator();
        int i = 0;
        while(iterator.hasNext()){
            Map.Entry<String,Set<String>> entry = iterator.next();
            labels[i] = new JLabel(entry.getKey());
            //刷新列表时，将上面的点击事件加进去
            labels[i].addMouseListener(new GroupLabelActon(entry.getKey()));
            groupNamePanel.add(labels[i]);
            i++;
        }
        groupListPanel.setViewportView(groupNamePanel);
        groupListPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        groupListPanel.revalidate();
    }
    public void addGroup(String groupName,Set<String> friends){
        groupList.put(groupName,friends);
    }
}
