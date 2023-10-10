package com.task.todolist.telegramcontroller;

import com.task.todolist.model.CompletedGroceryListDAO;
import com.task.todolist.model.GroceryList;
import com.task.todolist.model.GroceryListDAO;
import org.apache.commons.dbcp2.BasicDataSource;
import org.telegram.telegrambots.meta.api.objects.Message;
import javax.servlet.ServletContext;
import java.util.List;

public class UpdateItemConversation {
    private GroceryListDAO groceryListDAO;
    private CompletedGroceryListDAO completedGroceryListDAO;
    private int state = 1;
    private int listSelection;
    private int itemSelection;
    private String callBackData;

    public UpdateItemConversation(ServletContext servletContext){
        BasicDataSource connectionPool = (BasicDataSource) servletContext.getAttribute("connectionPool");
        groceryListDAO = new GroceryListDAO(connectionPool);
        completedGroceryListDAO = new CompletedGroceryListDAO(connectionPool);
    }

    public void setCallBackData(String callBackData){
        this.callBackData = callBackData;
    }

    public int getState(){
        return state;
    }

    public String handleInput(Message message){
        String text = message.getText();
        switch (state){
            case 1:
                state = 2;
                return "List no to select from ";
            case 2:
                try {
                    state = 3;
                    listSelection = Integer.parseInt(text);
                    System.out.println(listSelection);
                }catch (NumberFormatException e){
                    return "Invalid input. Please enter a valid quantity (integer):";
                }
                if (callBackData.equals("Update item")) {
                    if (listSelection == 1)
                        return "Enter item no to mark as done";
                    else
                        return "Enter item no to mark as to be completed";
                }else
                    return "Enter item no to delete from list";
            case 3:
                try {
                    state = 4;
                    itemSelection = Integer.parseInt(text);
                    System.out.println(itemSelection);
                    List<GroceryList> targetList = (listSelection == 1) ? DisplayItemsConversation.getGroceryList() : DisplayItemsConversation.getCompletedGroceryList();
                    updateItem(targetList);
                } catch (NumberFormatException e) {
                    return "Invalid input. Please enter a valid quantity (integer):";
                }
            case 4:
                state = 1;
                return "Updated list /menu to view main menu";
            default:
                return "Invalid input.";
        }
    }

    public void updateItem(List<GroceryList> targetList){
        for (GroceryList listitem : targetList) {
            if (listitem.getMenuid() == itemSelection) {
                if (callBackData.equals("Update item")) {
                    if (listSelection == 1) {
                        groceryListDAO.updateListItemStatus(listitem.getItemId(), "done");
                    } else if (listSelection == 2) {
                        completedGroceryListDAO.updateListItemStatus(listitem.getItemId(), "to be completed");
                    }
                } else if (callBackData.equals("Delete item")) {
                    if (listSelection == 1) {
                        groceryListDAO.removeListItem(listitem.getItemId());
                    } else if (listSelection == 2) {
                        completedGroceryListDAO.removeListItem(listitem.getItemId());
                    }
                }
            }
        }
    }
}
