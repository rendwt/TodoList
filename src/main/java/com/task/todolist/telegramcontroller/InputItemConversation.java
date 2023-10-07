package com.task.todolist.telegramcontroller;

import com.task.todolist.model.GroceryListDAO;
import org.apache.commons.dbcp2.BasicDataSource;
import org.telegram.telegrambots.meta.api.objects.Message;
import javax.servlet.ServletContext;

public class InputItemConversation {
    private static final int STATE_INITIAL = 1;
    private static final int STATE_ITEM_NAME = 2;
    private static final int STATE_QUANTITY = 3;
    private static final int STATE_UNIT_OF_MEASUREMENT = 4;
    private int state;
    private String itemName;
    private int quantity;
    private GroceryListDAO groceryListDAO;

    public InputItemConversation(ServletContext servletContext) {
        this.state = STATE_INITIAL;
        BasicDataSource connectionPool = (BasicDataSource) servletContext.getAttribute("connectionPool");
        groceryListDAO = new GroceryListDAO(connectionPool);
    }

    public int getState() {
        return state;
    }

    public String handleInput(Message message) {
        String text = message.getText();
        switch (state) {
            case STATE_INITIAL:
                state = STATE_ITEM_NAME;
                return "You selected Option 1. Please enter the item name:";
            case STATE_ITEM_NAME:
                itemName = text;
                state = STATE_QUANTITY;
                return "Item name: " + itemName + "\nPlease enter the quantity (integer):";
            case STATE_QUANTITY:
                try {
                    quantity = Integer.parseInt(text);
                    state = STATE_UNIT_OF_MEASUREMENT;
                    return "Quantity: " + quantity + "\nPlease enter the unit of measurement:";
                } catch (NumberFormatException e) {
                    return "Invalid input. Please enter a valid quantity (integer):";
                }
            case STATE_UNIT_OF_MEASUREMENT:
                String unitOfMeasurement = text;
                state = STATE_INITIAL;
                groceryListDAO.addListItem(itemName, quantity, unitOfMeasurement, "to be completed");
                return "Added Item " + itemName + " " + quantity + " " + unitOfMeasurement + " to list";
            default:
                return "Invalid input.";
        }
    }
}
