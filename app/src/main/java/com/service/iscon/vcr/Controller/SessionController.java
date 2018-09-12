package com.service.iscon.vcr.Controller;

import android.content.Context;

import com.service.iscon.vcr.Constants.WebServiceConstants;
import com.service.iscon.vcr.Helper.AsyncProcess;
import com.service.iscon.vcr.Helper.AsyncProcessListener;
import com.service.iscon.vcr.Model.SessionModel;
import com.service.iscon.vcr.Utils.NetUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by vkwb on 9/22/15.
 */
public class SessionController {

    public AsyncProcessListener<Object> getOnSessionHistory() {
        return OnSessionHistory;
    }

    public void setOnSessionHistory(AsyncProcessListener<Object> onSessionHistory) {
        OnSessionHistory = onSessionHistory;
    }

    private AsyncProcessListener<Object> OnSessionHistory;

    public static SessionController UserSessionHistory(final Context Ctx, final String email, final String password) {

        final SessionController UIC = new SessionController();

        if (!NetUtils.IsInternet(Ctx, true)) {
            if (UIC.getOnSessionHistory() != null) {
                UIC.getOnSessionHistory().ProcessFailed(new Exception("Internet Not Available"));
                return UIC;
            } else {
                return null;
            }
        }

        //Collecting Values to Pass
        final HashMap<String, String> HT = new HashMap<>();
        HT.put("email", email);
        HT.put("password", password);
        JSONObject json = getJsonObjectOf(HT);

        final HashMap<String, JSONObject> HTJ = new HashMap<>();
        HTJ.put("userDto", json);
        //HT.put("dateInput","");
        final String finaljson = getJsonOfHTJ(HTJ);
        AsyncProcess AP = new AsyncProcess(Ctx, "Loading your data...") {
            @Override
            protected Object doInBackground(Object... params) {
                String Response = NetUtils.PostWebServiceMethodforDotNet(Ctx, WebServiceConstants.ChantingHistory, finaljson);

//                String Response = "{\"technicalStatus\":\"SUCCESS\",\"responseCode\":\"0\",\"tag\":\"user_chanting_history\",\"chantingHistoryDTO\":{\"chantingHistory\":{\"2017/03/15\":20,\"2017/03/16\":90,\"2017/03/17\":70,\"2017/03/18\":40,\"2017/03/19\":80,\"2017/03/20\":10,\"2017/03/21\":20,\"2017/03/22\":80,\"2017/03/23\":50,\"2017/03/24\":30,\"2017/03/25\":440}}}";

                JSONObject JO = null;

                try {
                    JO = new JSONObject(Response);
                } catch (Exception ex) {
                    return "INVALID SERVICE RESPONSE";
                }

                try {
                    String responseTag = JO.getString("tag");
                    if (!responseTag.equals("user_chanting_history")) {
                        return "INVALID SERVICE";
                    }
                    //ArrayList<HashMap<String,Integer>> sessionHistory=new ArrayList<>();
                    ArrayList<SessionModel> sessionHistory=new ArrayList<>();
                    String technicalStatus = JO.getString("technicalStatus");
                    int responseCode = JO.getInt("responseCode");
                    if (technicalStatus.equals("SUCCESS")) {
                        switch (responseCode) {
                            case 0: //Response Success
                                if (JO.has("chantingHistoryDTO")) {
                                    if (!JO.isNull("chantingHistoryDTO")) {
                                        JSONObject chDTO = JO.getJSONObject("chantingHistoryDTO");
                                        if (chDTO.has("chantingHistory")) {
                                            if (!chDTO.isNull("chantingHistory")) {
                                                JSONObject chJO = chDTO.getJSONObject("chantingHistory");
                                                Iterator<String> keys=chJO.keys();
                                                while (keys.hasNext()){
                                                    String key=keys.next();
                                                    SessionModel mSessionModel=new SessionModel(key,chJO.getInt(key));
                                                    sessionHistory.add(mSessionModel);
                                                }
                                                return sessionHistory;
                                            }
                                        }
                                    }
                                }
                                break;
                            case 1:
                                return "Wrong Credentials";
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
                if (UIC.getOnSessionHistory() != null) {
                    if (o.getClass() == ArrayList.class) {
                        UIC.getOnSessionHistory().ProcessFinished(o);
                    } else {
                        UIC.getOnSessionHistory().ProcessFailed(new Exception(o.toString()));
                    }
                }
            }
        };
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

    private static String getJsonOfHTJ(HashMap<String, JSONObject> ht) {

        JSONObject JORequest = new JSONObject();
        for (Map.Entry<String, JSONObject> entry : ht.entrySet()) {
            try {
                JORequest.put(entry.getKey(), entry.getValue());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return JORequest.toString();
    }

    private static JSONObject getJsonObjectOf(HashMap<String, String> ht) {

        JSONObject JORequest = new JSONObject();
        for (Map.Entry<String, String> entry : ht.entrySet()) {
            try {
                JORequest.put(entry.getKey(), entry.getValue());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return JORequest;
    }
}