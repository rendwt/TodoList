package com.task.todolist.webcontroller;

import com.task.todolist.model.CompletedGroceryListDAO;
import org.apache.commons.dbcp2.BasicDataSource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/updatecompletedlistservlet")
public class UpdateCompletedItemServlet extends HttpServlet {
    private CompletedGroceryListDAO completedGroceryListDAO;

    public void init() throws ServletException {
        ServletContext context = getServletContext();
        BasicDataSource connectionPool = (BasicDataSource) context.getAttribute("connectionPool");
        completedGroceryListDAO = new CompletedGroceryListDAO(connectionPool);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String status = request.getParameter("status");
        if(status.equals("to be completed")){
            completedGroceryListDAO.updateListItemStatus(id,status);
        }else{
            completedGroceryListDAO.removeListItem(id);
        }
    }
}


