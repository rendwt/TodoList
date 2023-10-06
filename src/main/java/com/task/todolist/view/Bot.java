package com.task.todolist.view;


import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.task.todolist.dao.GoogleCalendarEventDAO;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.servlet.ServletException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
     public Map<Long, BotState> userStates = new HashMap<>();
    String summary ;
    String description ;
    String startDateTime ;
    String endDateTime ;

    private enum BotState {
        IDLE,
        WAITING_FOR_EVENT_SUMMARY,
        WAITING_FOR_EVENT_DESCRIPTION,
        WAITING_FOR_START_DATE,
        WAITING_FOR_END_DATE
    }

    @Override
    public String getBotUsername() {
        return "CourseTodoList_bot";
    }

    @Override
    public String getBotToken() {
        return "6576672471:AAGVuPWvyys3U7oqb7ILytkkeF9Nf1km5kw";
    }

    @Override
    public void onUpdateReceived(Update update) {
       
        if (update.hasMessage()) {
            Message message = update.getMessage();

          
            String username = message.getFrom().getFirstName();
          
            String messageText = message.getText();
           
            User u = update.getMessage().getFrom();
            Long who=u.getId();
          
            System.out.println("Message: " + messageText);



            if (messageText.equals("/start")) {
                sendText(u.getId(), "Welcome! ");
                sendText(u.getId(), "Add Task /add or View Task  /view  ");
            }  if (messageText.equals("/view")) {

                try {
                    GoogleCalendarEventDAO eventDAO = new GoogleCalendarEventDAO();
                    List<Event> events = eventDAO.getEvents("primary");
                    viewEvent(message.getChatId(), events);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                sendText(u.getId(), "Add Task /add or View Task  /view  ");
            }

        


                BotState currentState = userStates.get(who);

                if (currentState == null || currentState == BotState.IDLE) {
                    if (messageText.startsWith("/add")) {
                        startConversation(who);
                    }                  
                } else {
                    switch (currentState) {
                        case WAITING_FOR_EVENT_SUMMARY:
                            summary = update.getMessage().getText();
                            userStates.put(who, BotState.WAITING_FOR_EVENT_DESCRIPTION);
                            sendText(who, "Please enter the description of event");
                            break;
                        case WAITING_FOR_EVENT_DESCRIPTION:
                            description = update.getMessage().getText();
                            userStates.put(who, BotState.WAITING_FOR_START_DATE);
                            sendText(who, "Please enter the start date in YYYY-MM-DD format:");
                            break;
                        case WAITING_FOR_START_DATE:
                            startDateTime = update.getMessage().getText();
                            userStates.put(who, BotState.WAITING_FOR_END_DATE);
                            sendText(who, "Please enter the end date in YYYY-MM-DD format:");
                            break;
                        case WAITING_FOR_END_DATE:
                            endDateTime = update.getMessage().getText();
                            userStates.put(who, BotState.IDLE);
                          
                            try {
                                createEvent(summary, description, startDateTime, endDateTime);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        }
                   
                    }

                }

            }


private void startConversation(Long chatId) {
    userStates.put(chatId, BotState.WAITING_FOR_EVENT_SUMMARY);
    sendText(chatId, "Please enter the event summary:");
}

    public void sendText(Long who, String what) {
        SendMessage sm = new SendMessage().builder().chatId(who.toString()).text(what).build();
       

        try {
            execute(sm);                       
        } catch (TelegramApiException e) {
            e.printStackTrace();      
        }
    }

    public void viewEvent(Long chatId, List<Event> events){
        StringBuilder messageText = new StringBuilder("Upcoming events:\n");
        for (Event event : events) {
            String summary = event.getSummary();
          
            DateTime start = event.getStart().getDateTime();

            messageText.append(summary).append("/n");
        }
        sendText(chatId,messageText.toString());
    }

    public void createEvent(String summary, String description, String sdt, String edt) throws IOException {
        GoogleCalendarEventDAO eventDAO = new GoogleCalendarEventDAO();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        LocalDateTime startDateTime = LocalDateTime.parse(sdt, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(edt, formatter);

        DateTime start = new DateTime(startDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
        DateTime end = new DateTime(endDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
        Event event = new Event()
                .setSummary(summary)
                .setDescription(description)
                .setStart(new EventDateTime().setDateTime(start))
                .setEnd(new EventDateTime().setDateTime(end));

        Event createdEvent = eventDAO.createEvent(event, "primary");
    }
}
