package com.hl.java.sever;

import com.hl.java.util.CommUtils;
import com.hl.java.vo.MessageVO;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadSever {
    private static final String IP;
    private static final int PORT;
    //缓存当前服务器的所有在线客户端信息
    private static Map<String,Socket> clients = new ConcurrentHashMap<String, Socket>() ;
    //缓存当前服务器注册的所有群名称及群好友
    private static Map<String,Set<String>> groups = new ConcurrentHashMap<String, Set<String>>();
    static {
        Properties properties = CommUtils.loadProperties("socket.properties");
       IP = properties.getProperty("address");
       PORT = Integer.parseInt(properties.getProperty("port"));
    }
    public static class ExecuteClient implements Runnable{
        private Socket client;
        private Scanner in;
        private PrintStream out;

        public ExecuteClient(Socket client) {
            this.client = client;
            try {
                //服务端接受客户端的输入输出流
                this.in = new Scanner(client.getInputStream());
                this.out = new PrintStream(client.getOutputStream(),true,"UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while(true){
                if(in.hasNextLine()){
                    String jsonStrFromClient = in.nextLine();
                    MessageVO messageFromClient = (MessageVO) CommUtils.JsonToObject(jsonStrFromClient,MessageVO.class);
                    //新用户注册到服务端
                    if(messageFromClient.getType().equals("1")){
                        String userName = messageFromClient.getContent();
                        //将当前在线的所有用户名发回客户端
                        MessageVO msgToClient = new MessageVO();
                        msgToClient.setType("1");
                        //将当前所有在线的用户发回给客户端
                        msgToClient.setContent(CommUtils.objectToJson(clients.keySet()));
                        //刷新，将新上线的用户信息发回给当前已在线的用户
                        out.println(CommUtils.objectToJson(msgToClient));
                        //out.println(msgToClient);
                        sendUserLogin("newLogin:"+userName);
                        //将当前的新用户注册到服务端缓存
                        clients.put(userName,client);
                        System.out.println(userName+"上线了");
                        System.out.println("当前聊天室共有："+clients.size()+"人");
                    }
                    // 用户私聊
                    else if(messageFromClient.getType().equals("2")){
                    // type:2
                    //  Content:myName-msg
                    //  to:friendName
                        String friendName = messageFromClient.getTo();
                        Socket clientSocket = clients.get(friendName);
                        try {
                            PrintStream out = new PrintStream(clientSocket.getOutputStream(),
                                    true,"UTF-8");
                            MessageVO msgToClient = new MessageVO();
                            msgToClient.setType("2");
                            msgToClient.setContent(messageFromClient.getContent());
                            System.out.println("收到私聊消息，内容为"+messageFromClient.getContent());
                            out.println(CommUtils.objectToJson(msgToClient));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(messageFromClient.getType().equals("3")){
                        //注册群
                        String groupName = messageFromClient.getContent();
                        //该群的所有群成员
                        Set<String> friends = (Set<String>)
                                CommUtils.JsonToObject(messageFromClient.getTo(),Set.class);
                        groups.put(groupName,friends);
                        System.out.println("有新群注册成功，群名称为"+groupName+",一共有"+groups.size()+"个群");
                    }else if(messageFromClient.getType().equals("4")){
                        //群聊信息
                        String groupName = messageFromClient.getTo();
                        Set<String> names = groups.get(groupName);
                        Iterator<String> iterator = names.iterator();
                        while(iterator.hasNext()){
                            String socketName = iterator.next();
                            Socket client = clients.get(socketName);
                            try {
                                PrintStream out = new PrintStream(client.getOutputStream(),
                                        true,"UTF-8");
                                MessageVO messageVO = new MessageVO();
                                messageVO.setType("4");
                                messageVO.setContent(messageFromClient.getContent());
                                //群名-[]
                                messageVO.setTo(groupName+"-"+CommUtils.objectToJson(names));
                                out.println(CommUtils.objectToJson(messageVO));

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        /**
         *
         * 向所有在线用户发送新用户上线信息
         * @param msg
         */
        private void sendUserLogin(String msg){

            for(Map.Entry<String,Socket> entry :clients.entrySet()){
                Socket socket = entry.getValue();
                try {
                    PrintStream out = new PrintStream(socket.getOutputStream(),true,"UTF-8");
                    out.println(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        ExecutorService executor =  Executors.newFixedThreadPool(50);
        for (int i = 0; i < 50; i++) {
            System.out.println("等待客户端连接........");
            Socket client = serverSocket.accept();
            System.out.println("有新的连接，端口号为："+client.getPort());
            executor.submit(new ExecuteClient(client));
        }
    }
}
