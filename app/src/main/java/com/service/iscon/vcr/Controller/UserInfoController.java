package com.service.iscon.vcr.Controller;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.service.iscon.vcr.Constants.WebServiceConstants;
import com.service.iscon.vcr.Helper.AsyncProcess;
import com.service.iscon.vcr.Helper.AsyncProcessListener;
import com.service.iscon.vcr.Model.SessionModel;
import com.service.iscon.vcr.Model.UserStatistics;
import com.service.iscon.vcr.Model.UserInfo;
import com.service.iscon.vcr.Utils.NetUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by vkwb on 9/22/15.
 */
public class UserInfoController {

    public AsyncProcessListener<Object> getOnUserAuthentication() {
        return OnUserAuthentication;
    }

    public void setOnUserAuthentication(AsyncProcessListener<Object> onUserAuthentication) {
        OnUserAuthentication = onUserAuthentication;
    }


    public AsyncProcessListener<Object> getOnUserRegistration() {
        return OnUserRegistration;
    }

    public void setOnUserRegistration(AsyncProcessListener<Object> onUserRegistration) {
        OnUserRegistration = onUserRegistration;
    }


    public AsyncProcessListener<Object> getOnSendBeads() {
        return OnSendBeads;
    }

    public void setOnSendBeads(AsyncProcessListener<Object> onSendBeads) {
        OnSendBeads = onSendBeads;
    }

    public AsyncProcessListener<Object> getOnRefreshHome() {
        return OnRefreshHome;
    }

    public void setOnRefreshHome(AsyncProcessListener<Object> onRefreshHome) {
        OnRefreshHome = onRefreshHome;
    }

    public AsyncProcessListener<Object> getOnDeActivateUser() {
        return OnDeActivateUser;
    }

    public AsyncProcessListener<Object> getOnDailyQuote() {
        return OnActivateDailyQuote;
    }


    public void setOnDeActivateUser(AsyncProcessListener<Object> onDeActivateUser) {
        OnDeActivateUser = onDeActivateUser;
    }

    public AsyncProcessListener<Object> getOnActivateUser() {
        return OnActivateUser;
    }

    public void setOnActivateUser(AsyncProcessListener<Object> onActivateUser) {
        OnActivateUser = onActivateUser;
    }

    public void setOnActivateDaily(AsyncProcessListener<Object> setOnActivateDaily) {
        OnActivateDailyQuote = setOnActivateDaily;
    }

    /**********************************************
     * New Services
     *****************************************/

    private AsyncProcessListener<Object> OnUserAuthentication;
    private AsyncProcessListener<Object> OnUserRegistration;
    private AsyncProcessListener<Object> OnSendBeads;
    private AsyncProcessListener<Object> OnRefreshHome;
    private AsyncProcessListener<Object> OnActivateUser;
    private AsyncProcessListener<Object> OnActivateDailyQuote;

    private AsyncProcessListener<Object> OnDeActivateUser;

