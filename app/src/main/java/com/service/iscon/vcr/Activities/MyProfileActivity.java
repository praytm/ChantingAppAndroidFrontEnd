package com.service.iscon.vcr.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.service.iscon.vcr.handler.MyDBHelper;
import com.service.iscon.vcr.model.UserInfo;
import com.service.iscon.vcr.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyProfileActivity extends AppCompatActivity {

    @BindView(R.id.et_email)
    EditText mEmail;

    @BindView(R.id.et_full_name)
    EditText mFullName;

    @BindView(R.id.et_contact)
    EditText mContact;

    @BindView(R.id.et_city)
    EditText mCity;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ButterKnife.bind(this);

        MyDBHelper db = new MyDBHelper(this);
        UserInfo mUserInfo = db.getUserInfo();

        if(mUserInfo.getEmail()!=null){
            mEmail.setText(mUserInfo.getEmail());
        }
        if(mUserInfo.getFullName()!=null){
            mFullName.setText(mUserInfo.getFullName());
        }

        if(mUserInfo.getMobile()!=null){
            mContact.setText(mUserInfo.getMobile());
        }
        /*if(mUserInfo.getEmail()!=null){
            mCity.setText(mUserInfo.getEmail());
        }
*/
        mEmail.setEnabled(false);
        mFullName.setEnabled(false);
        mContact.setEnabled(false);
        mCity.setEnabled(false);

       // FloatingActionButton mFloatingActionButton = (FloatingActionButton) findViewById(R.id.mFloatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Contact us to update your details.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
