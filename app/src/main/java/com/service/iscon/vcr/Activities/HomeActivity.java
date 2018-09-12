package com.service.iscon.vcr.Activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.service.iscon.vcr.Constants.WebServiceConstants;
import com.service.iscon.vcr.Controller.UserInfoController;
import com.service.iscon.vcr.Handler.MyDBHelper;
import com.service.iscon.vcr.Helper.AsyncProcessListener;
import com.service.iscon.vcr.Model.SessionModel;
import com.service.iscon.vcr.Model.UserInfo;
import com.service.iscon.vcr.Model.UserStatistics;
import com.service.iscon.vcr.R;
import com.service.iscon.vcr.Reminder.Notification_reciver;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    TextView nav_name, nav_total_round, nav_last_login;
    TextView tv_total_user, tv_total_active_user, tv_todays_beads, tv_todays_round, tv_todays_count,tv_todays_quote;
    SessionModel currentSession;
    LinearLayout ll_chanting;
    ImageView btn_add_count,btn_chant_sound,imageViewProfile;
    Button btn_start_finish_chant;
    int currentCount = 0;
    UserStatistics US=new UserStatistics();
    static MediaPlayer mPlayer;
    boolean userActive=false;
    String compareDate;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String currentDate,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        nav_name = (TextView) header.findViewById(R.id.nav_name);
        nav_total_round = (TextView) header.findViewById(R.id.nav_total_round);
        nav_last_login = (TextView) header.findViewById(R.id.nav_last_login);

        tv_total_user = (TextView) findViewById(R.id.total_user);
        tv_todays_quote = (TextView) findViewById(R.id.todays_quote);
        tv_total_active_user = (TextView) findViewById(R.id.total_active_user);
        tv_todays_beads = (TextView) findViewById(R.id.todays_beads);
        tv_todays_round = (TextView) findViewById(R.id.todays_round);
        tv_todays_count = (TextView) findViewById(R.id.todays_count);
        ll_chanting = (LinearLayout) findViewById(R.id.ll_chanting);
        // ll_quote= (LinearLayout) findViewById(R.id.ll_quote);
        //   ll_quote.setVisibility(View.GONE);
        ll_chanting.setVisibility(View.GONE);
        imageViewProfile  = (ImageView) findViewById(R.id.imageView);

