package com.task.todolist.model;

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
            closeResources(connection,statement,null, resultSet);
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
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeResources(connection, preparedStatement, null, null);
        }
    }

    public void editListItem(int itemId, String itemName, int qty, String unit){
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        try {
            connection = dbConnection.getConnection();
            String query ="update grocerylist SET itemname = ?, qty = ?, unit = ? WHERE itemid = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,itemName);
            preparedStatement.setInt(2,qty);
            preparedStatement.setString(3,unit);
            preparedStatement.setInt(4,itemId);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeResources(connection, preparedStatement, null, null);
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

            String selectQuery = "SELECT * FROM grocerylist WHERE itemid = ?";
            deleteStatement = connection.prepareStatement(selectQuery);
            deleteStatement.setInt(1, itemId);
            resultSet = deleteStatement.executeQuery();

            if (resultSet.next()) {
                String itemName = resultSet.getString("itemname");
                int qty = resultSet.getInt("qty");
                String unit = resultSet.getString("unit");

                String deleteQuery = "DELETE FROM grocerylist WHERE itemid = ?";
                deleteStatement = connection.prepareStatement(deleteQuery);
                deleteStatement.setInt(1, itemId);
                 deleteStatement.executeUpdate();

                String insertQuery = "INSERT INTO completedgrocerylist (itemid, itemname, qty, unit, status) VALUES (?, ?, ?, ?, ?)";
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
