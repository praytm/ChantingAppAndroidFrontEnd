package com.service.iscon.vcr.Constants;

/**
 * Created by vkwb on 9/22/15.
 */

public final class WebServiceConstants {

    public static String BaseUrl="https://104.236.243.12/";
    //public static String BaseUrl="http://192.168.137.1:8080/";
    //UAT server

    public static String ServiceUrl = BaseUrl+"chantingapprest-0.0.1-SNAPSHOT/rest/user/";
    public static String ServiceProductThumbUrl = BaseUrl+"applinenetuat/img/product/thumb/";
    public static String ServiceProductFullUrl= BaseUrl+"applinenetuat/img/product/";
    public static String ServiceAttachmentThumbUrl = BaseUrl+"applinenetuat/Resources/img/order_attachment/thumb/";
    public static String ServiceAttachmentFullUrl= BaseUrl+"applinenetuat/Resources/img/order_attachment/";
    public static String ServiceProductThumbUrl_backup = BaseUrl+"applinenetuat/Resources/img/product/thumb/";
    public static String ServiceProductFullUrl_backup= BaseUrl+"applinenetuat/Resources/img/product/";

    public static String UserLogin = ServiceUrl + "login";
    public static String DailyQuote = ServiceUrl + "activate";

    //private static binding
    public static String totalTodaysBeadsCount="";
    public static String DailyTarget="";

    public static String UserRegister = ServiceUrl + "register";
    public static String ChantingHistory = ServiceUrl + "get_chanting_history";
    public static String SendNewSessionBeads = ServiceUrl + "save_new_chanting_session";
    public static String RefreshHome= ServiceUrl + "refresh_home";
    public static String ActivateUser= ServiceUrl + "activate";
    public static String DeActivateUser= ServiceUrl + "deactivate";
    public static String TotalBeadsForToday = BaseUrl+"chantingapprest-0.0.1-SNAPSHOT/rest/getTotalBeadsForToday";
    public static String getGetDailyTargetCount = BaseUrl+"chantingapprest-0.0.1-SNAPSHOT/rest/getDailyTarget";
    /****************************************
     * FCM
     ************************************/
    public static String Token_Registration_Service= BaseUrl + "applinedotnetweb/api/vender/token_registration_service";
}