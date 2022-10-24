package com.producersample.demo.dto;

public class Message {
    private String message;
    private int times;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getTimes() {
        return times;
    }
}
