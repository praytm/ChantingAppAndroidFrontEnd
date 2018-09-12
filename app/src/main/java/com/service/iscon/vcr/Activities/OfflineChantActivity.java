package com.service.iscon.vcr.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.service.iscon.vcr.Controller.UserInfoController;
import com.service.iscon.vcr.Handler.MyDBHelper;
import com.service.iscon.vcr.Helper.AsyncProcessListener;
import com.service.iscon.vcr.Model.SessionModel;
import com.service.iscon.vcr.Model.UserInfo;
import com.service.iscon.vcr.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OfflineChantActivity extends AppCompatActivity {

    EditText et_rounds;
    SessionModel currentSession;
    TextView tv_chant_date;
    ImageButton cal_date;
    private static final int MAX_CHANT_COUNT = 225;
    SimpleDateFormat session_date_format, display_date_format;
    Calendar todaysDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_chant);
        et_rounds = (EditText) findViewById(R.id.et_no_of_rounds);
        tv_chant_date = (TextView) findViewById(R.id.tv_chant_date);
        cal_date = (ImageButton) findViewById(R.id.cal_date);

        currentSession = new SessionModel();
        session_date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        display_date_format = new SimpleDateFormat("dd-MM-yyyy");

        todaysDate = Calendar.getInstance();
        String sessiondate = session_date_format.format(todaysDate.getTime());
        tv_chant_date.setText(display_date_format.format(todaysDate.getTime()));
        sessiondate = sessiondate.replace(" ", "T");
        currentSession.setDate(sessiondate);
        currentSession.setStartTime(sessiondate);
        currentSession.setEndTime(sessiondate);
    }

    public void submitChant(View view) {
        if (!(et_rounds.getText().toString().length() > 0)) {
            Toast.makeText(OfflineChantActivity.this, "Enter Chant Count.", Toast.LENGTH_SHORT).show();
            return;
        }

        int rounds = Integer.parseInt(et_rounds.getText().toString());

        if (rounds <= 0 || rounds > MAX_CHANT_COUNT) {
            Toast.makeText(OfflineChantActivity.this, "Invalid Chant Count.", Toast.LENGTH_SHORT).show();
            return;
        }

        MyDBHelper db = new MyDBHelper(OfflineChantActivity.this);
        UserInfo mUserInfo = db.getUserInfo();
        currentSession.setBeads(rounds * 108);

        UserInfoController UIC = UserInfoController.sendBeads(OfflineChantActivity.this, mUserInfo, currentSession);
        if (UIC == null) {
            Log.e("Error", "UIC is null");
            return;
        }

        //Handling Response of service
        UIC.setOnSendBeads(new AsyncProcessListener<Object>() {
            @Override
            public void ProcessFinished(Object Result) {
                Toast.makeText(OfflineChantActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void ProcessFailed(Exception E) { //Chanting Session Not Saved
                String Resp = E.getMessage();
                Toast.makeText(OfflineChantActivity.this, Resp, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void pickDate(View view) {
        todaysDate = Calendar.getInstance();
        todaysDate.setTime(new Date());


        DatePickerDialog DPD = new DatePickerDialog(OfflineChantActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar pickedDate = Calendar.getInstance();
                pickedDate.set(year, monthOfYear, dayOfMonth);

                //Update Model
                String sessiondate = session_date_format.format(pickedDate.getTime());
                sessiondate = sessiondate.replace(" ", "T");
                currentSession.setDate(sessiondate);
                currentSession.setStartTime(sessiondate);
                currentSession.setEndTime(sessiondate);

                //Update TextViews
                tv_chant_date.setText(display_date_format.format(pickedDate.getTime()));

            }
        }, todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH), todaysDate.get(Calendar.DAY_OF_MONTH));
        try {

            //Min Date = 10 days before
            DPD.getDatePicker().setMinDate(new Date().getTime() - (1000 * 60 * 60 * 24 * 10));

            //Max Date = todays date
            DPD.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());

           /* Calendar MaxCal = Calendar.getInstance();
            MaxCal.set(Calendar.YEAR, MaxCal.get(Calendar.YEAR));
            DPD.getDatePicker().setMaxDate(MaxCal.getTime().getTime());*/

            DPD.show();
        } catch (Exception e) {
        }
    }

    public void addRounds(View view) {
        String rounds=et_rounds.getText().toString();
        if(!(rounds.length()>0)){
            et_rounds.setText("1");
        }else if(Integer.parseInt(rounds)!=MAX_CHANT_COUNT){
            et_rounds.setText(""+(Integer.parseInt(rounds)+1));
        }
    }

    public void removeRounds(View view) {
        String rounds=et_rounds.getText().toString();
        if(!(rounds.length()>0)){
            et_rounds.setText("0");
        }else if(Integer.parseInt(rounds)!=0){
            et_rounds.setText(""+(Integer.parseInt(rounds)-1));
        }
    }
}
