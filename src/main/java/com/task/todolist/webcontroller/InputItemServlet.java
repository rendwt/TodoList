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
        out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\"css/styles.css\"></head>");
        out.println("<div class=\"inter\">");
        out.println("<h1>Item added to list</h1>");
        out.println("<a href=\"inputitem.jsp\">Add another item</a>");
        out.println("<a href=\"displaylist.jsp\">Display list</a>");
        out.println("</div>");
        out.close();

    }
}
