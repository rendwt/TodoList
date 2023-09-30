package com.task.todolist.dao;

import com.task.todolist.model.GroceryList;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompletedGroceryListDAO {
    private BasicDataSource dbConnection;
    public CompletedGroceryListDAO(BasicDataSource dbConnection) {
        this.dbConnection = dbConnection;
    }


    public void removeListItem(int itemId){
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        try {
            connection = dbConnection.getConnection();
            String query ="delete from completedgrocerylist where itemid = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1,itemId);
            preparedStatement.executeUpdate();
            /*query ="ALTER SEQUENCE grocerylist_itemid_seq RESTART WITH (SELECT MAX(itemid) + 1 FROM grocerylist)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();*/
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeResources(connection, preparedStatement,null, null);
        }
    }


    public void updateListItemStatus(int itemId, String status){
        Connection connection = null;
        PreparedStatement deleteStatement=null;
        PreparedStatement insertStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnection.getConnection();
            connection.setAutoCommit(false);

            String selectQuery = "SELECT * FROM completedgrocerylist WHERE itemid = ?";
            deleteStatement = connection.prepareStatement(selectQuery);
            deleteStatement.setInt(1, itemId);
            resultSet = deleteStatement.executeQuery();

            if (resultSet.next()) {
                String itemName = resultSet.getString("itemname");
                int qty = resultSet.getInt("qty");
                String unit = resultSet.getString("unit");

                String deleteQuery = "DELETE FROM completedgrocerylist WHERE itemid = ?";
                deleteStatement = connection.prepareStatement(deleteQuery);
                deleteStatement.setInt(1, itemId);
                deleteStatement.executeUpdate();

                String insertQuery = "INSERT INTO grocerylist (itemid, itemname, qty, unit, status) VALUES (?, ?, ?, ?, ?)";
                insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setInt(1, itemId);
                insertStatement.setString(2, itemName);
                insertStatement.setInt(3,qty);
                insertStatement.setString(4,unit);
                insertStatement.setObject(5, status, Types.OTHER);
                insertStatement.executeUpdate();

                connection.commit();

            }else {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            closeResources(connection, deleteStatement, insertStatement,null);
        }
    }
    public List<GroceryList> getList() {
        List<GroceryList> groceryList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            String query = "SELECT itemid, itemname, qty, unit, status FROM completedgrocerylist";
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
            closeResources(connection, preparedStatement, null, resultSet);
        }
        return groceryList;
    }
    private void closeResources(Connection connection, PreparedStatement statement,PreparedStatement statement2, ResultSet resultSet) {
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

