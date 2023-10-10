package com.task.todolist.telegramcontroller;

import com.task.todolist.model.CompletedGroceryListDAO;
import com.task.todolist.model.GroceryList;
import com.task.todolist.model.GroceryListDAO;
import org.apache.commons.dbcp2.BasicDataSource;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.servlet.ServletContext;
import java.util.List;

public class EditItemConversation {
    private GroceryListDAO groceryListDAO;
    private CompletedGroceryListDAO completedGroceryListDAO;
    private int state = 1;
    private int itemSelection;
    private String itemName;
    private int quantity;
    public EditItemConversation(ServletContext servletContext){
        BasicDataSource connectionPool = (BasicDataSource) servletContext.getAttribute("connectionPool");
        groceryListDAO = new GroceryListDAO(connectionPool);
        completedGroceryListDAO = new CompletedGroceryListDAO(connectionPool);
    }
    public int getState(){
        return state;
    }

    public String handleInput(Message message){
        String text = message.getText();
        switch (state){
            case 1:
                state = 2;
                return "Enter item no to edit";
            case 2:
                try {
                    state = 3;
                    itemSelection = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    return "Invalid input. Please enter a valid quantity (integer):";
                }
            case 3:
                state = 4;
                return "Please Enter the item name:";
            case 4:
                itemName = text;
                state = 5;
                return "Item name: " + itemName + "\nPlease enter the quantity (integer):";
            case 5:
                try {
                    quantity = Integer.parseInt(text);
                    state = 6;
                    return "Quantity: " + quantity + "\nPlease enter the unit of measurement:";
                } catch (NumberFormatException e) {
                    return "Invalid input. Please enter a valid quantity (integer):";
                }
            case 6:
                state = 7;
                List<GroceryList> groceryList = DisplayItemsConversation.getGroceryList();
                for (GroceryList listitem : groceryList){
                    if (listitem.getMenuid() == itemSelection)
                        groceryListDAO.editListItem(listitem.getItemId(), itemName, quantity, text);
                }
                return "Added Item " + itemName + " " + quantity + " " + text + " to list /menu to view menu";
            case 7:
                state = 1;
                return "Updated list /menu to view main menu";
            default:
                return "Invalid input.";
        }
    }
}