    public static UserInfoController AuthenticateUser(final Context Ctx, final String email, final String password,boolean isRegisteredByGoogle) {

        final UserInfoController UIC = new UserInfoController();

        if (!NetUtils.IsInternet(Ctx, true)) {
            if (UIC.getOnUserAuthentication() != null) {
                UIC.getOnUserAuthentication().ProcessFailed(new Exception("Internet Not Available"));
                return UIC;
            } else {
                return null;
            }
        }

        //Collecting Values to Pass
        final HashMap<String, String> HT = new HashMap<>();
        HT.put("email", email);
        HT.put("password", password);
        final String json = getJsonOf(HT);
        final UserInfo UI = new UserInfo();

        AsyncProcess AP = new AsyncProcess(Ctx, "Requesting Authentication") {
            @Override
            protected Object doInBackground(Object... params) {
                String Response = NetUtils.PostWebServiceMethodforDotNet(Ctx, WebServiceConstants.UserLogin, json);

                // String Response2="{\"error\": {\"alert\": \"Enter Valid Mobile Number\"}}";
                //String Response = "{\"error\":\"0\",\"user\": {\"name\": \"Aditya Anand\",\"email\": \""+email+"\",\"mobile\": \"9999999991\",\"last_login\": \"2017-02-12T13:26:16\"}}";
                //String Response = "{\"technicalStatus\":\"SUCCESS\",\"responseCode\":\"0\",\"tag\":\"login\",\"user\":{\"userId\":\"5\",\"name\":\"Aditya Anand\",\"createdDate\":\"2017-02-10T23:21:22\",\"lastLoginDate\":\"2017-02-12T13:26:16\",\"email\":\"er.aditya.anand@gmail.com\",\"password\":null,\"mobile\":\"9999999991\"}}";

                JSONObject JO = null;
                try {
                    JO = new JSONObject(Response);
                } catch (Exception ex) {
                    return "INVALID SERVICE RESPONSE";
                }

                try {
                    String responseTag = JO.getString("tag");
                    if (!responseTag.equals("login")) {
                        return "INVALID SERVICE";
                    }
                    String technicalStatus = JO.getString("technicalStatus");
                    int responseCode = JO.getInt("responseCode");
                    //if (technicalStatus.equals("SUCCESS")) {
                    switch (responseCode) {
                        case 0: //Login Success
                            if (!JO.isNull("user")) {
                                JSONObject JOUser = JO.getJSONObject("user");
                                UserInfo UI = new UserInfo();

                                if (!JOUser.isNull("userId")) {
                                    UI.setId(JOUser.getInt("userId"));
                                }

                                if (!JOUser.isNull("name")) {
                                    UI.setFullName(JOUser.getString("name"));
                                }

                                if (!JOUser.isNull("createdDate")) {
                                    UI.setCreatedDate(JOUser.getString("createdDate"));
                                }
                                if (!JOUser.isNull("lastLoginDate")) {
                                    UI.setLastLogin(JOUser.getString("lastLoginDate"));
                                }

                                if (!JOUser.isNull("email")) {
                                    UI.setEmail(JOUser.getString("email"));
                                }
                                if (!JOUser.isNull("mobile")) {
                                    UI.setMobile(JOUser.getString("mobile"));
                                }
                                if (!JOUser.isNull("profilepic")) {
                                    UI.setProfilePic(JOUser.getString("profilepic"));
                                }

                                if (!JOUser.isNull("password")) {
                                    UI.setPassword(JOUser.getString("password"));
                                } else {
                                    UI.setPassword(password);
                                }

                                return UI;
                            }
                            break;
                        case 1:
                            return "Wrong Password";
                        case 2:
                            return "Email id not found";
                        case 10:
                            return "Technical error found";
                    }
                    //  return "INVALID SERVICE RESPONSE";
                    //
                    // }

                } catch (Exception ex) {
                    return ex.getMessage();
                }
                return "INVALID SERVICE RESPONSE";
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (UIC.getOnUserAuthentication() != null) {
                    if (o.getClass() == UserInfo.class) {
                        UIC.getOnUserAuthentication().ProcessFinished(o);
                    } else {
                        UIC.getOnUserAuthentication().ProcessFailed(new Exception(o.toString()));
                    }
                }
            }
        };
        AP.execute();
        return UIC;
    }

