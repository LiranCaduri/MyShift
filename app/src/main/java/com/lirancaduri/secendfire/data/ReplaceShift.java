package com.lirancaduri.secendfire.data;


import java.io.Serializable;

public class ReplaceShift implements Serializable {

    private String uidUser;
    private int idShift;
    private long startTime;
    private String date;

    public ReplaceShift(){}

    public ReplaceShift(String uidUser, int idShift, long startTime, String date) {
        this.uidUser = uidUser;
        this.idShift = idShift;
        this.startTime = startTime;
        this.date = date;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }

    public int getIdShift() {
        return idShift;
    }

    public void setIdShift(int idShift) {
        this.idShift = idShift;
    }
}
