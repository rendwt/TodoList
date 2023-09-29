package com.task.todolist.dao;

import com.task.todolist.model.GroceryList;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroceryListDAO {
    private BasicDataSource dbConnection;
    public GroceryListDAO(BasicDataSource dbConnection) {
        this.dbConnection = dbConnection;
    }

    public int addListItem(String itemName,int qty, String unit, String status) {
        int listItemId = -1;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnection.getConnection();
            statement = connection.prepareStatement("SELECT COALESCE(MAX(itemid), 0) + 1 FROM grocerylist");
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                listItemId = resultSet.getInt(1);
            }

            String query = "insert into grocerylist (itemid, itemname, qty, unit, status) values (?,?,?,?,?)";
            statement = connection.prepareStatement(query);
            statement.setInt(1,listItemId);
            statement.setString(2,itemName);
            statement.setInt(3,qty);
            statement.setString(4,unit);
            statement.setObject(5,status, Types.OTHER);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeResources(connection,statement,resultSet);
        }
        return listItemId;
    }

    public void removeListItem(int itemId){
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        try {
            connection = dbConnection.getConnection();
            String query ="delete from grocerylist where itemid = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1,itemId);
            preparedStatement.executeUpdate();
            /*query ="ALTER SEQUENCE grocerylist_itemid_seq RESTART WITH (SELECT MAX(itemid) + 1 FROM grocerylist)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();*/
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeResources(connection, preparedStatement, null);
        }
    }
    public void updateListItem(int itemId, String status){
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        try {
            connection = dbConnection.getConnection();
            String query ="update grocerylist set status = ? where itemid = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1,status,Types.OTHER);
            preparedStatement.setInt(2,itemId);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeResources(connection, preparedStatement, null);
        }
    }
    public List<GroceryList> getList() {
        List<GroceryList> groceryList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            String query = "SELECT itemid, itemname, qty, unit, status FROM grocerylist";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                GroceryList groceryitem = new GroceryList();
                groceryitem.setItemId(resultSet.getInt("itemid"));
                groceryitem.setItemName(resultSet.getString("itemname"));
                groceryitem.setQty(resultSet.getInt("qty"));
                groceryitem.setUnit(resultSet.getString("unit"));
                groceryitem.setStatus(resultSet.getString("status"));
                groceryList.add(groceryitem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(connection, preparedStatement, resultSet);
        }
        return groceryList;
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
