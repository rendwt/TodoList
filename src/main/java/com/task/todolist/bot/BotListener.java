package com.task.todolist.bot;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class BotListener implements ServletContextListener {
    private BotThread botThread;
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            botThread = new BotThread(servletContextEvent.getServletContext());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        botThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        botThread.stopBot();
    }
}
