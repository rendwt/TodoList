package com.task.todolist.webcontroller;

import com.task.todolist.model.GroceryListDAO;
import org.apache.commons.dbcp2.BasicDataSource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/addlistitemservlet")
public class InputItemServlet extends HttpServlet{
    private GroceryListDAO groceryListDAO;

    public void init() throws ServletException {
        ServletContext context = getServletContext();
        BasicDataSource connectionPool = (BasicDataSource) context.getAttribute("connectionPool");
        groceryListDAO = new GroceryListDAO(connectionPool);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String itemName = request.getParameter("itemName");
        int qty = Integer.parseInt(request.getParameter("quantity"));
        String unit = request.getParameter("unit");
        String status = "to be completed";
        groceryListDAO.addListItem(itemName, qty, unit, status);
        PrintWriter out = response.getWriter();
        response.sendRedirect("grocerylist.jsp");
        out.close();

    }
}
