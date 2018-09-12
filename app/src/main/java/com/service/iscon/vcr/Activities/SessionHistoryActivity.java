package com.service.iscon.vcr.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.service.iscon.vcr.Adapter.SessionHistoryAdapter;
import com.service.iscon.vcr.Controller.SessionController;
import com.service.iscon.vcr.Handler.MyDBHelper;
import com.service.iscon.vcr.Helper.AsyncProcessListener;
import com.service.iscon.vcr.Helper.Comparator.SessionHistoryComapator;
import com.service.iscon.vcr.Model.SessionModel;
import com.service.iscon.vcr.Model.UserInfo;
import com.service.iscon.vcr.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SessionHistoryActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1122;
    private List<SessionModel> sessionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SessionHistoryAdapter mAdapter;
    public Context con;
    ImageButton ibAddChant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        con=this;
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        ibAddChant=(ImageButton)findViewById(R.id.ib_add_chant);
        ibAddChant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SessionHistoryActivity.this,OfflineChantActivity.class);
                startActivityForResult(i,REQUEST_CODE);
            }
        });
        LoadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode==REQUEST_CODE && resultCode== RESULT_OK){
                LoadData();
            }
    }

    private void LoadData() {

        MyDBHelper db = new MyDBHelper(SessionHistoryActivity.this);
        UserInfo _u = db.getUserInfo();

        SessionController SC = SessionController.UserSessionHistory(SessionHistoryActivity.this,_u.getEmail(), _u.getPassword());
        if (SC == null) {
            Log.e("Error", "UIC is null");
            return;
        }
        //Handling Response of service
        SC.setOnSessionHistory(new AsyncProcessListener<Object>() {
            @Override
            public void ProcessFinished(Object Result) {
                sessionList= (ArrayList<SessionModel>) Result;
                if(sessionList.size()>0){
                    //  Toast.makeText(SessionHistoryActivity.this,"SH size "+sessionList.size(),Toast.LENGTH_SHORT).show();
                    //mAdapter.notifyDataSetChanged();
                    Collections.sort(sessionList,new SessionHistoryComapator());
                    Collections.reverse(sessionList);
                    mAdapter = new SessionHistoryAdapter(con,sessionList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    //recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                    // UpdateUI();
                }else{
                    Toast.makeText(SessionHistoryActivity.this, "No chanting history available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override

            public void ProcessFailed(Exception E) { //Request Not Success
                String Resp = E.getMessage();
                Toast.makeText(SessionHistoryActivity.this,Resp,Toast.LENGTH_SHORT).show();
            }
        });
    }


}