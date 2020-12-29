package com.survey.hujuhj.SendNotificationPack;

import android.location.LocationListener;

public class Data {
    private String Title;
    private String Message;
    private String Location;

    public Data(String title, String message, String location) {
        Title = title;
        Message = message;
        Location = location;
    }

    public Data() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }
}