    public static UserInfoController RegisterUser(final Context Ctx, final UserInfo UI, String isRegisteredViaGoogle) {

        final UserInfoController UIC = new UserInfoController();

        if (!NetUtils.IsInternet(Ctx, true)) {
            if (UIC.getOnUserRegistration() != null) {
                UIC.getOnUserRegistration().ProcessFailed(new Exception("Internet Not Available"));
                return UIC;
            } else {
                return null;
            }
        }

        //Collecting Values to Pass
        final HashMap<String, String> HT = new HashMap<>();
        HT.put("email", UI.getEmail());
        final String password=UI.getPassword();
        HT.put("password", password);
        HT.put("isRegisteredViaGoogle","false");
        if (!TextUtils.isEmpty(UI.getMobile()))
            HT.put("mobile", UI.getMobile());
        if (!TextUtils.isEmpty(UI.getFullName()))
            HT.put("name", UI.getFullName());
        HT.put("googleAuthToken",UI.getGoogleAuthToken());
        HT.put("profilepic",UI.getProfilePic());
        //HT.put("profilePic","https://lh3.googleusercontent.com/-UGapVoMB9Hw/AAAAAAAAAAI/AAAAAAAAAAA/APUIFaNIxSm7SWwhYogYhs_whNS7RWAy2Q/s96-c/photo.jpg");
        final String json = getJsonOf(HT);
        System.out.println("***json***"+json);
        //final UserInfo UI = new UserInfo();

        AsyncProcess AP = new AsyncProcess(Ctx, "Requesting Authentication") {
            @Override
            protected Object doInBackground(Object... params) {
                String Response = NetUtils.PostWebServiceMethodforDotNet(Ctx, WebServiceConstants.UserRegister, json);
//{"technicalStatus":"SUCCESS","responseCode":"2","tag":"registration","user":{"userId":null,"name":"Sushma","createdDate":"2017-08-13T19:55:43","lastLoginDate":null,"email":"sush@gmail.com","password":null,"mobile":"9049470990","isRegisteredViaGoogle":null,"googleAuthToken":null}}
                // String Response2="{\"error\": {\"alert\": \"Enter Valid Mobile Number\"}}";
                //String Response = "{\"error\":\"0\",\"user\": {\"name\": \"Shrinath Tamada\",\"email\": \"shri@gmail.com\",\"mobile\": \"9898989898\",\"last_login\": \"2015-09-17 13:26:16\"}}";
                //String Response = "{\"technicalStatus\":\"SUCCESS\",\"responseCode\":\"0\",\"tag\":\"registration\",\"user\":{\"userId\":\"5\",\"name\":\""+UI.getFullName()+"\",\"createdDate\":\"2017-02-10T23:21:22\",\"lastLoginDate\":\"2017-02-12T13:26:16\",\"email\":\""+UI.getEmail()+"\",\"password\":null,\"mobile\":\""+UI.getMobile()+"\"}}";

                System.out.println("******"+Response);

                JSONObject JO = null;
                try {

                    JO = new JSONObject(Response);
                } catch (Exception ex) {
                    System.out.println("---------------"+ex.getMessage());
                    return "INVALID SERVICE RESPONSE";
                }

                try {
                    String responseTag = JO.getString("tag");
                    if (!responseTag.equals("registration")) {
                        return "INVALID SERVICE";
                    }
                    String technicalStatus = JO.getString("technicalStatus");
                    int responseCode = JO.getInt("responseCode");
                    if (technicalStatus.equals("SUCCESS")) {
                        switch (responseCode) {
                            case 0: //Login Success
                                if (!JO.isNull("user")) {
                                    JSONObject JOUser = JO.getJSONObject("user");
                                    UserInfo UI = new UserInfo();

                                    if (!JOUser.isNull("userId")) {
                                        UI.setId(JOUser.getInt("userId"));
                                    }

                                    if (!JOUser.isNull("name")) {
                                        UI.setFullName(JOUser.getString("name"));
                                    }

                                    if (!JOUser.isNull("createdDate")) {
                                        UI.setCreatedDate(JOUser.getString("createdDate"));
                                    }
                                    if (!JOUser.isNull("lastLoginDate")) {
                                        UI.setLastLogin(JOUser.getString("lastLoginDate"));
                                    }

                                    if (!JOUser.isNull("email")) {
                                        UI.setEmail(JOUser.getString("email"));
                                    }
                                    if (!JOUser.isNull("mobile")) {
                                        UI.setMobile(JOUser.getString("mobile"));
                                    }
                                    if (!JOUser.isNull("profilepic")) {
                                        UI.setProfilePic(JOUser.getString("profilepic"));
                                    }
                                    if (!JOUser.isNull("password")) {
                                        UI.setPassword(JOUser.getString("password"));
                                    } else {
                                        UI.setPassword(password);
                                    }

                                    return UI;
                                }
                                break;
                            case 1:
                                return "User already exist with given Email.";
                            case 10:
                                return "Technical error found";
                        }
                        return "INVALID SERVICE RESPONSE";
                    }else  if(technicalStatus.equals("FAIL") && responseCode==10) {
                        return "Technical error found";
                    }

                } catch (Exception ex) {
                    return ex.getMessage();
                }
                return "INVALID SERVICE RESPONSE";
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (UIC.getOnUserRegistration() != null) {
                    if (o.getClass() == UserInfo.class) {
                        UIC.getOnUserRegistration().ProcessFinished(o);
                    } else {
                        UIC.getOnUserRegistration().ProcessFailed(new Exception(o.toString()));
                    }
                }
            }
        };
        AP.set_ShowProgress(false);
        AP.execute();
        return UIC;
    }

