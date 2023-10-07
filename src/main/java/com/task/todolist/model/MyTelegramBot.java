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
    private final TelegramBotsApi botsApi;
    private ServletContext servletContext;
    private volatile boolean isRunning = false;
    private Map<Long, InputItemConversation> inputItemConversationMap = new HashMap<>();
    private Map<Long, CreateEventConversation> createEventConversationMap = new HashMap<>();

    public MyTelegramBot(ServletContext servletContext) throws TelegramApiException {
        super("6576672471:AAGVuPWvyys3U7oqb7ILytkkeF9Nf1km5kw");
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
            handleCallbackQuery(update);
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update);
        }
    }

    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        if (callbackData.equals("Input Items")) {
            InputItemConversation conversation = new InputItemConversation(servletContext);
            inputItemConversationMap.put(chatId, conversation);
            sendResponse(chatId, conversation.handleInput(update.getCallbackQuery().getMessage()));
        } else if (callbackData.trim().equals("Display Items")) {
            sendTableResponse(new DisplayItemsConversation(servletContext).updateDisplay(chatId));
        } else if (callbackData.equals("Create Event")) {
            CreateEventConversation conversation = new CreateEventConversation();
            createEventConversationMap.put(chatId, conversation);
            sendResponse(chatId, "Please enter the event name:");
        } else if (callbackData.equals("Display Events")) {
            sendTableResponse(new DisplayEventsConversation().displayEvents(chatId));
        }
        sendMenu(chatId);
    }

    private void handleMessage(Update update) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        String text = message.getText();
        if (text.equals("/start")) {
            sendMenu(chatId);
        } else if (inputItemConversationMap.containsKey(chatId)) {
            handleInputItemConversation(update, chatId);
        } else if (createEventConversationMap.containsKey(chatId)) {
            handleCreateEventConversation(update, chatId);
        }
    }

    private void handleInputItemConversation(Update update, long chatId) {
        InputItemConversation conversation = inputItemConversationMap.get(chatId);
        String response = conversation.handleInput(update.getMessage());
        sendResponse(chatId, response);
        if (conversation.getState() == 1) {
            inputItemConversationMap.remove(chatId);
        }
    }

    private void handleCreateEventConversation(Update update, long chatId) {
        CreateEventConversation conversation = createEventConversationMap.get(chatId);
        try {
            String response = conversation.handleInput(update.getMessage());
            sendResponse(chatId, response);
            if (conversation.getState() == 1) {
                inputItemConversationMap.remove(chatId);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMenu(Long chatId) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(Menu.createMenu());
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Choose an option:");
        message.setReplyMarkup(markupInline);
        try {
            execute(message);
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
}
