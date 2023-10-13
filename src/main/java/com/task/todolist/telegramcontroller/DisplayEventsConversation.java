package com.task.todolist.telegramcontroller;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.task.todolist.model.CalendarEvent;
import com.task.todolist.model.GoogleCalendarService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisplayEventsConversation {
    private GoogleCalendarService eventDAO;
    private static List<CalendarEvent> eventList = new ArrayList<>();
    public DisplayEventsConversation() {
        eventDAO = new GoogleCalendarService();
    }
    public static List<CalendarEvent> getEventList() {
        return eventList;
    }

    public SendMessage displayEvents(Long chatId) {
        SendMessage message = new SendMessage();
        try {
            List<Event> events = eventDAO.getAllEvents("primary");
            if (events.isEmpty()) {
                message.setText("No events to show");
            } else {
                StringBuilder messageText = new StringBuilder();
                messageText.append("Here are the calendar events:\n");
                messageText.append("No | Event Name | Event Summary | Start Date | End Date\n");
                int i = 1;
                for (Event event : events) {
                    CalendarEvent calendarEvent = new CalendarEvent();
                    calendarEvent.setMenuId(i);
                    calendarEvent.setEventId(event.getId());
                    calendarEvent.setSummary(event.getSummary());
                    calendarEvent.setDescription(event.getDescription());
                    calendarEvent.setStartDateTimeStr(String.valueOf(event.getStart()));
                    calendarEvent.setEndDateTimeStr(String.valueOf(event.getEnd()));
                    eventList.add(calendarEvent);
                    messageText.append(i++).append(" | ");
                    messageText.append(event.getSummary()).append(" | ");
                    messageText.append(event.getDescription()).append(" | ");
                    messageText.append(datetimeFormat(event.getStart())).append(" | ");
                    messageText.append(datetimeFormat(event.getEnd())).append("\n");
                }
                messageText.append("\n/menu to view main menu");
                message.setChatId(chatId.toString());
                message.setText(messageText.toString());
                return message;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public String datetimeFormat(EventDateTime event){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy  HH:mm");
        Date dateTime = new Date(event.getDateTime().getValue());
        return simpleDateFormat.format(dateTime);
    }
}
