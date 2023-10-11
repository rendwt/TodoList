package com.task.todolist.view;

import com.task.todolist.dbUtil.GroceryListButtonsTel;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
public class groceryListBot {
    public static class gLBot extends TelegramLongPollingBot {
        private enum BotState {
            IDLE,
            WAITING_FOR_ADD_LIST_ITEM,
            WAITING_FOR_DISPLAY_LIST_ITEM

        }
        private Map<Long, BotState> userStates = new HashMap<>();
        @Override
        public String getBotUsername() {
            return "TodolistBot";
        }

        @Override
        public String getBotToken() {
            return "6430243161:AAFUpKGpmmJTlFrQVOudMwQM_RksZZA8C1M";
        }

        @Override
        public void onUpdateReceived(Update update) {
            Long chatId = update.getMessage().getChatId();
            BotState userState = userStates.getOrDefault(chatId, BotState.IDLE);
            String userMessage = update.getMessage().getText();
            System.out.println(userMessage);
            handleUserInput(chatId, userState, userMessage);

            if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String callbackData = callbackQuery.getData();

                if (callbackData.startsWith("remove_")) {
                    String itemId = callbackData.substring("remove_".length());
                    System.out.println("Inside callbackdata");
                    handleRemoveAction(itemId);
                }
            }
        }
        private void handleUserInput(Long chatId, BotState userState, String userMessage) {
            switch (userState) {
                case IDLE:
                    handleIdleState(chatId, userMessage);
                    if(userMessage.contains("add")){
                        userStates.put(chatId, BotState.WAITING_FOR_ADD_LIST_ITEM);
                        sendMessage(chatId, "You chose to add items. Please enter the item name and quantity separated by space.");
                    }else if(userMessage.contains("display")){
                        userStates.put(chatId, BotState.WAITING_FOR_DISPLAY_LIST_ITEM);
                        sendMessage(chatId, "You chose to display items.");
                        handledisplaylistitemState(chatId,userMessage);
                    }
                    break;

                case WAITING_FOR_ADD_LIST_ITEM:
                    handleaddlistitemState(chatId, userMessage);
                    userStates.put(chatId, BotState.IDLE);
                    break;

                case WAITING_FOR_DISPLAY_LIST_ITEM:
                    handledisplaylistitemState(chatId,userMessage);
                    userStates.put(chatId, BotState.IDLE);
                    break;
            }
        }

        private void handleIdleState(Long chatId,String userMessage){
            boolean isFirstInteraction = !userStates.containsKey(chatId);

            if (isFirstInteraction) {
                sendMessage(chatId, "Welcome! You can add items by sending their names and quantities separated by space, e.g., 'item1 5'.");
                userStates.put(chatId, BotState.WAITING_FOR_ADD_LIST_ITEM);
            }
        }
        private void handleaddlistitemState(Long chatId,String userMessage){
            String[] parts = userMessage.split(" ");

            if (parts.length == 2) {
                String itemName = parts[0];
                String quantity = parts[1];
                String unit = "Kg";

            try{

                HttpClient httpClient = HttpClients.createDefault();

                HttpPost httpPost = new HttpPost("http://localhost:8080/TodoList/addlistitemservlettel");

                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("itemName", itemName));
                params.add(new BasicNameValuePair("quantity", quantity));
                params.add(new BasicNameValuePair("unit", unit));

                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = httpClient.execute(httpPost);

                if (response.getStatusLine().getStatusCode() == 200) {
                    sendMessage(chatId, "Item added: " + itemName + ", Quantity: " + quantity+ ", unit: "+ unit);
                } else {
                    sendMessage(chatId,"The message could not be sent due to bad request in AddListItemServlet");
                }

            }catch (IOException e) {
                e.printStackTrace();
                sendMessage(chatId, "An error occurred while sending the data to the server in AddListItemServlet.");
            }
            }else{
                sendMessage(chatId, "Please enter both item name and quantity separated by space.");
            }
        }

        private void handledisplaylistitemState(Long chatId, String userMessage) {
            InlineKeyboardMarkup groceryListButtons = null;
            try {
                HttpClient httpClient = HttpClients.createDefault();
                HttpGet httpGet = new HttpGet("http://localhost:8080/TodoList/displaylistservlettel");

                HttpResponse response = httpClient.execute(httpGet);

                if (response.getStatusLine().getStatusCode() == 200) {

                    String responseBody = EntityUtils.toString(response.getEntity());

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);

                    for (JsonNode item : jsonNode) {
                        StringBuilder formattedResponse = new StringBuilder("Grocerylist");
                        formattedResponse.append("Item ID: ").append(item.get("itemId").asText()).append("\n");
                        formattedResponse.append("Item Name: ").append(item.get("itemName").asText()).append("\n");
                        formattedResponse.append("Quantity: ").append(item.get("qty").asText()).append("\n");
                        formattedResponse.append("Unit: ").append(item.get("unit").asText()).append("\n");
                        formattedResponse.append("Status: ").append(item.get("status").asText()).append("\n\n");

                        groceryListButtons = GroceryListButtonsTel.returnKeyBoardButtonsForEachItem(item.get("itemId").asText());
                        sendListWithButtons(chatId, formattedResponse.toString(), groceryListButtons);
                    }


                    sendMessage(chatId, Integer.toString(response.getStatusLine().getStatusCode()));
                } else {
                    sendMessage(chatId, "The message could not be sent due to a bad request.");
                    sendMessage(chatId,Integer.toString(response.getStatusLine().getStatusCode()));
                }
            } catch (IOException e) {
                e.printStackTrace();
                sendMessage(chatId, "An error occurred while making the request to the servlet.");
            }
        }

        private void handleRemoveAction(String itemId){

        }

        private void sendMessage(Long who, String what) {

            SendMessage sm = SendMessage.builder()
                    .chatId(who.toString())
                    .text(what).build();
            try {
                execute(sm);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        private void sendListWithButtons(Long who, String what, InlineKeyboardMarkup kb){
            SendMessage sm = SendMessage.builder().chatId(who.toString())
                    .text(what)
                    .replyMarkup(kb).build();

            try {
                execute(sm);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