    public static UserInfoController sendBeads(final Context Ctx, final UserInfo UI, final SessionModel SM) {

        final UserInfoController UIC = new UserInfoController();

        if (!NetUtils.IsInternet(Ctx, true)) {
            if (UIC.getOnSendBeads() != null) {
                UIC.getOnSendBeads().ProcessFailed(new Exception("Internet Not Available"));
                return UIC;
            } else {
                return null;
            }
        }

        //Collecting Values to Pass
        final HashMap<String, String> HT = new HashMap<>();
        HT.put("userName", UI.getEmail());
        HT.put("password", UI.getPassword());
        HT.put("chantingSessionDate", SM.getDate());
        HT.put("chantingSessionStartTime", SM.getStartTime());
        HT.put("chantingSessionEndTime", SM.getEndTime());
        HT.put("numberOfBeads", String.valueOf(SM.getBeads()));
/*
        HT.put("userName", "priyashirke21@gmail.com");
        HT.put("password", "priyanka");
        HT.put("chantingSessionDate", "2017-03-24T13:23:21");
        HT.put("chantingSessionStartTime", "2017-03-25T13:23:21");
        HT.put("chantingSessionEndTime", "2017-03-25T14:30:54");
        HT.put("numberOfBeads", "22");
*/

        final String json = getJsonOf(HT);

        AsyncProcess AP = new AsyncProcess(Ctx, "Submitting chanting count..") {
            @Override
            protected Object doInBackground(Object... params) {
                String Response = NetUtils.PostWebServiceMethodforDotNet(Ctx, WebServiceConstants.SendNewSessionBeads, json);

                // String Response2="{\"error\": {\"alert\": \"Enter Valid Mobile Number\"}}";
                //String Response = "{\"error\":\"0\",\"user\": {\"name\": \"Aditya Anand\",\"email\": \""+email+"\",\"mobile\": \"9999999991\",\"last_login\": \"2017-02-12T13:26:16\"}}";
                //String Response = "{\"technicalStatus\":\"SUCCESS\",\"responseCode\":\"0\",\"tag\":\"login\",\"user\":{\"userId\":\"5\",\"name\":\"Aditya Anand\",\"createdDate\":\"2017-02-10T23:21:22\",\"lastLoginDate\":\"2017-02-12T13:26:16\",\"email\":\"er.aditya.anand@gmail.com\",\"password\":null,\"mobile\":\"9999999991\"}}";

                JSONObject JO = null;
                try {
                    JO = new JSONObject(Response);
                } catch (Exception ex) {
                    return "INVALID SERVICE RESPONSE";
                }

                try {
        /*            String responseTag = JO.getString("tag");
                    if (!responseTag.equals("login")) {
                        return "INVALID SERVICE";
                    }
        */
                    String technicalStatus = JO.getString("technicalStatus");
                    int responseCode = JO.getInt("responseCode");
                    if (technicalStatus.equals("SUCCESS")) {
                        switch (responseCode) {
                            case 0: //Login Success
                                boolean ischantingSessionSaved = false;
                                if (JO.has("chantingSessionSaved")) {
                                    if (!JO.isNull("chantingSessionSaved")) {
                                        ischantingSessionSaved = JO.getBoolean("chantingSessionSaved");
                                    }
                                }
                                return ischantingSessionSaved;
                            case 1:
                                return "Authentication failed.";
                            case 10:
                                return "Technical error found";
                        }
                        return "INVALID SERVICE RESPONSE";
                    }

                } catch (Exception ex) {
                    return ex.getMessage();
                }
                return "INVALID SERVICE RESPONSE";
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (UIC.getOnSendBeads() != null) {
                    if (o.getClass() == Boolean.class) {
                        UIC.getOnSendBeads().ProcessFinished(o);
                    } else {
                        UIC.getOnSendBeads().ProcessFailed(new Exception(o.toString()));
                    }
                }
            }
        };
        AP.execute();
        return UIC;
    }

