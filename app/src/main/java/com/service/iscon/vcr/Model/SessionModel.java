
package com.service.iscon.vcr.Model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd") ;
        SimpleDateFormat sdf2=new SimpleDateFormat("dd MMM yyyy") ;
        String s="";
        Date d=null;
        try {
            d= sdf.parse(date);
            if(d!=null)
                s=sdf2.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }

    public String getDisplayDate(){

        String[] a=(date.trim().split("/"));
                if(isToday(a[2])){
                    return "Today";
                }else if(isYesterday(a[2])){
                    return "Yesterday";
                }else {
                    return getDateInISTFormat2();
                }
    }

    private boolean isToday(String day_date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        Calendar cal = Calendar.getInstance();
        String yesterday=dateFormat.format(cal.getTime());
        if(day_date.equals(yesterday)){
            return true;
        }
        return false;
    }

    private boolean isYesterday(String day_date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday=dateFormat.format(cal.getTime());
        if(day_date.equals(yesterday)){
            return true;
        }
        return false;
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