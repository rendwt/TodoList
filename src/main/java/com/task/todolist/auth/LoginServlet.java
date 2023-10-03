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
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UsersDAO usersDAO;
    private User existingUser;
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        BasicDataSource connectionPool = (BasicDataSource) context.getAttribute("connectionPool");
        usersDAO = new UsersDAO(connectionPool);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (!userExists(username,password)) {
            HttpSession session = request.getSession();
            session.setAttribute("loginError", "Invalid username or password");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }else {
            HttpSession session = request.getSession();
            session.setAttribute("user",existingUser);
            session.setAttribute("userRole", existingUser.getRole());
            session.setAttribute("username", existingUser.getUsername());
            response.sendRedirect("index.jsp");
        }
    }

    private boolean userExists(String username, String password) {
        existingUser = usersDAO.checkUser(username, password);
        return existingUser != null;
    }
}
