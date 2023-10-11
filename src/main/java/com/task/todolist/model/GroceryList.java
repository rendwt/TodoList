package com.task.todolist.model;

public class GroceryList {
    private int menuid;
    private int itemId;
    private String itemName;
    private int qty;
    private String unit;
    private String status;

    public int getMenuid() {
        return menuid;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQty() {
        return qty;
    }

    public String getUnit() {
        return unit;
    }

    public String getStatus() {
        return status;
    }

    public void setMenuid(int menuid) {
        this.menuid = menuid;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
