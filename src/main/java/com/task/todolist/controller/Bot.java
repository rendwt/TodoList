package com.task.todolist.controller;


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
   public Map<Long, Map<BotState, String>> userResponses = new HashMap<>();
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
        //System.out.println(update.getMessage().getText());
        if (update.hasMessage()) {
            Message message = update.getMessage();

            // Extract the username
            String username = message.getFrom().getFirstName();
            // Extract the message text
            String messageText = message.getText();
            //user
            User u = update.getMessage().getFrom();
            Long who=u.getId();
            //System.out.println("Username: " + username);
            System.out.println("Message: " + messageText);


            //commands

            if (messageText.equals("/start")) {
                sendText(u.getId(), "Welcome! ");
                sendText(u.getId(), "Add Task /add or View Task  /view  ");
            }  if (messageText.equals("/view")) {
                //calendar details

                try {
                    GoogleCalendarEventDAO eventDAO = new GoogleCalendarEventDAO();
                    List<Event> events = eventDAO.getEvents("primary");
                    viewEvent(message.getChatId(), events);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                sendText(u.getId(), "Add Task /add or View Task  /view  ");
            }

            //ADD EVENTS


                BotState currentState = userStates.get(who);

                if (currentState == null || currentState == BotState.IDLE) {
                    // Handle general commands when not in a specific state
                    if (messageText.startsWith("/add")) {
                        startConversation(who);
                    }
//                    }  else {
//                        // Respond to unrecognized commands or provide a menu
//                        sendText(who, "I'm not sure what you want. Please use /start to begin.");
//                    }
                } else {
                    switch (currentState) {
                        case WAITING_FOR_EVENT_SUMMARY:
                            // Store the event summary, then ask for start date
//                            Message msg= update.getMessage();
                            summary = update.getMessage().getText();

                            userStates.put(who, BotState.WAITING_FOR_EVENT_DESCRIPTION);
                            sendText(who, "Please enter the description of event");
                            break;
                        case WAITING_FOR_EVENT_DESCRIPTION:
                            // Store the event summary, then ask for start date
//                            Message msg= update.getMessage();
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

//                            String eventSummary = userResponses.get(who).get(BotState.WAITING_FOR_EVENT_SUMMARY);
//                            String startDate = (userResponses.get(who).get(BotState.WAITING_FOR_START_DATE));
//                            String endDate = (userResponses.get(who).get(BotState.WAITING_FOR_END_DATE));


                            userStates.put(who, BotState.IDLE);
                            userResponses.remove(who);
                            try {
                                createEvent(summary, description, startDateTime, endDateTime);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                    }
                    //System.out.println("from bot " + startDateTime);
                }



                }

//

//
//                 if()
//                try {
//                    setEventDetails(u,messageText);
//                } catch (GeneralSecurityException e) {
//                    throw new RuntimeException(e);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
                //sendText(u.getId(), "Add Task /add or View Task  /view  ");

            }


//conversation
private void startConversation(Long chatId) {
    userStates.put(chatId, BotState.WAITING_FOR_EVENT_SUMMARY);
    userResponses.put(chatId, new HashMap<>());
    sendText(chatId, "Please enter the event summary:");
}


    public void userInput(Long who, String messageText, BotState currentState) {
        switch (currentState) {

            case  WAITING_FOR_EVENT_SUMMARY:
                // Store the event summary, then ask for start date

                userStates.put(who, BotState.WAITING_FOR_START_DATE);
                sendText(who, "Please enter the start date in YYYY-MM-DD format:");
                userResponses.put(who, new HashMap<>());
                break;
            case WAITING_FOR_START_DATE:
                // Store the start date, then ask for end date
                userStates.put(who, BotState.WAITING_FOR_END_DATE);
                sendText(who, "Please enter the end date in YYYY-MM-DD format:");

                break;
            case  WAITING_FOR_END_DATE:
                // Here, you have all the necessary details; you can add the event to the calendar
                String eventSummary = userResponses.get(who).get(BotState.WAITING_FOR_EVENT_SUMMARY);
                String startDate = userResponses.get(who).get(BotState.WAITING_FOR_START_DATE);
                String endDate = userResponses.get(who).get(BotState.WAITING_FOR_END_DATE);
               // addEventToCalendar(chatId, eventSummary, startDate, endDate);
                System.out.println(endDate);
                // Reset the user's state to idle
                userStates.put(who ,BotState.IDLE);
                userResponses.remove(who);
                break;

        }

    }







    public void sendText(Long who, String what) {
        SendMessage sm = new SendMessage().builder().chatId(who.toString()).text(what).build();
        //Message content

        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            e.printStackTrace();      //Any error will be printed here
        }
    }





    public void viewEvent(Long chatId, List<Event> events){
        StringBuilder messageText = new StringBuilder("Upcoming events:\n");
        for (Event event : events) {
            String summary = event.getSummary();
           // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            DateTime start = event.getStart().getDateTime();

            messageText.append(summary).append("/n");//.append(" (").append(start).append(")\n");
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




