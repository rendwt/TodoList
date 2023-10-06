package com.task.todolist.bot;

import com.task.todolist.model.MyTelegramBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import javax.servlet.ServletContext;

public class BotThread extends Thread {
    private MyTelegramBot bot;
    private ServletContext servletContext;

    public BotThread(ServletContext servletContext) throws TelegramApiException {
        System.out.println("initbot");
        bot = new MyTelegramBot(servletContext);
        this.servletContext = servletContext;

    }

    @Override
    public void run() {
        try {
            bot.runBot();
            System.out.println("BotThread: Started");
            while (bot.isRunning()) {
                Thread.sleep(1000);
            }

            System.out.println("BotThread: Stopped");
        } catch (TelegramApiException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopBot() {
        bot.stopBot();
    }
}
