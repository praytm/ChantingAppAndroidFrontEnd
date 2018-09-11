
package com.service.iscon.vcr.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class SessionModel implements Serializable {

    String date; //YYYY-MM-DDTHH:MM:SS
    String startTime;   //YYYY-MM-DDTHH:MM:SS
    String endTime; //YYYY-MM-DDTHH:MM:SS
    int beads;

    public SessionModel(String date, String startTime, String endTime, int beads) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.beads = beads;
    }

    public SessionModel(){
        this.date="";
        this.startTime="";
        this.endTime="";
        this.beads=0;
    }

    public SessionModel(String Date, int Beads) {
        this.date=Date;
        this.beads=Beads;
        this.startTime="";
        this.endTime="";
    }

    public String getDate() {
        return date;
    }

    public String getDateInIST(){
        return date;
    }

    public String getDateInISTFormat2(){
        SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy") ;
        SimpleDateFormat sdf2=new SimpleDateFormat("dd-MMM") ;
        String s="";
        Date d=null;
        try {
            d= sdf.parse(date);
            if(d!=null)
                s=sdf2.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getStartTimeInIST(){
        return date;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getEndTimeInIST(){
        return date;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getBeads() {
        return beads;
    }

    public void setBeads(int beads) {
        this.beads = beads;
    }
}