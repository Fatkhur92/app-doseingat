package com.satyajitghosh.mediclock;

public class PostTestData {
    public String jawaban1, jawaban2, jawaban3, jawaban4, jawaban5, jawaban6, jawaban7, jawaban8;
    public long timestamp;

    // Default constructor required for Firebase
    public PostTestData() {
    }

    public PostTestData(String jawaban1, String jawaban2, String jawaban3, 
                       String jawaban4, String jawaban5, String jawaban6,
                       String jawaban7, String jawaban8, long timestamp) {
        this.jawaban1 = jawaban1;
        this.jawaban2 = jawaban2;
        this.jawaban3 = jawaban3;
        this.jawaban4 = jawaban4;
        this.jawaban5 = jawaban5;
        this.jawaban6 = jawaban6;
        this.jawaban7 = jawaban7;
        this.jawaban8 = jawaban8;
        this.timestamp = timestamp;
    }
}