package com.task.todolist.dbUtil;
import com.task.todolist.view.groceryListBot.gLBot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class RegisterBot {
        public static void main( String[] args )
        {
            try{
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new gLBot());
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }


