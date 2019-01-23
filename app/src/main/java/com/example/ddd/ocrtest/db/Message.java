package com.example.ddd.ocrtest.db;

import org.litepal.crud.LitePalSupport;

public class Message extends LitePalSupport {

    private String Address;
    private int Person;
    private String body;
    private int Type;
    private boolean detectionResult;

    public Boolean getDetectionResult() {
        return detectionResult;
    }

    public void setDetectionResult(Boolean detectionResult) {
        this.detectionResult = detectionResult;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public int getPerson() {
        return Person;
    }

    public void setPerson(int person) {
        Person = person;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }
}