//        Uri uri=Uri.parse(LoginActivity.ProfileImage);
        //      imageViewProfile.setImageURI(uri);
        btn_start_finish_chant = (Button) findViewById(R.id.btn_start_finish_chant);
        btn_start_finish_chant.setOnClickListener(this);
        //btn_add_count=(ImageView)findViewById(R.id.btn_add_count);
        //btn_add_count.setOnClickListener(this);
        btn_chant_sound=(ImageView)findViewById(R.id.btn_chant_sound);
        btn_chant_sound.setOnClickListener(this);

        // Set Image
        /*Drawable drawable = AppCompatDrawableManager.get().getDrawable(getApplicationContext(), R.drawable.ic_add_circle_blue_24dp);
        btn_add_count.setImageDrawable(drawable);*/

        // Set Image
        Drawable drawable1 = AppCompatDrawableManager.get().getDrawable(getApplicationContext(), R.drawable.ic_volume_up_24dp);
        btn_chant_sound.setImageDrawable(drawable1);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        MyDBHelper db = new MyDBHelper(HomeActivity.this);
        UserInfo mUserInfo = db.getUserInfo();
        if (mUserInfo != null) {
            if (mUserInfo.getFullName() != null)
                nav_name.setText(mUserInfo.getFullName());
            if (mUserInfo.getLastLogin() != null)
                nav_last_login.setText("Last Login: " + mUserInfo.getLastLogin().replace("T", " at "));
            System.out.println("url is" + mUserInfo.getProfilePic());
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
                    // Toast.makeText(HomeActivity.this, "Welcome :" + AuthenticatedUser.getDailyQuote().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("dates are not equal", "" + AuthenticatedUser.getDailyQuote().toString());
                    tv_todays_quote.setText("Quote of the Day \n" + AuthenticatedUser.getDailyQuote().toString());
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
                tv_todays_quote.setVisibility(View.GONE);
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
                        tv_todays_quote.setVisibility(View.VISIBLE);
                        UserInfo AuthenticatedUser = (UserInfo) Result;
                        //Toast.makeText(HomeActivity.this, "Welcome :" + AuthenticatedUser.getDailyQuote().toString(), Toast.LENGTH_SHORT).show();
                        Log.d("dates are not equal", "" + AuthenticatedUser.getDailyQuote().toString());
                        tv_todays_quote.setText(AuthenticatedUser.getDailyQuote().toString());

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
        showDialogForChantedTargetCount();
    }

    // Showing that Total chant and Targeted Chant Count
    private void showDialogForChantedTargetCount(){

        MyDBHelper db = new MyDBHelper(HomeActivity.this);
        final UserInfo mUserInfo = db.getUserInfo();

        final UserInfoController UIC = UserInfoController.getTotalBeadsForToday(HomeActivity.this, mUserInfo);
        final UserInfoController UIC1 = UserInfoController.getDailyTargetCount(HomeActivity.this, mUserInfo);
        if (UIC == null || UIC1 == null) {
            Log.e("Error", "UIC is null");
            return;
        }
        //Handling Response of service
        UIC.setOnActivateDaily(new AsyncProcessListener<Object>() {
            @Override
            public void ProcessFinished(final Object Result) {
                // Toast.makeText(HomeActivity.this, Result.toString(), Toast.LENGTH_SHORT).show();
                //WebServiceConstants.totalTodaysBeadsCount = Result.toString();
                //Handling Response of service
                UIC1.setOnActivateDaily(new AsyncProcessListener<Object>() {
                    @Override
                    public void ProcessFinished(Object Result1) {
                        // Toast.makeText(HomeActivity.this, Result.toString(), Toast.LENGTH_SHORT).show();
                        //WebServiceConstants.DailyTarget = Result1.toString();
                        // custom dialog
                        final Dialog dialog = new Dialog(HomeActivity.this);
                        dialog.setContentView(R.layout.targatedchantcount_layout);

                        // set the Rounds Count
                        TextView text = (TextView) dialog.findViewById(R.id.tvRoundCount);
                        int roundsCount = ConvertIntoNumeric(WebServiceConstants.totalTodaysBeadsCount);
                        //text.setText(WebServiceConstants.totalTodaysBeadsCount);
                        text.setText(""+roundsCount/108);

                        // set the Target Count
                        TextView text1 = (TextView) dialog.findViewById(R.id.tvTargetRoundsCount);
                        int targetCount = ConvertIntoNumeric(WebServiceConstants.DailyTarget);
                        text1.setText(""+targetCount);
                        //text1.setText(WebServiceConstants.DailyTarget);

                        ImageView image = (ImageView) dialog.findViewById(R.id.imageView2);
                        image.setImageResource(R.drawable.chant);

                        @SuppressLint("WrongViewCast") Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                    }

                    @Override
                    public void ProcessFailed(Exception E) {
                        String Resp = E.getMessage();
                        Toast.makeText(HomeActivity.this, Resp, Toast.LENGTH_SHORT).show();
                    }
                });
                // custom dialog
                /*final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.targatedchantcount_layout);
                dialog.setTitle("Chant Holy Name...");

                // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.textView2);
                text.setText(Result.toString());
                ImageView image = (ImageView) dialog.findViewById(R.id.imageView2);
                image.setImageResource(R.drawable.holy_festival);

                @SuppressLint("WrongViewCast") Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();*/

            }

            @Override
            public void ProcessFailed(Exception E) {
                String Resp = E.getMessage();
                Toast.makeText(HomeActivity.this, Resp, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //convert the string to number and habndle exception
    private int ConvertIntoNumeric(String xVal)
    {
        try
        {
            return (int) Float.parseFloat(xVal);
        }
        catch(Exception ex)
        {
            return 0;
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
            alertDialog.setIcon(R.mipmap.ic_launcher);

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // User pressed YES button. Write Logic Here
                    Toast.makeText(getApplicationContext(), "Hare Krishna!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // User pressed No button. Write Logic Here
                    //Toast.makeText(getApplicationContext(), "Hare Krishna Welcome To PrayTM", Toast.LENGTH_SHORT).show();
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

            final Intent i2 = new Intent(this, LoginActivity.class);

            //clearing local storage
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle("Logout");

            // Setting Dialog Message
            alertDialog.setMessage("Do you want to logout from PrayTM?");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.mipmap.ic_launcher);

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // User pressed YES button. Write Logic Here
                    Toast.makeText(getApplicationContext(), "Hare Krishna!", Toast.LENGTH_SHORT).show();
                    // Showing Alert Message

                    userActive=false;
                    deactivateUser();
                    MyDBHelper db = new MyDBHelper(HomeActivity.this);
                    db.clearUser();
                    startActivity(i2);
                    finish();
                }
            });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // User pressed No button. Write Logic Here
                    //Toast.makeText(getApplicationContext(), "Hare Krishna Welcome To PrayTM", Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.show();

        }else if (id == R.id.nav_profile) {
            i = new Intent(this, MyProfileActivity.class);
            startActivity(i);
        }else if(id == R.id.nav_rating){
            showDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
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

                btn_start_finish_chant.setText("Start Chant");
                currentCount = 0;
                getRefreshedData(false);
            }

            @Override
            public void ProcessFailed(Exception E) { //Chanting Session Not Saved
                String Resp = E.getMessage();
                Toast.makeText(HomeActivity.this, Resp, Toast.LENGTH_SHORT).show();
                btn_start_finish_chant.setText("Start Chant");
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
        tv_total_user.setText("" + mUS.getTotalUser());
        tv_total_active_user.setText("" + mUS.getTotalActiveUser());
        tv_todays_round.setText("" + (mUS.getTodaysBeads() / 108));
        tv_todays_beads.setText("" + (mUS.getTodaysBeads()));
        nav_total_round.setText("Total Rounds : " + (mUS.getTotalBeads() / 108));
    }

    @Override
    public void onClick(View v) {
        tv_todays_quote.setVisibility(View.GONE);
        //  ll_quote.setVisibility(View.GONE);

        switch (v.getId()){
            case R.id.btn_start_finish_chant:

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Calendar c;
                if (btn_start_finish_chant.getText().equals("Start Chant")) {
                    btn_start_finish_chant.setText("Finish Chant");
                    activateUser();
                    ll_chanting.setVisibility(View.VISIBLE);
                    c = Calendar.getInstance();
                    String sessiondate = format.format(c.getTime());
                    sessiondate=sessiondate.replace(" ","T");
                    currentSession=new SessionModel();
                    currentSession.setDate(sessiondate);
                    currentSession.setStartTime(sessiondate);
                    tv_todays_count.setText(""+currentCount);

                } else if (btn_start_finish_chant.getText().equals("Finish Chant")) {
                    ll_chanting.setVisibility(View.GONE);

                    if (mPlayer != null && mPlayer.isPlaying()) {
                        //Set button background image as 'Play'
                        btn_chant_sound.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_volume_up_24dp));

                        //Stop playing om.mp3
                        mPlayer.stop();
                    }

                    if(currentCount>0) {
                        btn_start_finish_chant.setText("Submitting count..");
                        c = Calendar.getInstance();
                        String sessiondate = format.format(c.getTime());
                        sessiondate = sessiondate.replace(" ", "T");
                        currentSession.setEndTime(sessiondate);
                        currentSession.setBeads(currentCount);
                        saveChantSession();
                    }else{
                        btn_start_finish_chant.setText("Start Chant");
                    }
                    //  mPlayer.stop();
                }
                break;

         /*   case R.id.btn_add_count:
               increaseChantCount(btn_add_count);
                break;*/

            case R.id.btn_chant_sound:
                try {
                    //When Om chant is not playing
                    if (mPlayer==null || !mPlayer.isPlaying()) {
                        //Set button background image as 'Pause'
                        btn_chant_sound.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_volume_off_24dp));
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
                            btn_chant_sound.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_volume_up_24dp));
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
                break;
        }

    }

    public void increaseChantCount(View v) {
        currentCount++;
        tv_todays_count.setText(""+currentCount);
        int beadint=(currentCount)+(US.getTodaysBeads());

        tv_todays_beads.setText("" + beadint);
        Log.d("beads count: ", "" + beadint);
    }

    private void showQuote(UserInfo authenticatedUser) {

        Log.d("daily quote","" + authenticatedUser.getDailyQuote());

    }

    public void activateUser() {

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

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer != null && mPlayer.isPlaying()) {
            //Set button background image as 'Play'
            btn_chant_sound.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_volume_up_24dp));

            //Stop playing om.mp3
            mPlayer.stop();
        }
    }
}