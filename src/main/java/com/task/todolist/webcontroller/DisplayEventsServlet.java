package com.task.todolist.webcontroller;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.task.todolist.model.GoogleCalendarService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/geteventservlet")
public class DisplayEventsServlet extends HttpServlet {
    private GoogleCalendarService eventDAO;

    public void init() throws ServletException {
        eventDAO = new GoogleCalendarService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Event> events = eventDAO.getAllEvents("primary");
            StringBuilder html = new StringBuilder();
            if(events.isEmpty()){
                html.append("<p>No events to show</p>");
            }else {
                html.append("<html><head><title>Calendar Events</title></head><body>");
                html.append("<table border='1'>");
                html.append("<tr><th>Event Name</th><th>Event Summary</th><th>Start Date</th><th>End Date</th><th>Actions</th></tr>");

                for (Event event : events) {
                    html.append("<tr>");
                    html.append("<td>").append(event.getSummary()).append("</td>");
                    html.append("<td>").append(event.getDescription()).append("</td>");
                    html.append("<td>").append(datetimeFormat(event.getStart())).append("</td>");
                    html.append("<td>").append(datetimeFormat(event.getEnd())).append("</td>");
                    html.append("<td><button class='edit-button'>Edit</button>");
                    html.append("<button class='remove-button' data-id='" + event.getId() + "'>Remove</button></td>");
                    html.append("</tr>");

                    html.append("<tr class='edit-form' style='display: none;'>");
                    html.append("<td colspan='4'>");
                    html.append("<form method='post' action='editeventservlet'>");
                    html.append("<input type='hidden' name='eventId' value='" + event.getId() + "'>");
                    html.append("Summary: <input type='text' name='summary' value='" + event.getSummary() + "'><br>");
                    html.append("Description: <input type='text' name='description' value='" + event.getDescription() + "'><br>");
                    html.append("Start Date and Time: <input type='datetime-local' name='startDate'><br>");
                    html.append("End Date and Time: <input type='datetime-local' name='endDate'><br>");
                    html.append("<input type='submit' value='Save'>");
                    html.append("</form>");
                    html.append("</td>");
                    html.append("</tr>");
                }

                html.append("</table>");
                html.append("</body></html>");

                response.setContentType("text/html");
            }
                PrintWriter out = response.getWriter();
                out.println(html);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error retrieving events.");
        }
    }

    public String datetimeFormat(EventDateTime event){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy  HH:mm");
        Date dateTime = new Date(event.getDateTime().getValue());
        return simpleDateFormat.format(dateTime);
    }
}

