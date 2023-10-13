package com.task.todolist.webcontroller;

import com.task.todolist.model.GoogleCalendarService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/updateeventservlet")
public class UpdateEventServlet extends HttpServlet {
    private GoogleCalendarService eventDAO;
    public void init() throws ServletException {
        eventDAO = new GoogleCalendarService();
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String eventId = request.getParameter("id");
        try {
            eventDAO.deleteEvent(eventId, "primary");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error deleting event.");
        }
    }
}
