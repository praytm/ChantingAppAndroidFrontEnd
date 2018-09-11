package com.service.iscon.vcr.model;

import java.io.Serializable;

public class UserStatistics implements Serializable {

    private int TotalUser=0;

    public int getTotalUser() {
        return TotalUser;
    }

    public void setTotalUser(int totalUser) {
        TotalUser = totalUser;
    }

    public int getTotalActiveUser() {
        return TotalActiveUser;
    }

    public void setTotalActiveUser(int totalActiveUser) {
        TotalActiveUser = totalActiveUser;
    }

    public int getTotalBeads() {
        return TotalBeads;
    }

    public void setTotalBeads(int totalBeads) {
        TotalBeads = totalBeads;
    }

    public int getTodaysBeads() {
        return TodaysBeads;
    }

    public void setTodaysBeads(int todaysBeads) {
        TodaysBeads = todaysBeads;
    }

    private int TotalActiveUser=0;

    public String getDailyQuotestring() {
        return DailyQuotestring;
    }

    public void setDailyQuotestring(String dailyQuotestring) {
        DailyQuotestring = dailyQuotestring;
    }

    private int TotalBeads=0;
    private int TodaysBeads=0;
    private String DailyQuotestring;

    UserStatistics US=null;

    public UserStatistics(int totalUser, int totalActiveUser, int totalBeads, int todaysBeads) {
        TotalUser = totalUser;
        TotalActiveUser = totalActiveUser;
        TotalBeads = totalBeads;
        TodaysBeads = todaysBeads;
    }


    public UserStatistics getInstance(){
        if(this.US==null)
            US=new UserStatistics();
        return US;
    }

    public UserStatistics(){
    }

}
