package com.task.todolist.model;

import com.task.todolist.telegramcontroller.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyTelegramBot extends TelegramLongPollingBot {
    private TelegramBotsApi botsApi;
    private ServletContext servletContext;
    private volatile boolean isRunning = false;
    private Map<Long, InputItemConversation> inputItemConversationMap = new HashMap<>();
    private Map<Long, CreateEventConversation> createEventConversationMap = new HashMap<>();

    public MyTelegramBot(ServletContext servletContext) throws TelegramApiException {
        this.botsApi = new TelegramBotsApi(DefaultBotSession.class);
        this.servletContext = servletContext;
    }

    public void runBot() throws TelegramApiException {
        isRunning = true;
        botsApi.registerBot(this);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stopBot() {
        isRunning = false;
        System.out.println("stopped bot");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (callbackData.equals("Input Items")) {
                InputItemConversation conversation = new InputItemConversation(servletContext);
                inputItemConversationMap.put(chatId, conversation);
                sendResponse(chatId, conversation.handleInput(update.getCallbackQuery().getMessage()));
            }else if (callbackData.trim().equals("Display Items")) {
                DisplayItemsConversation displayItemsConversation = new DisplayItemsConversation(servletContext);
                sendTableResponse(displayItemsConversation.updateDisplay(chatId));
            } else if (callbackData.equals("Create Event")) {
                CreateEventConversation conversation = new CreateEventConversation(chatId);
                createEventConversationMap.put(chatId, conversation);
                sendResponse(chatId, "Please enter the event name:");
            } else if (callbackData.equals("Display Events")) {
                DisplayEventsConversation displayEventsConversation = new DisplayEventsConversation();
                sendTableResponse(displayEventsConversation.displayEvents(chatId));
            }
        } else {
            if (inputItemConversationMap.containsKey(update.getMessage().getChatId())) {
                InputItemConversation conversation = inputItemConversationMap.get(update.getMessage().getChatId());
                String response = conversation.handleInput(update.getMessage());
                sendResponse(update.getMessage().getChatId(), response);
                if (conversation.getState() == 1) {
                    inputItemConversationMap.remove(update.getMessage().getChatId());
                }
            }else if(createEventConversationMap.containsKey(update.getMessage().getChatId())){
                CreateEventConversation conversation = createEventConversationMap.get(update.getMessage().getChatId());
                try {
                    String response = conversation.handleInput(update.getMessage());
                    sendResponse(update.getMessage().getChatId(), response);
                    if (conversation.getState() == 1) {
                        inputItemConversationMap.remove(update.getMessage().getChatId());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String text = message.getText();
            if (text.equals("/start")) {
                sendMenu(update.getMessage().getChatId());
            }
        }
    }

    private void sendMenu(Long chatId) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(Menu.createMenu());
        SendMessage message = new SendMessage().builder()
        .chatId(chatId.toString())
        .text("Choose an option:")
        .replyMarkup(markupInline)
                .build();
        try {
            execute(message);
            System.out.println(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendResponse(Long chatId, String response) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(response)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendTableResponse(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "CourseTodoList_bot";
    }

    @Override
    public String getBotToken() {
        return "6576672471:AAGVuPWvyys3U7oqb7ILytkkeF9Nf1km5kw";
    }
}
