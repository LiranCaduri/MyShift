package com.lirancaduri.secendfire.data;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.sql.Time;


public class Shift implements Serializable , Comparable<Shift> {

    private String date;
    private long start, end;
    private int salary, tip, id;

    public Shift() {// empty constractor
    }

    public Shift(String date, long start, long end, int salary, int tip) {
        this(date, start, end, salary, tip, -1);
    }

    public Shift(String date, long start, long end, int salary, int tip, int id) {
        this.date = date;
        this.start = start;
        this.end = end;
        this.salary = salary;
        this.tip = tip;
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {

        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Shift : " +
                "\ndate = " + date +
                "\nstart = " + start +
                "\nend = " + end +
                "\nsalary = " + salary +
                "\ntip = " + tip;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof Shift) {
            Shift otherShift = ((Shift) obj);
            return otherShift.date.equals(this.date) && otherShift.start == this.start && otherShift.end == this.end;
        }
        return false;

    }

    @Override
    public int compareTo(@NonNull Shift o) {

        //הכרזה על משתנים
        int thisYears ,otherYears;
        int thisMonth ,otherMonth ;
        int thisDay ,otherDay;


        //פיצול לפי ימים חודשים שנים
        String[] thisSplit = this.date.split("/");
        thisDay = Integer.parseInt(thisSplit[0]);
        thisMonth = Integer.parseInt(thisSplit[1]);
        thisYears = Integer.parseInt(thisSplit[2]);

        String[] otherSplit = o.date.split("/");
        otherDay = Integer.parseInt(otherSplit[0]);
        otherMonth = Integer.parseInt(otherSplit[1]);
        otherYears = Integer.parseInt(otherSplit[2]);


        //השוואות
        if (thisYears > otherYears){
            return -1;
        }else if (thisYears < otherYears){
            return 1;
        }else if (thisMonth > otherMonth){
            return -1;
        }else if (thisMonth < otherMonth) {
            return 1;
        }else if (thisDay > otherDay) {
            return -1;
        }else if (thisDay <otherDay){
            return 1;
        }else{
            int compere = new Time(this.start).toString().compareTo(new Time(o.start).toString());
            if (compere == 1){
                compere = -1;
            }else if (compere == -1){
                compere = 1;
            }
            return compere;
        }

    }
}
