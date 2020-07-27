package com.example.weather;

class Weather {
    private String temp;
    private String time;
    private String date;
    private String humidity;
    private String weather;
    private String description;
    private String icon;


    public Weather(String temp, String time, String date, String humidity, String weather, String description, String icon) {
        this.date = date;
        this.temp = temp;
        this.time = time;
        this.humidity = humidity;
        this.weather = weather;
        this.description = description;
        this.icon = icon;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