    public static UserInfoController RefreshHome(final Context Ctx, final UserInfo UI,final Boolean showProgrss) {

        final UserInfoController UIC = new UserInfoController();

        if (!NetUtils.IsInternet(Ctx, true)) {
            if (UIC.getOnRefreshHome() != null) {
                UIC.getOnRefreshHome().ProcessFailed(new Exception("Internet Not Available"));
                return UIC;
            } else {
                return null;
            }
        }

        //Collecting Values to Pass
        final HashMap<String, String> HT = new HashMap<>();
        HT.put("email", UI.getEmail());
        HT.put("password", UI.getPassword());
        final String json = getJsonOf(HT);

        AsyncProcess AP = new AsyncProcess(Ctx, "Loading page") {
            @Override
            protected Object doInBackground(Object... params) {
                String Response = NetUtils.PostWebServiceMethodforDotNet(Ctx, WebServiceConstants.RefreshHome, json);

                // String Response2="{\"error\": {\"alert\": \"Enter Valid Mobile Number\"}}";
                //String Response = "{\"error\":\"0\",\"user\": {\"name\": \"Aditya Anand\",\"email\": \""+email+"\",\"mobile\": \"9999999991\",\"last_login\": \"2017-02-12T13:26:16\"}}";
                //String Response = "{\"technicalStatus\":\"SUCCESS\",\"responseCode\":\"0\",\"tag\":\"login\",\"user\":{\"userId\":\"5\",\"name\":\"Aditya Anand\",\"createdDate\":\"2017-02-10T23:21:22\",\"lastLoginDate\":\"2017-02-12T13:26:16\",\"email\":\"er.aditya.anand@gmail.com\",\"password\":null,\"mobile\":\"9999999991\"}}";

                JSONObject JO = null;
                try {
                    JO = new JSONObject(Response);
                } catch (Exception ex) {
                    return "INVALID SERVICE RESPONSE";
                }

                try {
                    String responseTag = JO.getString("tag");
                    if (!responseTag.equals("refresh_home")) {
                        return "INVALID SERVICE TAG";
                    }
                    String technicalStatus = JO.getString("technicalStatus");
                    int responseCode = JO.getInt("responseCode");
                    if (technicalStatus.equals("SUCCESS")) {
                        switch (responseCode) {
                            case 0: //Login Success
                                if (!JO.isNull("refreshUserStatisticsOutputDTO")) {
                                    JSONObject JORefreshStat = JO.getJSONObject("refreshUserStatisticsOutputDTO");
                                    UserStatistics US = new UserStatistics();

                                    if (!JORefreshStat.isNull("totalNumberOfActiveUsers")) {
                                        US.setTotalActiveUser(JORefreshStat.getInt("totalNumberOfActiveUsers"));
                                    }

                                    if (!JORefreshStat.isNull("totalNumberOfUsers")) {
                                        US.setTotalUser(JORefreshStat.getInt("totalNumberOfUsers"));
                                    }
                                    if (!JORefreshStat.isNull("totalNumberOfBeadsForUser")) {
                                        US.setTotalBeads(JORefreshStat.getInt("totalNumberOfBeadsForUser"));
                                    }
                                    if (!JORefreshStat.isNull("todaysNumberOfBeadsForUser")) {
                                        US.setTodaysBeads(JORefreshStat.getInt("todaysNumberOfBeadsForUser"));
                                    }
                                    return US;
                                }
                                break;
                            case 1:
                                return "Wrong Credentials";
                            case 10:
                                return "Technical error found";
                        }
                        return "INVALID SERVICE RESPONSE";
                    }

                } catch (Exception ex) {
                    return ex.getMessage();
                }
                return "INVALID SERVICE RESPONSE";
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (UIC.getOnRefreshHome() != null) {
                    if (o.getClass() == UserStatistics.class) {
                        UIC.getOnRefreshHome().ProcessFinished(o);
                    } else {
                        UIC.getOnRefreshHome().ProcessFailed(new Exception(o.toString()));
                    }
                }
            }
        };
        AP.set_ShowProgress(showProgrss);
        AP.execute();
        return UIC;
    }

