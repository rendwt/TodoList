package com.task.todolist.webcontroller;

import com.task.todolist.model.GroceryListDAO;
import org.apache.commons.dbcp2.BasicDataSource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/updatelistservlet")
public class UpdateItemServlet extends HttpServlet {
    private GroceryListDAO groceryListDAO;

    public void init() throws ServletException {
        ServletContext context = getServletContext();
        BasicDataSource connectionPool = (BasicDataSource) context.getAttribute("connectionPool");
        groceryListDAO = new GroceryListDAO(connectionPool);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        int id = Integer.parseInt(request.getParameter("id"));
        String status = request.getParameter("status");
        if(status.equals("done")){
            groceryListDAO.updateListItemStatus(id,status);
        }else{
            groceryListDAO.removeListItem(id);
        }
    }
}

