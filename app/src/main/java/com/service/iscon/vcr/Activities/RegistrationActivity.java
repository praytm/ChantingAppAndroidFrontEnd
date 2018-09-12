package com.service.iscon.vcr.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.service.iscon.vcr.Constants.ValidationPatternConstants;
import com.service.iscon.vcr.Controller.UserInfoController;
import com.service.iscon.vcr.Handler.MyDBHelper;
import com.service.iscon.vcr.Helper.AsyncProcessListener;
import com.service.iscon.vcr.Model.UserInfo;
import com.service.iscon.vcr.R;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class RegistrationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText etName, etMobile, etPassword;
    TextView tvGoogleauthtoken;
    Button btnSignUp;
    private AutoCompleteTextView mEmailView;
    private static final int REQUEST_READ_CONTACTS = 0;
    private View mProgressView;
    private View mLoginFormView;
    private ImageView imgLogoreg;
    private TextView tv_profilepic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initView();


        //populateAutoComplete();
    }

    private void initView() {
        imgLogoreg = (ImageView) findViewById(R.id.imgLogoreg);
        etName = (EditText) findViewById(R.id.et_reg_name);
        etMobile = (EditText) findViewById(R.id.et_reg_mobile);
        etPassword = (EditText) findViewById(R.id.et_reg_password);
        tvGoogleauthtoken = (TextView) findViewById(R.id.tv_google_auth_token);
        btnSignUp = (Button) findViewById(R.id.btn_reg_sign_up);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.et_reg_email);
        tv_profilepic = (TextView) findViewById(R.id.tv_profilepic);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.registration || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });
        mLoginFormView = findViewById(R.id.registration_form);
        mProgressView = findViewById(R.id.registration_progress);
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(getApplicationContext(), R.drawable.ic_filter_vintage_black_24dp);
        imgLogoreg.setImageDrawable(drawable);
        /*
        Bundle bundleData =  getIntent().getExtras();
        if(bundleData != null){
            String Name = bundleData.getString("Name");
            String email = bundleData.getString("email");
            String personPhotoUrl = bundleData.getString("personPhotoUrl");
            String accessToken = bundleData.getString("accessToken");
            etName.setText(Name);
            mEmailView.setText(email);
            tvGoogleauthtoken.setText(accessToken);
            tv_profilepic.setText(personPhotoUrl);
        }*/
    }

    private void attemptRegistration() {
// Reset errors.
        mEmailView.setError(null);
        etPassword.setError(null);
        etName.setError(null);
        etMobile.setError(null);
        tvGoogleauthtoken.setError(null);
        tv_profilepic.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = etPassword.getText().toString();
        String name = etName.getText().toString();
        String mobile = etMobile.getText().toString();
        final String googleToken = tvGoogleauthtoken.getText().toString();
        String profPic = tv_profilepic.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid name, if the user entered one.
        if (!TextUtils.isEmpty(name) && !isNameValid(name)) {
            etName.setError(getString(R.string.error_invalid_name));
            focusView = etName;
            cancel = true;
        }

        if(mobile.length() < 10 || mobile.length() > 10)
        {
        // Check for a valid Mobile, if the user entered one.
       // if (!TextUtils.isEmpty(mobile) && !isMobileValid(mobile) ) {
            etMobile.setError(getString(R.string.error_invalid_name));
            focusView = etMobile;
            cancel = true;
        }


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            focusView = etPassword;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            System.out.println("data from-----"+name+","+mobile+","+email+","+password+","+googleToken+""+profPic);
            UserInfoController UIC = UserInfoController.RegisterUser(RegistrationActivity.this,new UserInfo(name,mobile,email,password,googleToken,profPic),"false");
            if (UIC == null) {
                Log.e("Error", "UIC is null");
                return;
            }
            //Handling Response of service
            UIC.setOnUserRegistration(new AsyncProcessListener<Object>() {
                @Override
                public void ProcessFinished(Object Result) {
                    UserInfo AuthenticatedUser = (UserInfo) Result;
                    MyDBHelper db = new MyDBHelper(RegistrationActivity.this);
                    db.clearUser();
                    db.addUser(AuthenticatedUser);
                    showProgress(false);
                    Toast.makeText(RegistrationActivity.this,"Welcome "+AuthenticatedUser.getFullName().toString(),Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(RegistrationActivity.this,HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    // If User comUp with GooglePlus SignIn
                    /*
                    if(!googleToken.equals("0000000000")){
                        SharedPrefs.save(RegistrationActivity.this,googleToken,SharedPrefs.accessTokenKey);
                    }*/

                    finish();

                }

                @Override
                public void ProcessFailed(Exception E) { //Unsuccess Registration
                    String Resp = E.getMessage();
                    Toast.makeText(RegistrationActivity.this,Resp,Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            });
        }
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /*private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }*/


    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), RegistrationActivity.ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(RegistrationActivity.ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegistrationActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        //   mEmailView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isNameValid(String name) {
        //TODO: Replace this with your own logic
        return name.matches(ValidationPatternConstants.UserFullNameValidationFind);
    }

    private boolean isMobileValid(String mobile) {
        //TODO: Replace this with your own logic
        return mobile.matches(ValidationPatternConstants.UserMobileValidationPattern);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
