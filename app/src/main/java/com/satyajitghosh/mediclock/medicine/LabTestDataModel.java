package com.satyajitghosh.mediclock.model;

public class LabTestDataModel {
    private String id;
    private String testName;
    private String time;
    private String date;
    private boolean isEnabled;

    // Wajib: Konstruktor kosong untuk Firebase
    public LabTestDataModel() {
    }

    // Konstruktor dengan parameter (opsional)
    public LabTestDataModel(String id, String testName, String time, String date, boolean isEnabled) {
        this.id = id;
        this.testName = testName;
        this.time = time;
        this.date = date;
        this.isEnabled = isEnabled;
    }

    // Getter & Setter (wajib untuk Firebase)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
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

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}