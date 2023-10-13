package com.task.todolist.telegramcontroller;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class CalendarEventMenu {
    public static List<List<InlineKeyboardButton>> createEventMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Edit event");
        button1.setCallbackData("Edit event");
        row.add(button1);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Delete event");
        button2.setCallbackData("Update event");
        row.add(button2);

        keyboard.add(row);
        return keyboard;
    }
}
