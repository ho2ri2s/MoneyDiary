package com.kurus.moneydiary;

import android.os.Parcel;
import android.os.Parcelable;

import com.applandeo.materialcalendarview.EventDay;

import java.util.Calendar;

public class MyEventDay extends EventDay implements Parcelable {

    private String updateDate;
    private int imageResource;
    private String itemType;
    private String itemName;
    private int price;
    private Calendar calendar;

    public MyEventDay(Calendar calendar, String updateDate, int imageResource, String itemType, String itemName, int price){
        super(calendar, imageResource);
        this.calendar = calendar;
        this.updateDate = updateDate;
        this.imageResource = imageResource;
        this.itemType = itemType;
        this.itemName = itemName;
        this.price = price;
    }

    protected MyEventDay(Parcel in) {
        super((Calendar)in.readSerializable(), in.readInt());
        updateDate = in.readString();
        imageResource = in.readInt();
        itemType = in.readString();
        itemName = in.readString();
        price = in.readInt();
    }

    public static final Creator<MyEventDay> CREATOR = new Creator<MyEventDay>() {
        @Override
        public MyEventDay createFromParcel(Parcel in) {
            return new MyEventDay(in);
        }

        @Override
        public MyEventDay[] newArray(int size) {
            return new MyEventDay[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(updateDate);
        dest.writeInt(imageResource);
        dest.writeString(itemType);
        dest.writeString(itemName);
        dest.writeInt(price);
        dest.writeSerializable(calendar);
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

    public String getUpdateDate() {
        return updateDate;
    }

    @Override
    public int getImageResource() {
        return imageResource;
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
