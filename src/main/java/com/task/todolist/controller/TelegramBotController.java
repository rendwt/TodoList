/*
package com.task.todolist.controller;
import com.task.todolist.dao.GroceryListDAO;
import com.task.todolist.dbUtil.DbConnection;
import com.task.todolist.model.GroceryList;
import com.task.todolist.view.TelegramView;
import org.apache.commons.dbcp2.BasicDataSource;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class TelegramBotController extends TelegramLongPollingBot {
        private GroceryListDAO groceryListDAO;

        public TelegramBotController() {
            groceryListDAO = new GroceryListDAO(DbConnection.getDataSource());
        }

        private void handleAddItemCommand(long chatId, String itemName, int qty, String unit, String status) {
            groceryListDAO.addListItem(chatId, itemName, qty, unit, status);
            SendMessage message = new SendMessage()
                    .setChatId(chatId)
                    .setText("Item added to list");

            try {
                execute(message);
            } catch (TelegramApiException e) {
            }
        }

        private void handleListItemsCommand(long chatId) {
            List<GroceryList> groceryItems = groceryListDAO.getList();
            SendMessage message = TelegramView.generateGroceryListResponse(chatId, groceryItems);
            try {
                execute(message);
            } catch (TelegramApiException e) {
            }
        }
    }
}
*/
