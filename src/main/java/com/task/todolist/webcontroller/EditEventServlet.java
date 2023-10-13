package com.task.todolist.webcontroller;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.task.todolist.model.GoogleCalendarService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@WebServlet("/editeventservlet")
public class EditEventServlet extends HttpServlet {
    private GoogleCalendarService eventDAO;

    public void init() throws ServletException {
        eventDAO = new GoogleCalendarService();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String eventId = request.getParameter("eventId");
        String summary = request.getParameter("summary");
        String description = request.getParameter("description");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

        DateTime start = new DateTime(startDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
        DateTime end = new DateTime(endDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());

        try {
            Event eventToUpdate = eventDAO.getEvent("primary", eventId);

            if (eventToUpdate != null) {
                eventToUpdate.setSummary(summary);
                eventToUpdate.setDescription(description);
                eventToUpdate.setStart(new EventDateTime().setDateTime(start));
                eventToUpdate.setEnd(new EventDateTime().setDateTime(end));
                eventDAO.updateEvent(eventToUpdate, "primary");
                response.sendRedirect("calendar.jsp");
            } else {
                response.getWriter().write("Event not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error updating event.");
        }
    }
}

