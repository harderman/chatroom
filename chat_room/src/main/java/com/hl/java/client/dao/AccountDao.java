package com.hl.java.client.dao;

import com.hl.java.client.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;

public class AccountDao extends BasedDao{
    //用户注册(insert)
    public boolean userReg(User user){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            String sql = "INSERT INTO user(username,password,brief)" + "VALUES (?,?,?)";
            statement = connection.prepareStatement(sql,statement.RETURN_GENERATED_KEYS);
            statement.setString(1,user.getUserName());
            //密码加密
            statement.setString(2,DigestUtils.md5Hex(user.getPassWord()));
            statement.setString(3,user.getBrief());
            int rows = statement.executeUpdate();
            if(rows == 1){
                return true;
            }
        } catch (SQLException e) {
            System.err.println("注册失败");
            e.printStackTrace();
        }finally {
            CloseResources(connection,statement);
        }
        return false;
    }
//用户登录
    public User userLogin(String userName,String password){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            String sql = "SELECT * FROM user WHERE username =?AND password =?";
            statement = connection.prepareStatement(sql);
            statement.setString(1,userName);
            statement.setString(2,DigestUtils.md5Hex(password));
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                User user = getUser(resultSet);
                return user;
            }
        } catch (SQLException e) {
            System.err.println("登录失败");
            e.printStackTrace();
        }finally {
            CloseResources(connection,statement);
        }
        return null;
    }
    private User getUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUserName(resultSet.getString("username"));
        user.setPassWord(resultSet.getString("password"));
        user.setBrief(resultSet.getString("brief"));
        return user;
    }
}
