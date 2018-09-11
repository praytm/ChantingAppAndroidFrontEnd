package com.service.iscon.vcr.model;

import android.util.Log;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private int UserId=-1;
    private String Email="";
    private String Password="";
    private String FullName="";
    private String Mobile="";
    private String CreatedDate="";
    private String LastLogin="";
    public String DailyQuote="";
    private int IsActive=1;


    public void setDailyQuote(String dailyQuote) {
        DailyQuote = dailyQuote;
        Log.d("chantingQuoteuser---", "" + dailyQuote);

    }
    public String getDailyQuote() {
        Log.d("chantingQuotegetuser---", "" + DailyQuote);

        return DailyQuote;
    }


    UserInfo UI=null;

    public UserInfo(int id, String Name, String Email, String Password) {
        this.UserId=id;
        this.FullName=Name;
        this.Email=Email;
        this.Password=Password;
    }

    public UserInfo(int userId, String email, String password, String name, String mobile, String createdDate, String lastLogin, int isActive) {
        UserId = userId;
        Email = email;
        Password = password;
        FullName = name;
        Mobile = mobile;
        CreatedDate = createdDate;
        LastLogin = lastLogin;
        IsActive = isActive;
    }

    public UserInfo(String name, String mobile, String email, String password) {
        Email = email;
        Password = password;
        FullName = name;
        Mobile = mobile;
    }

    public UserInfo getInstance(){
        if(this.UI==null)
            UI=new UserInfo();
        return UI;
    }

    public UserInfo(){
    }
    public UserInfo(String email, String password) {
        this.Email = email;
        this.Password = password;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }



    public void setLastLogin(String lastLogin) {
        LastLogin = lastLogin;
    }
    public String getLastLogin() {

        return LastLogin;
    }
    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getId() {
        return UserId;
    }

    public void setId(int id) {
        UserId = id;
    }


}
