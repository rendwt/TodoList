package com.task.todolist.telegramcontroller;

import com.task.todolist.model.CalendarEvent;
import com.task.todolist.model.GoogleCalendarService;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.io.IOException;
import java.util.List;

public class UpdateEventConversation {
    private GoogleCalendarService eventDAO;
    private int state = 1;
    private int eventSelection;
    public UpdateEventConversation(){
        eventDAO = new GoogleCalendarService();
    }
    public int getState(){
        return state;
    }
    public String handleInput(Message message) throws IOException {
        String text = message.getText();
        switch (state){
            case 1:
                state = 2;
                return "Enter event no to select ";
            case 2:
                try {
                    state = 3;
                    eventSelection = Integer.parseInt(text);
                }catch (NumberFormatException e){
                    return "Invalid input. Please enter a valid quantity (integer):";
                }
            case 3:
                state = 1;
                List<CalendarEvent> eventList = DisplayEventsConversation.getEventList();
                for(CalendarEvent event : eventList){
                    if(eventSelection == event.getMenuId())
                        eventDAO.deleteEvent(event.getEventId(), "primary");
                }
                return "Event no :" + eventSelection + " deleted";
            default:
                return "Invalid input.";
        }
    }

}
