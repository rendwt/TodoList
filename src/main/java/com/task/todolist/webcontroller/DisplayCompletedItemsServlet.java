package com.task.todolist.webcontroller;

import com.task.todolist.model.CompletedGroceryListDAO;
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

@WebServlet("/displaycompletedlistservlet")
public class DisplayCompletedItemsServlet extends HttpServlet {
    private CompletedGroceryListDAO completedGroceryListDAO;

    public void init() throws ServletException {
        ServletContext context = getServletContext();
        BasicDataSource connectionPool = (BasicDataSource) context.getAttribute("connectionPool");
        completedGroceryListDAO = new CompletedGroceryListDAO(connectionPool);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        try {
            List<GroceryList> completedgroceryList = completedGroceryListDAO.getList();
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<h2>Completed List</h2>");
            out.println("<table border='1'>");
            if(completedgroceryList.isEmpty())
                out.println("<p>The completed list is empty</p>");
            else {
                out.println("<tr><th>Item no</th><th>Item Name</th><th>Qty</th><th>Unit</th><th>Status</th><th>Actions</th></tr>");
                int i = 1;
                for (GroceryList listitem : completedgroceryList) {
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
                    out.println("<td><button class='status-button1' data-id='" + listItemId + "' data-status='to be completed'>Mark undone</button>");
                    out.println("<button class='status-button1' data-id='" + listItemId + "' data-status='remove'>Remove</button>");

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
