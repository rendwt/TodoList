package com.task.todolist.telegramcontroller;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.task.todolist.model.CalendarEvent;
import com.task.todolist.model.GoogleCalendarService;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EditEventConversation {
    private GoogleCalendarService eventDAO;
    private int state = 1;
    private int eventSelection;
    private String eventSummary;
    private String eventDescription;
    private DateTime eventStart;
    private DateTime eventEnd;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    public EditEventConversation(){
        eventDAO = new GoogleCalendarService();
    }
    public int getState(){
        return state;
    }
    public String handleInput(Message message) throws Exception {
        String text = message.getText();
        switch (state){
            case 1:
                state = 2;
                return "Enter event no to select ";
            case 2:
                try {
                    state = 3;
                    eventSelection = Integer.parseInt(text);
                    return "Enter event name ";
                }catch (NumberFormatException e){
                    return "Invalid input. Please enter a valid quantity (integer):";
                }
            case 3:
                state = 4;
                eventSummary = text;
                return "Enter event description ";
            case 4:
                state = 5;
                eventDescription = text;
                return "Please enter the event start date and time (yyyy-mm-dd'T'HH:mm):";
            case 5:
                if (!isValidDateTimeFormat(text)) {
                    return "Invalid date and time format. Please enter a valid date and time (e.g., yyyy-mm-dd'T'HH:mm):";
                }
                startDateTime = LocalDateTime.parse(text, formatter);
                eventStart = new DateTime(startDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
                state = 6;
                return "Please enter the event start date and time (yyyy-mm-dd'T'HH:mm):";
            case 6:
                if (!isValidDateTimeFormat(text)) {
                    return "Invalid date and time format. Please enter a valid date and time (e.g., yyyy-mm-dd'T'HH:mm):";
                }
                endDateTime = LocalDateTime.parse(text, formatter);
                eventEnd = new DateTime(endDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
                state = 7;
                return "Please enter the event end date and time (yyyy-mm-dd'T'HH:mm):";
            case 7:
                state = 1;
                List<CalendarEvent> eventList = DisplayEventsConversation.getEventList();
                for(CalendarEvent event : eventList){
                    if(eventSelection == event.getMenuId()){
                        Event eventToUpdate = eventDAO.getEvent("primary", event.getEventId());
                        if (eventToUpdate != null) {
                            eventToUpdate.setSummary(eventSummary);
                            eventToUpdate.setDescription(eventDescription);
                            eventToUpdate.setStart(new EventDateTime().setDateTime(eventStart));
                            eventToUpdate.setEnd(new EventDateTime().setDateTime(eventEnd));
                            eventDAO.updateEvent(eventToUpdate, "primary");
                        }
                    }
                }
                return "Event edited with given input ";
            default:
                return "Invalid input.";
        }
    }
    private boolean isValidDateTimeFormat(String input) {
        try {
            LocalDateTime.parse(input, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
