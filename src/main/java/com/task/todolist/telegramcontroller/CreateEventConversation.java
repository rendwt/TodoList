package com.task.todolist.telegramcontroller;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.task.todolist.model.GoogleCalendarService;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.time.format.DateTimeParseException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public class CreateEventConversation {
    private GoogleCalendarService eventDAO;
    private static final int STATE_INITIAL = 1;
    private static final int STATE_EVENT_SUMMARY = 2;
    private static final int STATE_EVENT_START_DATE= 3;
    private static final int STATE_EVENT_END_DATE = 4;
    private int state;
    private String eventSummary;
    private String eventDescription;
    private DateTime eventStart;
    private DateTime eventEnd;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public CreateEventConversation(Long chatId) {
        this.state = STATE_INITIAL;
        eventDAO = new GoogleCalendarService();
    }

    public int getState() {
        return state;
    }

    public String handleInput(Message message) throws IOException {
        String text = message.getText();
        switch (state) {
            case STATE_INITIAL:
                eventSummary = text;
                state = STATE_EVENT_SUMMARY;
                return "Please enter the event summary:";
            case STATE_EVENT_SUMMARY:
                eventDescription = text;
                state = STATE_EVENT_START_DATE;
                return "Please enter the event start date and time (yyyy-mm-dd'T'HH:mm):";
            case STATE_EVENT_START_DATE:
                if (!isValidDateTimeFormat(text)) {
                    return "Invalid date and time format. Please enter a valid date and time (e.g., yyyy-mm-dd'T'HH:mm):";
                }
                startDateTime = LocalDateTime.parse(text, formatter);
                eventStart = new DateTime(startDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
                state = STATE_EVENT_END_DATE;
                return "Please enter the event end date and time (yyyy-mm-dd'T'HH:mm):";

            case STATE_EVENT_END_DATE:
                if (!isValidDateTimeFormat(text)) {
                    return "Invalid date and time format. Please enter a valid date and time (e.g., yyyy-mm-dd'T'HH:mm):";
                }
                endDateTime = LocalDateTime.parse(text, formatter);
                eventEnd = new DateTime(endDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
                state = STATE_INITIAL;
                return createEventAndSendConfirmation();
            default:
                state = STATE_INITIAL;
                return "Invalid input. Please start over.";
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
    private String createEventAndSendConfirmation() throws IOException {
        Event event = new Event()
                .setSummary(eventSummary)
                .setDescription(eventDescription)
                .setStart(new EventDateTime().setDateTime(eventStart))
                .setEnd(new EventDateTime().setDateTime(eventEnd));
        eventDAO.createEvent(event, "primary");
        return "Event created:\nName: " + eventSummary + "\nDescription: " + eventDescription + "\nStart Date/Time: " + startDateTime + "\nEnd Date/Time: " + endDateTime;
    }
}
