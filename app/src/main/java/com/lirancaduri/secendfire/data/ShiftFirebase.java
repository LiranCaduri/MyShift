package com.lirancaduri.secendfire.data;


import java.io.Serializable;

public class ShiftFirebase extends Shift implements Serializable {
    private String uid;
    private String problem;

    public ShiftFirebase() {

    }

    public ShiftFirebase(Shift shift) {
        setId(shift.getId());
        setDate(shift.getDate());
        setStart(shift.getStart());
        setEnd(shift.getEnd());
        setSalary(shift.getSalary());
        setTip(shift.getTip());
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
