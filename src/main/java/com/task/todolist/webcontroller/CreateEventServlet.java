package com.task.todolist.webcontroller;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
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
import java.util.Arrays;

@WebServlet("/createeventservlet")
public class CreateEventServlet extends HttpServlet {
    private GoogleCalendarService eventDAO;

    public void init() throws ServletException {
        eventDAO = new GoogleCalendarService();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String summary = request.getParameter("summary");
            String description = request.getParameter("description");
            String startDateTimeStr = request.getParameter("startDateTime");
            String endDateTimeStr = request.getParameter("endDateTime");
            String participants = request.getParameter("participants");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

            LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeStr, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeStr, formatter);

            DateTime start = new DateTime(startDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
            DateTime end = new DateTime(endDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
            Event event = new Event()
                    .setSummary(summary)
                    .setDescription(description)
                    .setStart(new EventDateTime().setDateTime(start))
                    .setEnd(new EventDateTime().setDateTime(end));

            String[] participantEmails = participants.split(",");
            EventAttendee[] attendees = new EventAttendee[participantEmails.length];
            for (int i = 0; i < participantEmails.length; i++) {
                attendees[i] = new EventAttendee().setEmail(participantEmails[i].trim());
            }
            event.setAttendees(Arrays.asList(attendees));
            eventDAO.createEvent(event, "primary");
            response.sendRedirect("Gevent.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error creating the event.");
        }
    }
}