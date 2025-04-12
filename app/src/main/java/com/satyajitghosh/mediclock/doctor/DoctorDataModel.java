package com.satyajitghosh.mediclock.doctor;

public class DoctorDataModel {
    String name;
    String reason;
    int date;
    int month;
    int year;
    int notificationId;
    String key;

    public DoctorDataModel(String name, String reason, int date, int month, int year, int notificationId) {
        this.name = name;
        this.reason = reason;
        this.date = date;
        this.month = month;
        this.year = year;
        this.notificationId = notificationId;
    }

    public DoctorDataModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}
