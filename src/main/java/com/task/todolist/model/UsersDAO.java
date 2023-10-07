package com.task.todolist.model;

import com.task.todolist.Util.PasswordUtil;
import com.task.todolist.model.User;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersDAO {
    private BasicDataSource dbConnection;
    public UsersDAO(BasicDataSource dbConnection) {
        this.dbConnection = dbConnection;
    }

    public int createUser(String username, String password, String salt, String role){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int rowCount =0;
        try {
            connection = dbConnection.getConnection();
            preparedStatement=connection.prepareStatement("insert into users(username, password, salt, role) values(?,?,?,?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, salt);
            preparedStatement.setString(4, role);
            rowCount=preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeResources(connection, preparedStatement, null);
        }
        return rowCount;
    }

    public User checkUser(String username, String inputPassword){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet;
        User user = null;
        try {
            connection = dbConnection.getConnection();
            preparedStatement=connection.prepareStatement("select * from users where username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String storedPasswordHash = resultSet.getString("password");
                String salt = resultSet.getString("salt");
                String hashedInputPassword = PasswordUtil.hashPassword(inputPassword, salt);
                if (storedPasswordHash.equals(hashedInputPassword)) {
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setSalt(resultSet.getString("salt"));
                    user.setRole(resultSet.getString("role"));
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeResources(connection, preparedStatement, null);
        }
        return user;
    }
    public User getUserByUsername(String username){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        int rowCount =0;
        try {
            connection = dbConnection.getConnection();
            preparedStatement=connection.prepareStatement("select * from users where username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setRole(resultSet.getString("role"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeResources(connection, preparedStatement, null);
        }
        return user;
    }
    private void closeResources(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
