package com.task.todolist.telegramcontroller;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.ArrayList;
import java.util.List;

public class EventMenu {
    public static List<List<InlineKeyboardButton>> createEventMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Edit item");
        button1.setCallbackData("Edit item");
        row.add(button1);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Update item");
        button2.setCallbackData("Update item");
        row.add(button2);

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Delete item");
        button3.setCallbackData("Delete item");
        row.add(button3);

        keyboard.add(row);
        return keyboard;
    }
}
