package com.hl.java.client.entity;


import lombok.Data;

import java.util.Set;

@Data
public class User {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    private Integer id;
    private String userName;
    private String passWord;
    private String brief;
    //序列化单元测试测试使用
    private Set<String> userNames;


    public Set<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(Set<String> userNames) {
        this.userNames = userNames;
    }
}
