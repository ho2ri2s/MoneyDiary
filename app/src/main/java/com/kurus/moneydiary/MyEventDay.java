package com.kurus.moneydiary;

import com.applandeo.materialcalendarview.EventDay;

import java.util.Calendar;

public class MyEventDay extends EventDay {

    private String itemType;
    private String itemName;
    private int price;

    public MyEventDay(Calendar date, int imageResource, String itemType, String itemName, int price){
        super(date, imageResource);
        this.itemType = itemType;
        this.itemName = itemName;
        this.price = price;
    }

    public String getItemType() {
        return itemType;
    }

    public String getItemName() {
        return itemName;
    }

    public int getPrice() {
        return price;
    }
}
