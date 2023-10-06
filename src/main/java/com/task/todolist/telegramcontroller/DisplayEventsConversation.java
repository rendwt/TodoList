package com.task.todolist.telegramcontroller;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.task.todolist.model.GoogleCalendarService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DisplayEventsConversation {
    private GoogleCalendarService eventDAO;

    public DisplayEventsConversation() {
        eventDAO = new GoogleCalendarService();
    }

    public SendMessage displayEvents(Long chatId) {
        SendMessage message = new SendMessage();
        try {
            List<Event> events = eventDAO.getEvents("primary");
            if (events.isEmpty()) {
                message.setText("No events to show");
            } else {
                StringBuilder messageText = new StringBuilder();
                messageText.append("Here are the calendar events:\n\n");
                messageText.append("Event Name | Event Summary | Start Date | End Date\n");

                for (Event event : events) {
                    messageText.append(event.getSummary()).append(" | ");
                    messageText.append(event.getDescription()).append(" | ");
                    messageText.append(datetimeFormat(event.getStart())).append(" | ");
                    messageText.append(datetimeFormat(event.getEnd())).append("\n");
                }
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