    private static String getJsonOf(HashMap<String, String> ht) {

        JSONObject JORequest = new JSONObject();
        for (Map.Entry<String, String> entry : ht.entrySet()) {
            try {
                JORequest.put(entry.getKey(), entry.getValue());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return JORequest.toString();
    }

    public static UserInfoController ActivateUser(final Context Ctx, final UserInfo UI) {

        final UserInfoController UIC = new UserInfoController();

        if (!NetUtils.IsInternet(Ctx, true)) {
            if (UIC.getOnActivateUser()!= null) {
                UIC.getOnActivateUser().ProcessFailed(new Exception("Internet Not Available"));
                return UIC;
            } else {
                return null;
            }
        }

        //Collecting Values to Pass
        final HashMap<String, String> HT = new HashMap<>();
        HT.put("email", UI.getEmail());
        HT.put("password", UI.getPassword());
        final String json = getJsonOf(HT);

        AsyncProcess AP = new AsyncProcess(Ctx, "Updating Status..") {
            @Override
            protected Object doInBackground(Object... params) {
                String Response = NetUtils.PostWebServiceMethodforDotNet(Ctx, WebServiceConstants.ActivateUser, json);

                JSONObject JO = null;
                try {
                    JO = new JSONObject(Response);
                } catch (Exception ex) {
                    return "INVALID SERVICE RESPONSE";
                }

                try {
                    String responseTag = JO.getString("tag");
                    if (!responseTag.equals("changeUserStatus")) {
                        return "INVALID SERVICE TAG";
                    }
                    String technicalStatus = JO.getString("technicalStatus");
                    int responseCode = JO.getInt("responseCode");
                    if (technicalStatus.equals("SUCCESS")) {
                        switch (responseCode) {
                            case 0: //Login Success
                                if (!JO.isNull("userStatusChanged")) {
                                    if(JO.getBoolean("userStatusChanged")){
                                        return true;
                                    }else{
                                        return "User Activation Failed";
                                    }
                                }
                                break;
                            case 1:
                                return "Wrong Credentials";
                            case 10:
                                return "Technical error found";
                        }
                        return "INVALID SERVICE RESPONSE";
                    }

                } catch (Exception ex) {
                    return ex.getMessage();
                }
                return "INVALID SERVICE RESPONSE";
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (UIC.getOnActivateUser() != null) {
                    if (o.getClass() == Boolean.class) {
                        UIC.getOnActivateUser().ProcessFinished(o);
                    } else {
                        UIC.getOnActivateUser().ProcessFailed(new Exception(o.toString()));
                    }
                }
            }
        };
        AP.set_ShowProgress(false);
        AP.execute();
        return UIC;
    }

    public static UserInfoController DeactivateUser(final Context Ctx, final UserInfo UI) {

        final UserInfoController UIC = new UserInfoController();

        if (!NetUtils.IsInternet(Ctx, true)) {
            if (UIC.getOnDeActivateUser()!= null) {
                UIC.getOnDeActivateUser().ProcessFailed(new Exception("Internet Not Available"));
                return UIC;
            } else {
                return null;
            }
        }

        //Collecting Values to Pass
        final HashMap<String, String> HT = new HashMap<>();
        HT.put("email", UI.getEmail());
        HT.put("password", UI.getPassword());
        final String json = getJsonOf(HT);

        AsyncProcess AP = new AsyncProcess(Ctx, "Updating Status..") {
            @Override
            protected Object doInBackground(Object... params) {
                String Response = NetUtils.PostWebServiceMethodforDotNet(Ctx, WebServiceConstants.DeActivateUser, json);

                // String Response2="{\"error\": {\"alert\": \"Enter Valid Mobile Number\"}}";
                //String Response = "{\"error\":\"0\",\"user\": {\"name\": \"Aditya Anand\",\"email\": \""+email+"\",\"mobile\": \"9999999991\",\"last_login\": \"2017-02-12T13:26:16\"}}";
                //String Response = "{\"technicalStatus\":\"SUCCESS\",\"responseCode\":\"0\",\"tag\":\"login\",\"user\":{\"userId\":\"5\",\"name\":\"Aditya Anand\",\"createdDate\":\"2017-02-10T23:21:22\",\"lastLoginDate\":\"2017-02-12T13:26:16\",\"email\":\"er.aditya.anand@gmail.com\",\"password\":null,\"mobile\":\"9999999991\"}}";

                JSONObject JO = null;
                try {
                    JO = new JSONObject(Response);
                } catch (Exception ex) {
                    return "INVALID SERVICE RESPONSE";
                }

                try {
                    String responseTag = JO.getString("tag");
                    if (!responseTag.equals("changeUserStatus")) {
                        return "INVALID SERVICE TAG";
                    }
                    String technicalStatus = JO.getString("technicalStatus");
                    int responseCode = JO.getInt("responseCode");
                    if (technicalStatus.equals("SUCCESS")) {
                        switch (responseCode) {
                            case 0: //Login Success
                                if (!JO.isNull("userStatusChanged")) {
                                    if(JO.getBoolean("userStatusChanged")){
                                        return true;
                                    }else{
                                        return "User deactivation Failed";
                                    }
                                }
                                break;
                            case 1:
                                return "Wrong Credentials";
                            case 10:
                                return "Technical error found";
                        }
                        return "INVALID SERVICE RESPONSE";
                    }

                } catch (Exception ex) {
                    return ex.getMessage();
                }
                return "INVALID SERVICE RESPONSE";
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (UIC.getOnDeActivateUser() != null) {
                    if (o.getClass() == Boolean.class) {
                        UIC.getOnDeActivateUser().ProcessFinished(o);
                    } else {
                        UIC.getOnDeActivateUser().ProcessFailed(new Exception(o.toString()));
                    }
                }
            }
        };
        AP.set_ShowProgress(false);
        AP.execute();
        return UIC;
    }

    public static UserInfoController GetDailyQuote(final Context Ctx, final UserInfo UI) {

        final UserInfoController UIC = new UserInfoController();

        if (!NetUtils.IsInternet(Ctx, true)) {
            if (UIC.getOnDailyQuote()!= null) {
                UIC.getOnDailyQuote().ProcessFailed(new Exception("Internet Not Available"));
                return UIC;
            } else {
                return null;
            }
        }

        //Collecting Values to Pass
        final HashMap<String, String> HT = new HashMap<>();
        HT.put("email", UI.getEmail());
        HT.put("password", UI.getPassword());
        final String json = getJsonOf(HT);

        AsyncProcess AP = new AsyncProcess(Ctx, "Updating Status..") {
            @Override
            protected Object doInBackground(Object... params) {
                String Response = NetUtils.PostWebServiceMethodforDotNet(Ctx, WebServiceConstants.DailyQuote, json);

                // String Response2="{\"error\": {\"alert\": \"Enter Valid Mobile Number\"}}";
                //String Response = "{\"error\":\"0\",\"user\": {\"name\": \"Aditya Anand\",\"email\": \""+email+"\",\"mobile\": \"9999999991\",\"last_login\": \"2017-02-12T13:26:16\"}}";
                //String Response = "{\"technicalStatus\":\"SUCCESS\",\"responseCode\":\"0\",\"tag\":\"login\",\"user\":{\"userId\":\"5\",\"name\":\"Aditya Anand\",\"createdDate\":\"2017-02-10T23:21:22\",\"lastLoginDate\":\"2017-02-12T13:26:16\",\"email\":\"er.aditya.anand@gmail.com\",\"password\":null,\"mobile\":\"9999999991\"}}";

                JSONObject JO = null;
                try {
                    JO = new JSONObject(Response);
                } catch (Exception ex) {
                    return "INVALID SERVICE RESPONSE";
                }

                try {
                    String responseTag = JO.getString("tag");
                    if (!responseTag.equals("changeUserStatus")) {
                        return "INVALID SERVICE TAG";
                    }
                    String technicalStatus = JO.getString("technicalStatus");
                    int responseCode = JO.getInt("responseCode");
                    if (technicalStatus.equals("SUCCESS")) {
                        switch (responseCode) {
                            case 0: //Login Success
                                UserInfo UI = new UserInfo();
                                UI.setDailyQuote(JO.getString("chantingQuote"));
                                Log.d("chantingQuote---",""+JO.getString("chantingQuote"));
                                return UI;

                            case 1:
                                return "Wrong Credentials";
                            case 10:
                                return "Technical error found";
                        }
                        return "INVALID SERVICE RESPONSE";
                    }

                } catch (Exception ex) {
                    return ex.getMessage();
                }
                return "INVALID SERVICE RESPONSE";
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (UIC.getOnDailyQuote() != null) {
                    if (o.getClass() == UserInfo.class) {
                        UIC.getOnDailyQuote().ProcessFinished(o);
                    } else {
                        UIC.getOnDailyQuote().ProcessFailed(new Exception(o.toString()));
                    }
                }
            }
        };
        AP.set_ShowProgress(false);
        AP.execute();
        return UIC;
    }

    public static UserInfoController getTotalBeadsForToday(final Context Ctx, final UserInfo UI) {

        final UserInfoController UIC = new UserInfoController();

        if (!NetUtils.IsInternet(Ctx, true)) {
            if (UIC.getOnDailyQuote()!= null) {
                UIC.getOnDailyQuote().ProcessFailed(new Exception("Internet Not Available"));
                return UIC;
            } else {
                return null;
            }
        }

        //Collecting Values to Pass
        final HashMap<String, String> HT = new HashMap<>();
        HT.put("email", UI.getEmail());
        HT.put("password", UI.getPassword());
        final String json = getJsonOf(HT);

        AsyncProcess AP = new AsyncProcess(Ctx, "Updating Status..") {
            @Override
            protected Object doInBackground(Object... params) {
                String Response = NetUtils.PostWebServiceMethodforDotNet(Ctx, WebServiceConstants.TotalBeadsForToday, json);

                UserInfo UI = new UserInfo();
                UI.setTotalTodaysBeadsCount(Response);
                WebServiceConstants.totalTodaysBeadsCount = Response;
                return UI;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (UIC.getOnDailyQuote() != null) {
                    if (o.getClass() == UserInfo.class) {
                        UIC.getOnDailyQuote().ProcessFinished(o);
                    } else {
                        UIC.getOnDailyQuote().ProcessFailed(new Exception(o.toString()));
                    }
                }
            }
        };
        AP.set_ShowProgress(false);
        AP.execute();
        return UIC;
    }

    public static UserInfoController getDailyTargetCount(final Context Ctx, final UserInfo UI) {

        final UserInfoController UIC = new UserInfoController();

        if (!NetUtils.IsInternet(Ctx, true)) {
            if (UIC.getOnDailyQuote()!= null) {
                UIC.getOnDailyQuote().ProcessFailed(new Exception("Internet Not Available"));
                return UIC;
            } else {
                return null;
            }
        }

        AsyncProcess AP = new AsyncProcess(Ctx, "Updating Status..") {
            @Override
            protected Object doInBackground(Object... params) {
                String Response = NetUtils.PostWebServiceMethodforDotNet(Ctx, WebServiceConstants.getGetDailyTargetCount, "DAILY_TARGET");

                UserInfo UI = new UserInfo();
                UI.setDailyTarget(Response);
                WebServiceConstants.DailyTarget = Response;
                return UI;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (UIC.getOnDailyQuote() != null) {
                    if (o.getClass() == UserInfo.class) {
                        UIC.getOnDailyQuote().ProcessFinished(o);
                    } else {
                        UIC.getOnDailyQuote().ProcessFailed(new Exception(o.toString()));
                    }
                }
            }
        };
        AP.set_ShowProgress(false);
        AP.execute();
        return UIC;
    }
}