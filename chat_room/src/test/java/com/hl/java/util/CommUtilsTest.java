package com.hl.java.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hl.java.client.entity.User;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

public class CommUtilsTest {
    private static final Gson GSON = new GsonBuilder().create();
    @Test
    public void objectToJson() {
        User user = new User();
        user.setId(4);
        user.setUserName("text3");
        user.setPassWord("123");
        user.setBrief("handsome");
        Set<String> set = new HashSet<String>();
        set.add("text5");
        set.add("text6");
        set.add("text7");
        user.setUserNames(set);
        String str = CommUtils.objectToJson(user);
        System.out.println(str);
    }

    @Test
    public void jsonToObject() {
        String str = "{\"id\":4,\"userName\":\"text3\",\"passWord\":\"123\",\"brief\":\"handsome\",\"userNames\":[\"text7\",\"text5\",\"text6\"]}\n";
        User user = (User) CommUtils.JsonToObject(str,User.class);
        System.out.println(user);
        System.out.println(user.getUserNames());
    }
}