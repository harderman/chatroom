package com.hl.java.client.dao;

import com.hl.java.client.entity.User;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountDaoTest {
    private AccountDao accountDao = new AccountDao();

    @Test
    public void userReg() {
        User user = new User();
        user.setUserName("马化腾");
        user.setBrief("再冲5万，你会更强！");
        user.setPassWord("123456");
        boolean flag  = accountDao.userReg(user);
        Assert.assertTrue(flag);
    }

    @Test
    public void userLogin() {
        String userName = "Jack 马";
        String password = "123456";
        User user = accountDao.userLogin(userName,password);
        System.out.println(user);
        Assert.assertNotNull(user);
    }
}