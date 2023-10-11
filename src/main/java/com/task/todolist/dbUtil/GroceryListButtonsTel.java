package com.task.todolist.dbUtil;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.List;
public class GroceryListButtonsTel {
    private static InlineKeyboardMarkup groceryListButtons;
    public static InlineKeyboardMarkup returnKeyBoardButtonsForEachItem(String itemId) {
        var markasdone = InlineKeyboardButton.builder()
                .text("Mark as done")
                .callbackData("markasdone_" + itemId)
                .build();

        var remove = InlineKeyboardButton.builder()
                .text("Remove")
                .callbackData("remove_" + itemId)
                .build();

        var edit = InlineKeyboardButton.builder()
                .text("Edit")
                .callbackData("edit_" + itemId)
                .build();

        groceryListButtons = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(markasdone))
                .keyboardRow(List.of(remove))
                .keyboardRow(List.of(edit))
                .build();

        return groceryListButtons;

    }
}
