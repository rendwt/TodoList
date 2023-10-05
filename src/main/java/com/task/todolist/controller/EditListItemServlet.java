package com.task.todolist.controller;

import com.task.todolist.model.GroceryListDAO;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/editlistitemservlet")
public class EditListItemServlet extends HttpServlet {
    private GroceryListDAO groceryListDAO;

    public void init() throws ServletException {
        ServletContext context = getServletContext();
        BasicDataSource connectionPool = (BasicDataSource) context.getAttribute("connectionPool");
        groceryListDAO = new GroceryListDAO(connectionPool);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int itemId = Integer.parseInt(request.getParameter("itemId"));
        String itemName = request.getParameter("itemName");
        int itemQty = Integer.parseInt(request.getParameter("itemQty"));
        String itemUnit = request.getParameter("itemUnit");
        groceryListDAO.editListItem(itemId,itemName,itemQty,itemUnit);
        response.sendRedirect("displaylist.jsp");
    }
}
