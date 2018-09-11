package com.service.iscon.vcr.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.service.iscon.vcr.controller.UserInfoController;
import com.service.iscon.vcr.db.DatabaseManager;
import com.service.iscon.vcr.db.QueryExecutor;
import com.service.iscon.vcr.handler.MyDBHelper;
import com.service.iscon.vcr.helper.AsyncProcessListener;
import com.service.iscon.vcr.model.SessionModel;
import com.service.iscon.vcr.model.UserInfo;
import com.service.iscon.vcr.model.UserStatistics;
import com.service.iscon.vcr.R;
import com.service.iscon.vcr.reminder.Notification_reciver;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    TextView mNavigation_Name, mTotalChantRounds, mLastLogin;
    SessionModel currentSession;

    int currentCount = 0;
    UserStatistics US = new UserStatistics();
    static MediaPlayer mPlayer;
    boolean userActive=false;
    String compareDate;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String currentDate,date;


    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindView(R.id.total_user)
    TextView mTotalUser;

    @BindView(R.id.todays_quote)
    TextView mTodaysQuote;

    @BindView(R.id.total_active_user)
    TextView mTotalActiveUser;

    @BindView(R.id.todays_beads)
    TextView mTodaysBeads;

    @BindView(R.id.todays_round)
    TextView mTodaysRound;

    @BindView(R.id.todays_count)
    TextView mTodaysCount;

    @BindView(R.id.ll_chanting)
    LinearLayout ll_chanting;

    @BindView(R.id.btn_start_finish_chant)
    Button mBtnStartStopChant;

    @BindView(R.id.btn_add_count)
    ImageView mAddChantCount;

    @BindView(R.id.btn_chant_sound)
    ImageView mChantSound;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mNavigationView.setNavigationItemSelectedListener(this);
        View header = mNavigationView.getHeaderView(0);

        mNavigation_Name = (TextView) header.findViewById(R.id.nav_name);
        mTotalChantRounds = (TextView) header.findViewById(R.id.nav_total_round);
        mLastLogin = (TextView) header.findViewById(R.id.nav_last_login);

        mBtnStartStopChant.setOnClickListener(this);
        mAddChantCount.setOnClickListener(this);
        mChantSound.setOnClickListener(this);

        mAddChantCount.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this, R.drawable.ic_add_circle_blue_24dp));
        mChantSound.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this, R.drawable.ic_volume_up_24dp));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        //TODO Change My DB HELPER
        MyDBHelper db = new MyDBHelper(HomeActivity.this);
        UserInfo mUserInfo = db.getUserInfo();
        if (mUserInfo != null) {
            if (mUserInfo.getFullName() != null)
                mNavigation_Name.setText(mUserInfo.getFullName());
            if (mUserInfo.getLastLogin() != null)
                mLastLogin.setText("Last Login: " + mUserInfo.getLastLogin().replace("T", " at "));
        }

        getRefreshedData(true);

        // UserInfo mUserInfo = db.getUserInfo();
        //Daily quote
        Calendar c= Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        currentDate= df.format(c.getTime());
        //  String currentDate= "31-7-2017";

        Log.d("curr date",""+currentDate);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        editor.putString("currentDate----", currentDate);
        String newdate=preferences.getString("newcompareDate","");
        if(newdate.equalsIgnoreCase("")) {
            editor.putString("compareDate", compareDate);
        }else{
            editor.putString("compareDate", newdate);

        }

        editor.apply();


        date = preferences.getString("compareDate", "");
        Log.d("comparedate-- date",""+date);

        if(date.equalsIgnoreCase("")){
            UserInfoController UIC = UserInfoController.GetDailyQuote(HomeActivity.this, mUserInfo);
            if (UIC == null) {
                Log.e("Error", "UIC is null");
                return;
            }
            //Handling Response of service
            UIC.setOnActivateDaily(new AsyncProcessListener<Object>() {
                @Override
                public void ProcessFinished(Object Result) {
                    // Toast.makeText(HomeActivity.this, Result.toString(), Toast.LENGTH_SHORT).show();

                    UserInfo AuthenticatedUser = (UserInfo) Result;
                    Toast.makeText(HomeActivity.this, "Welcome :" + AuthenticatedUser.getDailyQuote().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("dates are not equal", "" + AuthenticatedUser.getDailyQuote().toString());
                    mTodaysQuote.setText("Quote of the Day \n" + AuthenticatedUser.getDailyQuote().toString());
                    compareDate=currentDate;
                    editor.putString("newcompareDate", compareDate);
                    editor.apply();
                    showQuote(AuthenticatedUser);

                }

                @Override
                public void ProcessFailed(Exception E) {
                    String Resp = E.getMessage();
                    Toast.makeText(HomeActivity.this, Resp, Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(!date.equalsIgnoreCase(""))
        {
            if(currentDate.equalsIgnoreCase(date)){
                Log.d("both dates are equal","");
                mTodaysQuote.setVisibility(View.GONE);
            }
            else if(!currentDate.equalsIgnoreCase(date)){
                UserInfoController UIC = UserInfoController.GetDailyQuote(HomeActivity.this, mUserInfo);
                if (UIC == null) {
                    Log.e("Error", "UIC is null");
                    return;
                }
                //Handling Response of service
                UIC.setOnActivateDaily(new AsyncProcessListener<Object>() {
                    @Override
                    public void ProcessFinished(Object Result) {
                        // Toast.makeText(HomeActivity.this, Result.toString(), Toast.LENGTH_SHORT).show();
                        mTodaysQuote.setVisibility(View.VISIBLE);
                        UserInfo AuthenticatedUser = (UserInfo) Result;
                        Toast.makeText(HomeActivity.this, "Welcome :" + AuthenticatedUser.getDailyQuote().toString(), Toast.LENGTH_SHORT).show();
                        Log.d("dates are not equal", "" + AuthenticatedUser.getDailyQuote().toString());
                        mTodaysQuote.setText(AuthenticatedUser.getDailyQuote().toString());

                        showQuote(AuthenticatedUser);

                    }

                    @Override
                    public void ProcessFailed(Exception E) {
                        String Resp = E.getMessage();
                        Toast.makeText(HomeActivity.this, Resp, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle("Exit");

            // Setting Dialog Message
            alertDialog.setMessage("Do you want to exit from PrayTM ?");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.mipmap.ic_praytm);

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // User pressed YES button. Write Logic Here
                    Toast.makeText(getApplicationContext(), "Thank You Hare Krishna", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // User pressed No button. Write Logic Here
                    Toast.makeText(getApplicationContext(), "Hare Krishna Welcome To PrayTM ", Toast.LENGTH_SHORT).show();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }
    }

    private void showDialog() {

        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(3)
                .ratingBarColor(R.color.yellow)
                .onRatingBarFormSumbit(new RatingDialog.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                    }
                }).build();

        ratingDialog.show();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent i = new Intent();

        //creating fragment object
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            // Handle the camera action
            //i = new Intent(this, RegistrationActivity.class);
            //startActivity(i);
          /*  fragment = new HomeFragment();
            //replacing the fragment
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
*/
        } else if (id == R.id.nav_history) {
            i = new Intent(this, SessionHistoryActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_about_us) {
            i = new Intent(this, AboutActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_setting) {
            // i = new Intent(this, SettingsActivity.class);startActivity(i);
            // Reminder Set for selected Time For day and repeating every day on same Time
            reminderLogic();
        } else if (id == R.id.nav_sign_out) {
            //clearing local storage
            userActive=false;
            deactivateUser();
            MyDBHelper db = new MyDBHelper(this);
            db.clearUser();
            i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();

        }else if (id == R.id.nav_profile) {
            i = new Intent(this, MyProfileActivity.class);
            startActivity(i);
        }else if(id == R.id.nav_rating){
            showDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void saveChantSession() {

        MyDBHelper db = new MyDBHelper(HomeActivity.this);
        UserInfo mUserInfo = db.getUserInfo();

        UserInfoController UIC = UserInfoController.sendBeads(HomeActivity.this, mUserInfo, currentSession);
        if (UIC == null) {
            Log.e("Error", "UIC is null");
            return;
        }
        //Handling Response of service
        UIC.setOnSendBeads(new AsyncProcessListener<Object>() {
            @Override
            public void ProcessFinished(Object Result) {
                Toast.makeText(HomeActivity.this, "Chanting Saved.", Toast.LENGTH_SHORT).show();

                mBtnStartStopChant.setText("Start Chant");
                currentCount = 0;
                getRefreshedData(false);
            }

            @Override
            public void ProcessFailed(Exception E) { //Chanting Session Not Saved
                String Resp = E.getMessage();
                Toast.makeText(HomeActivity.this, Resp, Toast.LENGTH_SHORT).show();
                mBtnStartStopChant.setText("Start Chant");
                currentCount = 0;
                refreshUI(US);
            }

        });
    }

    public void getRefreshedData(Boolean showProgrss) {

        MyDBHelper db = new MyDBHelper(HomeActivity.this);
        UserInfo mUserInfo = db.getUserInfo();

        UserInfoController UIC = UserInfoController.RefreshHome(HomeActivity.this, mUserInfo, showProgrss);
        if (UIC == null) {
            Log.e("Error", "UIC is null");
            return;
        }
        //Handling Response of service
        UIC.setOnRefreshHome(new AsyncProcessListener<Object>() {
            @Override
            public void ProcessFinished(Object Result) {
                US = (UserStatistics) Result;
                refreshUI(US);
                //Toast.makeText(HomeActivity.this, "Page Refreshed.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void ProcessFailed(Exception E) { //Chanting Session Not Saved
                String Resp = E.getMessage();
                Toast.makeText(HomeActivity.this, Resp, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshUI(UserStatistics mUS) {
        mTotalUser.setText("" + mUS.getTotalUser());
        mTotalActiveUser.setText("" + mUS.getTotalActiveUser());
        mTodaysRound.setText("" + (mUS.getTodaysBeads() / 108));
        mTodaysBeads.setText("" + (mUS.getTodaysBeads()));
        mTotalChantRounds.setText("Total Rounds : " + (mUS.getTotalBeads() / 108));
    }

    @Override
    public void onClick(View v) {
        mTodaysQuote.setVisibility(View.GONE);
      //  ll_quote.setVisibility(View.GONE);

        if (v == mBtnStartStopChant) {
            MyDBHelper db = new MyDBHelper(HomeActivity.this);


            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Calendar c;
            if (mBtnStartStopChant.getText().equals("Start Chant")) {
                mBtnStartStopChant.setText("Finish Chant");
                activateUser();
                ll_chanting.setVisibility(View.VISIBLE);
                c = Calendar.getInstance();
                String sessiondate = format.format(c.getTime());
                sessiondate=sessiondate.replace(" ","T");
                currentSession=new SessionModel();
                currentSession.setDate(sessiondate);
                currentSession.setStartTime(sessiondate);
                mTodaysCount.setText(""+currentCount);

            } else if (mBtnStartStopChant.getText().equals("Finish Chant")) {
                ll_chanting.setVisibility(View.GONE);
                mBtnStartStopChant.setText("Submitting count..");
                c = Calendar.getInstance();
                String sessiondate = format.format(c.getTime());
                sessiondate=sessiondate.replace(" ","T");
                currentSession.setEndTime(sessiondate);
                currentSession.setBeads(currentCount);
                saveChantSession();
                //  mPlayer.stop();
            }
        }else if(v== mAddChantCount){
            currentCount++;
            mTodaysCount.setText(""+currentCount);
            int beadint=(currentCount)+(US.getTodaysBeads());

            mTodaysBeads.setText("" + beadint);
            Log.d("beads count: ", "" + beadint);

        }else if(v== mChantSound){
            try {
                //When Om chant is not playing
                if (mPlayer==null || !mPlayer.isPlaying()) {
                    //Set button background image as 'Pause'
                    mChantSound.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_volume_off_24dp));
                    //Create MediaPlayer object with raw resource 'om.mp3'
                    mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.srila_prabhupada_japa);
                    //Start playing mp3
                    mPlayer.start();
                    //Set player to be looping to continuously play om.mp3
                    mPlayer.setLooping(true);
                } else {
                    //When Om chant is playing
                    if (mPlayer != null && mPlayer.isPlaying()) {
                        //Set button background image as 'Play'
                        mChantSound.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_volume_up_24dp));
                        //Stop playing om.mp3
                        mPlayer.stop();
                    }
                }
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                //  Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                //    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showQuote(UserInfo authenticatedUser) {

        Log.d("daily quote","" + authenticatedUser.getDailyQuote());

    }

    public void activateUser() {
        //DatabaseManager manager= DatabaseManager.getInstance();
        //SQLiteDatabase db=manager.openDatabase();
        //QueryExecutor qe = new QueryExecutor();

        MyDBHelper db = new MyDBHelper(HomeActivity.this);
        UserInfo mUserInfo = db.getUserInfo();

        UserInfoController UIC = UserInfoController.ActivateUser(HomeActivity.this, mUserInfo);
        if (UIC == null) {
            Log.e("Error", "UIC is null");
            return;
        }
        //Handling Response of service
        UIC.setOnActivateUser(new AsyncProcessListener<Object>() {
            @Override
            public void ProcessFinished(Object Result) {
                //Toast.makeText(HomeActivity.this, "User Activated.", Toast.LENGTH_SHORT).show();
                userActive=true;
                getRefreshedData(false);
            }

            @Override
            public void ProcessFailed(Exception E) {
                String Resp = E.getMessage();
                Toast.makeText(HomeActivity.this, Resp, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deactivateUser() {

        MyDBHelper db = new MyDBHelper(HomeActivity.this);
        UserInfo mUserInfo = db.getUserInfo();

        if(mUserInfo==null){
            return;
        }

        UserInfoController UIC = UserInfoController.DeactivateUser(HomeActivity.this, mUserInfo);
        if (UIC == null) {
            Log.e("Error", "UIC is null");
            return;
        }
        //Handling Response of service
        /*UIC.setOnDeActivateUser(new AsyncProcessListener<Object>() {
            @Override
            public void ProcessFinished(Object Result) {
                Toast.makeText(HomeActivity.this, "User Deactivated.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void ProcessFailed(Exception E) {
                String Resp = E.getMessage();
                Toast.makeText(HomeActivity.this, Resp, Toast.LENGTH_SHORT).show();
            }
        });*/
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(userActive)
            deactivateUser();
    }

    public void reminderLogic(){
        final Calendar c = Calendar.getInstance();
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute1 = c.get(Calendar.MINUTE);

        TimePickerDialog timepick = new TimePickerDialog(HomeActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                //stime.setText(hourOfDay + ":" +minute);

                Calendar calender = Calendar.getInstance();

                calender.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calender.set(Calendar.MINUTE,minute);
                calender.set(Calendar.SECOND,0);

                Intent intent = new Intent(getApplicationContext(),Notification_reciver.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),100,
                        intent,PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calender.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);

                Toast.makeText(getApplicationContext(),"Chant Reminder Set",Toast.LENGTH_LONG).show();

            }
        },hour,minute1,false);
        timepick.setTitle("Select Time");
        timepick.show();
    }
}