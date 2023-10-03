package com.task.todolist.auth;

import com.task.todolist.dao.UsersDAO;
import com.task.todolist.model.User;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    private UsersDAO usersDAO;

    public void init() throws ServletException {
        ServletContext context = getServletContext();
        BasicDataSource connectionPool = (BasicDataSource) context.getAttribute("connectionPool");
        usersDAO = new UsersDAO(connectionPool);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        if (isUsernameTaken(username)) {
            response.sendRedirect(request.getContextPath()+"/registration-error.jsp");
        }else {
            try {
                int rowCount = usersDAO.createUser(username, password, role);
                if (rowCount > 0)
                    response.sendRedirect(request.getContextPath() + "/login.jsp");
                else
                    response.sendRedirect(request.getContextPath() + "/registration-error.jsp");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private boolean isUsernameTaken(String username) {
        User existingUser = usersDAO.getUserByUsername(username);
        return existingUser != null;
    }
}
