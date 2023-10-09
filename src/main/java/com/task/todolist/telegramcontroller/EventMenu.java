package com.task.todolist.telegramcontroller;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class EventMenu {

    public static List<List<InlineKeyboardButton>> createEventMenu() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("edit item");
        button1.setCallbackData("");
        row.add(button1);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("update item");
        button2.setCallbackData("");
        row.add(button2);

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("delete item");
        button3.setCallbackData("");
        row.add(button3);

        keyboard.add(row);

        return keyboard;
    }
}
