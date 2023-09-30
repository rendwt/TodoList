package com.task.todolist.controller;

import com.google.api.services.calendar.model.Event;
import com.task.todolist.dao.GoogleCalendarEventDAO;
import com.task.todolist.model.GroceryList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/geteventservlet")
public class GetEventServlet extends HttpServlet {
    private GoogleCalendarEventDAO eventDAO;

    public void init() throws ServletException {
        eventDAO = new GoogleCalendarEventDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            List<Event> events = eventDAO.getEvents("primary");
            StringBuilder html = new StringBuilder();
            if(events.isEmpty()){
                html.append("<p>No events to show</p>");
            }else {
                html.append("<html><head><title>Calendar Events</title></head><body>");
                html.append("<h1>Calendar Events</h1>");
                html.append("<table border='1'>");
                html.append("<tr><th>Event Name</th><th>Event Summary</th><th>Start Date</th><th>End Date</th></tr>");

                for (Event event : events) {
                    html.append("<tr>");
                    html.append("<td>").append(event.getSummary()).append("</td>");
                    html.append("<td>").append(event.getDescription()).append("</td>");
                    html.append("<td>").append(event.getStart().getDateTime()).append("</td>");
                    html.append("<td>").append(event.getEnd().getDateTime()).append("</td>");
                    html.append("</tr>");
                }

                html.append("</table>");
                html.append("</body></html>");

                response.setContentType("text/html");
            }
                PrintWriter out = response.getWriter();
                out.println(html.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error retrieving events.");
        }
    }
}

