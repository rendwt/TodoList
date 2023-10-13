package com.task.todolist.telegramcontroller;

import com.task.todolist.model.CompletedGroceryListDAO;
import com.task.todolist.model.GroceryList;
import com.task.todolist.model.GroceryListDAO;
import org.apache.commons.dbcp2.BasicDataSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import javax.servlet.ServletContext;
import java.util.List;

public class DisplayItemsConversation {
    private static List<GroceryList> groceryList;
    private static List<GroceryList> completedGroceryList;

    public DisplayItemsConversation(ServletContext servletContext) {
        BasicDataSource connectionPool = (BasicDataSource) servletContext.getAttribute("connectionPool");
        GroceryListDAO groceryListDAO = new GroceryListDAO(connectionPool);
        CompletedGroceryListDAO completedGroceryListDAO = new CompletedGroceryListDAO(connectionPool);
        groceryList = groceryListDAO.getList();
        completedGroceryList = completedGroceryListDAO.getList();
    }

    public static List<GroceryList> getGroceryList() {
        return groceryList;
    }

    public static List<GroceryList> getCompletedGroceryList() {
        return completedGroceryList;
    }

    public SendMessage updateDisplay(Long chatId) {
        StringBuilder messageText = new StringBuilder();
        messageText.append("1.Grocery List Items:");
        messageText.append("\nNo | Item Name | Quantity | Unit | Status\n");
        messageText = CreateMessage(groceryList,messageText);
        messageText.append("\n2.Completed Grocery List Items:");
        messageText.append("\nNo | Item Name | Quantity | Unit | Status\n");
        messageText = CreateMessage(completedGroceryList,messageText);
        messageText.append("\n/menu to view main menu");
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageText.toString());
        return message;
    }

    public StringBuilder CreateMessage(List<GroceryList> list, StringBuilder messageText){
        int i = 1;
        for (GroceryList listItem : list) {
            listItem.setMenuid(i);
            String itemName = listItem.getItemName();
            int quantity = listItem.getQty();
            String unit = listItem.getUnit();
            String status = listItem.getStatus();
            messageText.append(i++).append(" | ").append(itemName).append(" | ").append(quantity).append(" | ").append(unit).append(" | ").append(status).append("\n");
        }
        return messageText;
    }
}


