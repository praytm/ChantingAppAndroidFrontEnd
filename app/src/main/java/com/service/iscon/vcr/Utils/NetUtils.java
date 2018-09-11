package com.service.iscon.vcr.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager.BadTokenException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NetUtils {

    private static AlertDialog updatealertDialog;
    private static AlertDialog.Builder alertDialogBuilder;

    public static boolean IsInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            try {
                new AlertDialog.Builder(context)
                        .setTitle(("App Name"))
                        .setMessage("No Internet")
                        .setPositiveButton("OK", null)
                        .show();
            } catch (BadTokenException e) {
                e.printStackTrace();
            }
            return false;
        }
    }


    public static boolean IsWifiAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isWifi =netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI;
        return isWifi;
    }

    public static boolean IsInternet(Context context, boolean SHOW_DIALOG) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            try {
                if (context instanceof Activity) {
                    if (SHOW_DIALOG) {
                        new AlertDialog.Builder(context)
                                .setTitle(("App Name"))
                                .setMessage("No Internet")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
            } catch (BadTokenException e) {

            }
            return false;
        }

    }

    public static String PostWebServiceMethodforDotNet(final Context ctx, String Url, String jsonInput) {

        DefaultHttpClient httpclient = new MyHttpClient(ctx);
        //  HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Url);
        HttpResponse response = null;
        String response1 = null;

        try {
            // Add your data
            //    jsonParams.put("email", "test@yahoo.com");
            //   jsonParams.put("password", "test");
            //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            //nameValuePairs.add(new BasicNameValuePair("email", "test@yahoo.com"));
            //nameValuePairs.add(new BasicNameValuePair("password", "test"));
            StringEntity params = new StringEntity(jsonInput.toString());
            httppost.addHeader("content-type", "application/json");
            httppost.setEntity(params);

            response = httpclient.execute(httppost);                // Execute HTTP Post Request

            InputStream inputStream = response.getEntity().getContent();

            /*OutputStream os = _UrlConnection.getOutputStream();
            os.write(jsonInput.getBytes("UTF-8"));
            os.close();*/

            //Executing Request
            int _ResponseCode = response.getStatusLine().getStatusCode();
            // int _ResponseCode = 404;

            if (_ResponseCode == 200) {
                InputStream _Is = new BufferedInputStream(inputStream);
                response1 = convertStreamToString(_Is);
                _Is.close();

            } else if (_ResponseCode == 500) {
                //response = convertStreamToString(_UrlConnection.getErrorStream());
            } else {
                response1 = "INVALID RESPONSE";
            }


            System.out.println("*******" + response1);

            return response1;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return response1;

    }


    public static String PostWebServiceMethodforDotNet_old(final Context ctx, String Url, HashMap<String, String> HT) {
        URL _Url;
        HttpURLConnection _UrlConnection = null;
        String response = null;

        try {
            _Url = new URL(Url);

            _UrlConnection = (HttpURLConnection) _Url.openConnection();
            _UrlConnection.setRequestMethod("POST");
            _UrlConnection.setDoInput(true);
            _UrlConnection.setDoOutput(true);
            _UrlConnection.setConnectTimeout(2000);
            _UrlConnection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
           /* Iterator<Map.Entry<String, String>> iterator = HT.entrySet().iterator();



            while (iterator.hasNext()) {
                Map.Entry<String,String> pairs = (Map.Entry<String,String>)iterator.next();
                String value =  pairs.getValue();
                String key = pairs.getKey();
                _UrlConnection.addRequestProperty(pairs.getKey(),pairs.getValue());

                //_UrlConnection.getRequestProperties();
                Log.i("HM=",key +"--->"+value);
            }*/

            /*String m="9763970349";

            for(int i=0;i<HT.size();i++){
                //_UrlConnection.addRequestProperty(HM.keySet().toArray()[i].toString(),""+HM.get(HM.keySet().toArray()[i].toString().trim()).toString()+"");
                _UrlConnection.addRequestProperty("key"+i,"value"+1);
                if(i==1)
                    _UrlConnection.addRequestProperty(HT.get,m);
            }*/

            /*_UrlConnection.addRequestProperty("imei","23128149480277");
            _UrlConnection.addRequestProperty("mobile","8149480277");
*/
            //Adding Parameter for API Version
            /*ApplineCustomerApplication ACA = (ApplineCustomerApplication) ctx.getApplicationContext();
            String APIVersion = ACA.get_ApplineDataContext().getAPIVersion();
            HM.put("API_VERSION", APIVersion);*/

            String json = "{\"mobile\":9763970349,\"imei\":\"12345678912345\"}";

          /*  //Adding Parameters
            String QueryString = HashMapToUrlQueryString(HM);
            OutputStream os = _UrlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(QueryString);
            writer.flush();
            writer.close();
            os.close();*/


            OutputStream os = _UrlConnection.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();

        /*    Hashtable HT=new Hashtable();
            HT.putAll(HM);
            OutputStreamWriter writer = new OutputStreamWriter(_UrlConnection.getOutputStream());
            writer.write(getPostParamString(HT));
            writer.flush();
            writer.close();*/



            //Executing Request
           int _ResponseCode = _UrlConnection.getResponseCode();
          // int _ResponseCode = 404;

            if (_ResponseCode == 200) {
                InputStream _Is = new BufferedInputStream(_UrlConnection.getInputStream());
                response = convertStreamToString(_Is);
                _Is.close();

                //For Testing
                //response="{\"error\":{\"alert\":\"api update\"}}";

                /*if (checkAPIupdated(ctx, response)) {
                    response = "UPDATE APPLICATION";
                }*/

            } else if (_ResponseCode == 500) {
                response = convertStreamToString(_UrlConnection.getErrorStream());
            } else {
                response = "INVALID RESPONSE";

            }
        } catch (Exception ex) {

        } finally {
            _UrlConnection.disconnect();
        }
        return response;
    }


    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new InputStreamReader(is,"UTF-8"), 8000);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("UTF reader",e.getMessage());
        }
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] b = sb.toString().getBytes(Charset.forName("UTF-8"));
        try {
            String sb2 = new String(b, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("Byte Conversion",e.getMessage());
        }
        return sb.toString();
    }

    //rewrited by vinod sir on 17 Nov 2015
    private static String HashMapToUrlQueryString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            try {
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (Exception ex) {
                result.append(URLEncoder.encode("null", "UTF-8"));
            }
        }
        return result.toString();
    }


    public Boolean isAuthentic(String response, Context context)
            throws BadTokenException {
        // TODO Auto-generated method stub
        Boolean jSONRes = false;
        try {
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getString("success") != null) {
                jSONRes = true;
            }
        } catch (JSONException e1) {

            e1.printStackTrace();
            return jSONRes;
        } catch (NullPointerException e) {

            return jSONRes;
        } catch (RuntimeException e2) {
            // TODO: handle exception
        }
        return jSONRes;
    }

    public static void getAddressFromLocation(final String locationAddress,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = (Address) addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        sb.append(address.getLatitude()).append("\n");
                        sb.append(address.getLongitude()).append("\n");
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e("Error", "Unable to connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address: " + locationAddress +
                                "\n\nLatitude and Longitude :\n" + result;
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address: " + locationAddress +
                                "\n Unable to get Latitude and Longitude for this address location.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }


    private int responseCode;

    private String message;
    private String response;
    public String url;


  /*  private static boolean checkAPIupdated(final Context ctx, String response) {

        try {
            JSONObject JO = new JSONObject(response);
            if (JO.has("error")) {
                String Alert = JO.getJSONObject("error").getString("alert");
                if (Alert.contains("update")) {
                    return displayAPPUpdateDialogue(ctx);
                }
            }

        } catch (Exception e) {
            //No Error
            return false;
        }

        return false;
    }*/


    public static AlertDialog.Builder displayAPPUpdateDialogue(final Context ctx) {
        if (ctx instanceof Application) {
            Log.i("Context:", ctx.getClass().getSimpleName() + " is instance of application! ");
            return null;
        } else if (ctx instanceof Activity) {
            Log.i("Context:", ctx.getClass().getSimpleName() + " is instance of activity ");
            alertDialogBuilder = new AlertDialog.Builder(ctx);
            alertDialogBuilder.setCancelable(false);
            //alertDialogBuilder.setTitle("New updates are available!");
            /*alertDialogBuilder.setPositiveButton("Update!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(ctx, "Updating...", Toast.LENGTH_SHORT).show();
                    final String appPackageName = ctx.getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    ((Activity) ctx).finish();
                }
            });
            alertDialogBuilder.setNegativeButton("Remind later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //System.exit(0);
                    ApplineCustomerApplication ACA = (ApplineCustomerApplication) ctx.getApplicationContext();
                    ACA.setAskedForAppUpdate(true);

                }
            });*/
            //((Activity) ctx).runOnUiThread(new Runnable() {
              /*  @Override
                public void run() {*/
                    try {
                       // updatealertDialog = alertDialogBuilder.create();
                        //updatealertDialog = updatealertDialog == null ? al+ertDialogBuilder.create() : updatealertDialog;
                        return alertDialogBuilder;
                        /*if (!updatealertDialog.isShowing()) {

                            updatealertDialog.show();

                        }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                //}
           // });
           // return true;

        } else {
            Log.i("Context:", ctx.getClass().getSimpleName() + " is instance of dont know");
            return null;
        }
    }



    private static String getPostParamString(Hashtable<String, String> params) {
        if(params.size() == 0)
            return "";

        StringBuffer buf = new StringBuffer();
        Enumeration<String> keys = params.keys();
        while(keys.hasMoreElements()) {
            buf.append(buf.length() == 0 ? "" : "&");
            String key = keys.nextElement();
            buf.append(key).append("=").append(params.get(key));
        }
        return buf.toString();
    }



}