package com.task.todolist.view;

import com.google.api.services.calendar.model.Event;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.ParseException;


public class KngBot  extends TelegramLongPollingBot {
    public Map<Long, BotState> userStates = new HashMap<>();
    String summary;
    String startDateTime;
    String endDateTime;

    private enum BotState {
        IDLE,
        WAITING_FOR_EVENT_SUMMARY,
        WAITING_FOR_START_DATE,
        WAITING_FOR_END_DATE
    }

    @Override
    public String getBotUsername() {
        return "KNGjavabot";
    }

    @Override
    public String getBotToken() {
        return "6471445956:AAHLwJps-lua4NL04P_a6Qcv7l5Do4gR9wo";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String username = message.getFrom().getFirstName();
            String messageText = message.getText();

            User u = update.getMessage().getFrom();
            Long who = u.getId();

            System.out.println("Message: " + messageText);

            //commands
            if (messageText.equals("/start")) {
                welcomeMsg(u);
            } else if (messageText.equals("/grocery")) {
                sendText(u.getId(), "Add items /addgro \n View items  /viewgro  \n DeleteItems /delitems ");
            } else if (messageText.equals("/events")) {
                sendText(u.getId(), "Add Event /add \n View Event  /view  \n DeleteEvent /delevent ");
            } else if (messageText.equals("/view")) {
                    try {
                        viewEvent(message.getChatId());
                    } catch (IOException | GeneralSecurityException e) {
                        throw new RuntimeException(e);
                    }
                    sendText(u.getId(), "Add Event /add or View Event  /view   Delete /delevent ");
            } else if (messageText.startsWith("/delevent")) {
                try {
                    BotCalendar cal = new BotCalendar();
                    List<Event> events = cal.getEvents();
                    InlineKeyboardMarkup kb = createButton(events, who);
                } catch (GeneralSecurityException | IOException e) {
                    throw new RuntimeException(e);
                }
            }

            BotState currentState = userStates.get(who);
            if (messageText.startsWith("/add")) {
                 initiateBot(who,messageText,currentState);
                }
            else {
                eventInput(who, currentState, update);
            }
        }

        else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String eventId = callbackQuery.getData();
            try {
                deleteEvent(update);
                sendText(update.getMessage().getChatId(),"Add Event /add or View Event  /view   Delete /delevent ");
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void initiateBot(Long who,String messageText,BotState currentState){
        if (currentState == null || currentState == BotState.IDLE) {
            if (messageText.startsWith("/add")) {
                userStates.put(who, BotState.WAITING_FOR_EVENT_SUMMARY);
                sendText(who, "Please enter the event summary:");

            }
        }
    }

    public  void welcomeMsg(User u){
        sendText(u.getId(), "Welcome To KNG Service! ");
        sendText(u.getId(), "Manage Event  /events  OR  Manage Grocery  /grocery ");
    }
    public void sendText(Long who, String what) {
        SendMessage sm = new SendMessage().builder().chatId(who.toString()).text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void eventInput(Long who, BotState currentState, Update update) {
        switch (currentState) {
            case WAITING_FOR_EVENT_SUMMARY:
                summary = update.getMessage().getText();
                userStates.put(who, BotState.WAITING_FOR_START_DATE);
                sendText(who, "Please enter the start date in DD-MM-YYYY 00:00 format:");
                break;
            case WAITING_FOR_START_DATE:
                startDateTime = update.getMessage().getText();
                userStates.put(who, BotState.WAITING_FOR_END_DATE);
                sendText(who, "Please enter the start date in DD-MM-YYYY 00:00 format:");
                break;
            case WAITING_FOR_END_DATE:
                endDateTime = update.getMessage().getText();
                userStates.put(who, BotState.IDLE);
                BotCalendar cal;
                try {
                    cal = new BotCalendar();
                    if (cal.addEvent(summary, startDateTime, endDateTime)) {
                        sendText(who, "Event  added successfully");
                        sendText(update.getMessage().getChatId(), "Add Event /add \n View Event  /view  \n DeleteEvent /delevent ");
                    }
                } catch (GeneralSecurityException | IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }

    public void viewEvent(Long chatId) throws GeneralSecurityException, IOException {
        List<Event> events = new BotCalendar().getEvents();
        StringBuilder messageText = new StringBuilder("Upcoming events:\n");
        for (Event event : events) {
            String summary = event.getSummary();
            messageText.append(summary).append(System.getProperty("line.separator"));//.append(" (").append(start).append(")\n"
        }
        sendText(chatId, messageText.toString());
    }


    public void sendMenu(Long who, String txt, InlineKeyboardMarkup kb) {
        SendMessage sm = SendMessage.builder().chatId(who.toString()).parseMode("HTML").text(txt).build();
        sm.setReplyMarkup(kb);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public InlineKeyboardMarkup createButton(List<Event> events, Long who) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (Event event : events) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            String eventId = event.getId();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setCallbackData(eventId);
            inlineKeyboardButton.setText(event.getSummary());
            row.add(inlineKeyboardButton);
            keyboard.add(row);
        }
        keyboardMarkup.setKeyboard(keyboard);
        sendMenu(who, "Delete Event", keyboardMarkup);

        return keyboardMarkup;

    }

    public void deleteEvent(Update update) throws GeneralSecurityException, IOException {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String eventId = callbackQuery.getData();
            try {
                BotCalendar cal = new BotCalendar();
                if(cal.deleteCalEvent(eventId))
                {   sendText(update.getMessage().getChatId(), "Event Deleted");
                    sendText(update.getMessage().getChatId(), "Add Event /add \n View Event  /view  \n DeleteEvent /delevent ");
                }
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
