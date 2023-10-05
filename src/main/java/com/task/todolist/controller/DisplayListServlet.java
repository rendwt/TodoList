package com.task.todolist.controller;

import com.task.todolist.model.GroceryListDAO;
import com.task.todolist.model.GroceryList;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/displaylistservlet")
public class DisplayListServlet extends HttpServlet {
    private GroceryListDAO groceryListDAO;

    public void init() throws ServletException {
        ServletContext context = getServletContext();
        BasicDataSource connectionPool = (BasicDataSource) context.getAttribute("connectionPool");
        groceryListDAO = new GroceryListDAO(connectionPool);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            List<GroceryList> groceryList = groceryListDAO.getList();
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<table border='1'>");
            if(groceryList.isEmpty())
                out.println("<p>The list is empty</p>");
            else {
                out.println("<tr><th>Item no</th><th>Item Name</th><th>Qty</th><th>Unit</th><th>Status</th><th>Actions</th></tr>");
                int i = 1;
                for (GroceryList listitem : groceryList) {
                    int listItemId = listitem.getItemId();
                    String listItemName = listitem.getItemName();
                    int listitemQty = listitem.getQty();
                    String listitemUnit = listitem.getUnit();
                    String listitemStatus = listitem.getStatus();
                    out.println("<tr>");

                    out.println("<td>" + i++ + "</td>");
                    out.println("<td>" + listItemName + "</td>");
                    out.println("<td>" + listitemQty + "</td>");
                    out.println("<td>" + listitemUnit + "</td>");
                    out.println("<td>" + listitemStatus + "</td>");
                    out.println("<td><button class='status-button' data-id='" + listItemId + "' data-status='done'>Mark as done</button>");
                    out.println("<button class='status-button' data-id='" + listItemId + "' data-status='remove'>Remove</button>");
                    out.println("<button class='edit-button' data-id='" + listItemId + "'>Edit</button></td>");

                    out.println("</tr>");

                    out.println("<tr class='edit-form' style='display: none;'>");
                    out.println("<td colspan='4'>");
                    out.println("<form method='post' action='editlistitemservlet'>");
                    out.println("<input type='hidden' name='itemId' value='" + listItemId + "'>");
                    out.println("Name: <input type='text' name='itemName' value='" + listItemName + "'><br>");
                    out.println("Qty: <input type='text' name='itemQty' value='" + listitemQty + "'><br>");
                    out.println("Unit: <input type='text' name='itemUnit' value='" + listitemUnit + "'><br>");
                    out.println("<input type='submit' value='Save'>");
                    out.println("</form>");
                    out.println("</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
                out.println("</form>");
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}