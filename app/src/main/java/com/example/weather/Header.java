package com.example.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Header {
    private String date;

    public Header(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
