package com.task.todolist.model;

public class CalendarEvent {
    int menuId;
    String eventId;
    String summary;
    String description;
    String startDateTimeStr;
    String endDateTimeStr;

    public int getMenuId() {
        return menuId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDateTimeStr(String startDateTimeStr) {
        this.startDateTimeStr = startDateTimeStr;
    }

    public void setEndDateTimeStr(String endDateTimeStr) {
        this.endDateTimeStr = endDateTimeStr;
    }
}
