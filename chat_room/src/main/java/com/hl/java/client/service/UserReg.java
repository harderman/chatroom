package com.hl.java.client.service;
//用户注册
import com.hl.java.client.dao.AccountDao;
import com.hl.java.client.entity.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserReg {
    private JPanel userRegPanel;
    private JTextField userNameText;
    private JPasswordField passwordText;
    private JTextField briefText;
    private JButton regBtn;
    private AccountDao accountDao = new AccountDao();
    public UserReg(){
        final JFrame frame = new JFrame("用户注册");
        //
        frame.setContentPane(userRegPanel);
        //把界面差掉，此线程才停止
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置默认弹窗位置
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        //点击注册按钮，将数据持久化到db中，成功弹出提示框
        regBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //获取用户输入的注册信息
                String userName = userNameText.getText();
                String password = String.valueOf(passwordText.getPassword());
                String brief = briefText.getText();
                //将输入信息包装为User类存到数据库中
                User user = new User();
                user.setUserName(userName);
                user.setPassWord(password);
                user.setBrief(brief);
                //调用dao对象
                if(accountDao.userReg(user)){
                    //弹出提示框
                    JOptionPane.showMessageDialog(frame,"注册成功","提示信息",JOptionPane.INFORMATION_MESSAGE);
                    frame.setVisible(false);
                }else{
                    //保留当前页面
                    JOptionPane.showMessageDialog(frame,"注册失败","提示信息",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }
}



