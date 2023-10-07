package com.task.todolist.Util;

import org.apache.commons.dbcp2.BasicDataSource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@WebServlet(value = "/DbConnectionServlet",loadOnStartup = 1)
public class DbConnection extends HttpServlet {
    private static final String DB_PROPERTIES_PATH = "/WEB-INF/db.properties";
    private BasicDataSource connectionPool;



    public void init() throws ServletException {
        ServletContext context = getServletContext();
        String dbPropertiesPath = context.getRealPath(DB_PROPERTIES_PATH);

        try (FileInputStream input = new FileInputStream(dbPropertiesPath)) {
            Properties dbProperties = new Properties();
            dbProperties.load(input);

            String jdbcDriver = dbProperties.getProperty("jdbc.driver");
            String jdbcUrl = dbProperties.getProperty("jdbc.url");
            String jdbcUsername = dbProperties.getProperty("jdbc.username");
            String jdbcPassword = dbProperties.getProperty("jdbc.password");

            if (jdbcDriver == null || jdbcUrl == null || jdbcUsername == null || jdbcPassword == null) {
                throw new ServletException("One or more required properties are missing in db.properties.");
            }

            connectionPool = new BasicDataSource();
            connectionPool.setDriverClassName(jdbcDriver);
            connectionPool.setUrl(jdbcUrl);
            connectionPool.setUsername(jdbcUsername);
            connectionPool.setPassword(jdbcPassword);

            context.setAttribute("connectionPool", connectionPool);
        } catch (IOException e) {
            throw new ServletException("Failed to initialize database connection pool", e);
        }
    }

    private boolean isConnectionValid(Connection connection) {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    public  BasicDataSource getDataSource() {
        return connectionPool;
    }
    public Connection getConnection() throws SQLException {
        if (connectionPool == null) {
            throw new SQLException("Connection pool is not initialized.");
        }
        Connection connection = connectionPool.getConnection();
        if (!isConnectionValid(connection)) {
            throw new SQLException("Invalid database connection.");
        }
        return connection;
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void reloadConnection() {
        ServletContext context = getServletContext();
        String dbPropertiesPath = context.getRealPath(DB_PROPERTIES_PATH);

        try (FileInputStream input = new FileInputStream(dbPropertiesPath)) {
            Properties dbProperties = new Properties();
            dbProperties.load(input);

            String jdbcDriver = dbProperties.getProperty("jdbc.driver");
            String jdbcUrl = dbProperties.getProperty("jdbc.url");
            String jdbcUsername = dbProperties.getProperty("jdbc.username");
            String jdbcPassword = dbProperties.getProperty("jdbc.password");

            if (jdbcDriver == null || jdbcUrl == null || jdbcUsername == null || jdbcPassword == null) {
                throw new ServletException("One or more required properties are missing in db.properties.");
            }

            connectionPool.setDriverClassName(jdbcDriver);
            connectionPool.setUrl(jdbcUrl);
            connectionPool.setUsername(jdbcUsername);
            connectionPool.setPassword(jdbcPassword);

        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }


    public void destroy() {
        ServletContext context = getServletContext();
        BasicDataSource dataSource = (BasicDataSource) context.getAttribute("dataSource");
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
