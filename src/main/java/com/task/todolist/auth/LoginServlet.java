package com.task.todolist.auth;

import com.task.todolist.model.UsersDAO;
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
    private User user;

    public void init() throws ServletException {
        ServletContext context = getServletContext();
        BasicDataSource connectionPool = (BasicDataSource) context.getAttribute("connectionPool");
        usersDAO = new UsersDAO(connectionPool);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();
        if (!userExists(username,password)) {
            session.setAttribute("loginError", "Invalid username or password");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }else {
            session.setAttribute("user",user);
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("username", user.getUsername());
            response.sendRedirect("index.jsp");
        }
    }

    private boolean userExists(String username, String password) {
        user = usersDAO.checkUser(username, password);
        return user != null;
    }
}
