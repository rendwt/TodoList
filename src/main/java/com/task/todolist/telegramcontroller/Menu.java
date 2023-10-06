package com.task.todolist.telegramcontroller;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    public static List<List<InlineKeyboardButton>> createMenu() {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton option1 = new InlineKeyboardButton();
        option1.setText("Input Items");
        option1.setCallbackData("Input Items");
        row1.add(option1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton option2 = new InlineKeyboardButton();
        option2.setText("Display Items");
        option2.setCallbackData("Display Items");
        row2.add(option2);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton option3 = new InlineKeyboardButton();
        option3.setText("Create Event");
        option3.setCallbackData("Create Event");
        row3.add(option3);

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        InlineKeyboardButton option4 = new InlineKeyboardButton();
        option4.setText("Display Events");
        option4.setCallbackData("Display Events");
        row4.add(option4);

        rowsInline.add(row1);
        rowsInline.add(row2);
        rowsInline.add(row3);
        rowsInline.add(row4);

        return rowsInline;
    }
}
