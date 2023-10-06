package com.task.todolist.telegramcontroller;

import com.task.todolist.model.GroceryList;
import com.task.todolist.model.GroceryListDAO;
import org.apache.commons.dbcp2.BasicDataSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import javax.servlet.ServletContext;
import java.util.List;

public class DisplayItemsConversation {
    private GroceryListDAO groceryListDAO;

    public DisplayItemsConversation(ServletContext servletContext) {
        BasicDataSource connectionPool = (BasicDataSource) servletContext.getAttribute("connectionPool");
        groceryListDAO = new GroceryListDAO(connectionPool);
    }

    public SendMessage updateDisplay(Long chatId) {
        List<GroceryList> groceryList = groceryListDAO.getList();
        int i = 1;
        StringBuilder messageText = new StringBuilder();
        messageText.append("Grocery List Items:\n");
        messageText.append("No | Item Name | Quantity | Unit | Status\n");

        for (GroceryList listItem : groceryList) {
            String itemName = listItem.getItemName();
            int quantity = listItem.getQty();
            String unit = listItem.getUnit();
            String status = listItem.getStatus();
            messageText.append(i++).append(" | ").append(itemName).append(" | ").append(quantity).append(" | ").append(unit).append(" | ").append(status).append("\n");
        }
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageText.toString());

        return message;
    }
}


