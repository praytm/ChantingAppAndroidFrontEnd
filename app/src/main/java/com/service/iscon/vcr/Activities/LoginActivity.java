package com.service.iscon.vcr.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.service.iscon.vcr.Controller.UserInfoController;
import com.service.iscon.vcr.Handler.MyDBHelper;
import com.service.iscon.vcr.Helper.AsyncProcessListener;
import com.service.iscon.vcr.Model.UserInfo;
import com.service.iscon.vcr.R;
import com.service.iscon.vcr.Utils.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/*import hp.harsh.library.interfaces.PermissionInterface;
import hp.harsh.library.manager.PermissionRequest;
import hp.harsh.library.manager.PermissionResponse;
import hp.harsh.library.utilbag.Permission;
import hp.harsh.library.utilbag.PermissionCode;*/

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>,
        GoogleApiClient.OnConnectionFailedListener{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    private static final int RC_SIGN_IN = 1234;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    GoogleApiClient mGoogleApiClient;

    /*sharedpreference values*/
    private String Useraccid, Useraccname, UseraccProfilepic, UseraccGender, Useraccemail;
    private String UseraccFacebookId, UseraccGooglePlusId, UseraccLinkedinId, UseraccTwitterId;
    public static String ProfileImage;

    //com.k2infosoft.social_login.GooglePlusButton _google_plus;

    //public static GooglePlusSignInHelper mGHelper;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyDBHelper db = new MyDBHelper(LoginActivity.this);
        UserInfo _u = db.getUserInfo();
        if (_u != null && !_u.getEmail().equals("")) {
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        } else {

            // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(LoginActivity.this.getResources().getString(R.string.server_client_id))
                    .requestEmail()
                    .build();

            // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this , this )
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();


            /*mGHelper = new GooglePlusSignInHelper(this, this);

            _google_plus = (com.k2infosoft.social_login.GooglePlusButton) findViewById(R.id.btn_google_plus);

            _google_plus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(LoginActivity.this, "Loin Done", Toast.LENGTH_SHORT).show();
                    mGHelper.performSignIn();
                }
            });

            if (Build.VERSION.SDK_INT >= 23) {
                checkPhoneStatePermission();
            }*/

            // Set Image
           // Drawable drawable = AppCompatDrawableManager.get().getDrawable(getApplicationContext(), R.drawable.ic_filter_vintage_black_24dp);
            ivLogo = (ImageView) findViewById(R.id.imgLogo);
           // ivLogo.setImageDrawable(drawable);

            SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_STANDARD);
            signInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });

            // Set up the login form.
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
            //populateAutoComplete();

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        showProgress(true);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /*private void fff() {
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
System.out.println("password :"+password);
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        /*if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
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
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            // mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);

            UserInfoController UIC = UserInfoController.AuthenticateUser(LoginActivity.this, email, password,false);
            if (UIC == null) {
                Log.e("Error", "UIC is null");
                return;
            }
            //Handling Response of service
            UIC.setOnUserAuthentication(new AsyncProcessListener<Object>() {
                @Override
                public void ProcessFinished(Object Result) {
                    UserInfo AuthenticatedUser = (UserInfo) Result;
                    MyDBHelper db = new MyDBHelper(LoginActivity.this);
                    UserInfo _u = db.getUserInfo();
                    db.clearUser();
                    db.addUser(AuthenticatedUser);


                    List<UserInfo> listUsers = new ArrayList();
                    listUsers = db.getAllContacts();
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, "Welcome " + AuthenticatedUser.getFullName().toString(), Toast.LENGTH_SHORT).show();
                    finish();
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                }

                @Override
                public void ProcessFailed(Exception E) { //Login Not Success
                    String Resp = E.getMessage();
                    Toast.makeText(LoginActivity.this, Resp, Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }

            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Client Connection Failed", Toast.LENGTH_SHORT).show();
    }

    private void checkPhoneStatePermission() {
        /*new PermissionRequest(LoginActivity.this,
                Permission.PERMISSION_READ_PHONE_STATE,
                PermissionCode.CODE_PERMISSION_READ_PHONE_STATE,
                R.string.permission_phone_state_rationale,
                R.string.permission_phone_state_denied,
                R.string.permission_enable_message, this)
                .checkPermission();*/
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
*//*
        switch (requestCode) {

            case PermissionCode.CODE_PERMISSION_READ_PHONE_STATE:
                // Check granted permission for location
                new PermissionRequest(LoginActivity.this,
                        Permission.PERMISSION_READ_PHONE_STATE,
                        PermissionCode.CODE_PERMISSION_READ_PHONE_STATE, R.string.permission_phone_state_rationale,
                        R.string.permission_phone_state_denied,
                        R.string.permission_enable_message, this)
                        .onRequestPermissionsResult(LoginActivity.this, requestCode, grantResults);
                break;
        }

        mGHelper.onPermissionResult(requestCode, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
        }
*//*
        return;
    }


    @SuppressLint("HardwareIds")
    @Override
    public void onGranted(PermissionResponse permissionResponse) {
        *//*switch (permissionResponse.type) {
            case PermissionCode.CODE_PERMISSION_READ_PHONE_STATE:
                break;
        }*//*

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        *//*if(mGHelper!= null){
            mGHelper.disconnectApiClient();
        }*//*
    }

    @Override
    public void onGSignInFail() {
        Toast.makeText(this, "Google sign in failed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGSignInSuccess(Person person) {
        final String sessionid = person.getId();
        Useraccid = person.getId() + "";
        Useraccname = person.getDisplayName() + "";
        UseraccProfilepic = person.getImage().getUrl() + "";
        UseraccGender = person.getGender() + "";
        UseraccGooglePlusId = person.getId() + "";
        String userLoginId = person.getId();
        ProfileImage = person.getImage().getUrl();
        Toast.makeText(this, "Google sign in success."+Useraccname+" "+UseraccProfilepic, Toast.LENGTH_SHORT).show();
        *//*UserInfo AuthenticatedUser = new UserInfo(000, Useraccname, person.getId(), "00000");
        MyDBHelper db = new MyDBHelper(LoginActivity.this);
        db.clearUser();
        db.addUser(AuthenticatedUser);

        UserInfo _u = db.getUserInfo();

        List<UserInfo> listUsers = new ArrayList();
        listUsers = db.getAllContacts();
        showProgress(false);
        finish();*//*
    }*/

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                   // return pieces[1].equals(mPassword);
                    Intent i=new Intent(LoginActivity.this,HomeActivity.class);
                   // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                // finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public void registerUser(View v) {
        Intent i = new Intent(this, RegistrationActivity.class);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        //handle results

        //mGHelper.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result != null) {
            Log.d("Login", "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();

                final String email = acct.getEmail();
                final String Name = acct.getGivenName();
                final String personPhotoUrl = acct.getPhotoUrl().toString();
                final String accessToken = acct.getId().toString();

                /*Toast.makeText(LoginActivity.this, "Login Success! \nEmail:" + email + "\nName:" + Name
                        +"/n Photo URL"+personPhotoUrl, Toast.LENGTH_SHORT).show();
                Toast.makeText(LoginActivity.this, "Access Token : " +accessToken
                        , Toast.LENGTH_SHORT).show();*/

                // check the saveed AccessToken From Local db with above access Toeken
                String data = SharedPrefs.getString(LoginActivity.this,SharedPrefs.accessTokenKey);
                if(!data.equals("")){
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }

                //Initially Check for Login
                UserInfoController UIC1 = UserInfoController.AuthenticateUser(LoginActivity.this, email,accessToken,true);
                if (UIC1 == null) {
                    Log.e("Error", "UIC is null");
                    return;
                }
                //Handling Response of service
                UIC1.setOnUserAuthentication(new AsyncProcessListener<Object>() {
                    @Override
                    public void ProcessFinished(Object Result) {
                        UserInfo AuthenticatedUser = (UserInfo) Result;
                        MyDBHelper db = new MyDBHelper(LoginActivity.this);
                        UserInfo _u = db.getUserInfo();
                        db.clearUser();
                        db.addUser(AuthenticatedUser);


                        List<UserInfo> listUsers = new ArrayList();
                        listUsers = db.getAllContacts();
                        showProgress(false);
                        Toast.makeText(LoginActivity.this, "Welcome " + AuthenticatedUser.getFullName().toString(), Toast.LENGTH_SHORT).show();
                        finish();
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void ProcessFailed(Exception E) { //Login Not Success
                        //Else do the Registration
                        UserInfoController UIC2 = UserInfoController.RegisterUser(LoginActivity.this,new UserInfo(Name,email,accessToken,accessToken,personPhotoUrl), "true");
                        if (UIC2 == null) {
                            Log.e("Error", "UIC is null");
                            return;
                        }
                        //Handling Response of service
                        UIC2.setOnUserRegistration(new AsyncProcessListener<Object>() {
                            @Override
                            public void ProcessFinished(Object Result) {
                                UserInfo AuthenticatedUser = (UserInfo) Result;
                                MyDBHelper db = new MyDBHelper(LoginActivity.this);
                                db.clearUser();
                                db.addUser(AuthenticatedUser);
                                showProgress(false);
                                Toast.makeText(LoginActivity.this,"Welcome "+AuthenticatedUser.getFullName().toString(),Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(LoginActivity.this,HomeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);

                                /*TextView tvGoogleauthtoken = (TextView) findViewById(R.id.tv_google_auth_token);
                                final String googletoken = tvGoogleauthtoken.getText().toString();
                                // If User comUp with GooglePlus SignIn
                                if(!googletoken.equals("9060200454554")){
                                    SharedPrefs.save(LoginActivity.this,googletoken,SharedPrefs.accessTokenKey);
                                }*/

                                finish();

                            }

                            @Override
                            public void ProcessFailed(Exception E) { //Unsuccess Registration
                                String Resp = E.getMessage();
                                Toast.makeText(LoginActivity.this,Resp,Toast.LENGTH_SHORT).show();
                                showProgress(false);
                            }
                        });
                    }

                });



                /*//mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
                //updateUI(true);
                UserInfo AuthenticatedUser = new UserInfo(000, Name, email, "00000");
                MyDBHelper db = new MyDBHelper(LoginActivity.this);
                db.clearUser();
                db.addUser(AuthenticatedUser);

                UserInfo _u = db.getUserInfo();

                List<UserInfo> listUsers = new ArrayList();
                listUsers = db.getAllContacts();
                showProgress(false);*/
                /*finish();
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                i.putExtra("email",email);
                i.putExtra("Name",Name);
                i.putExtra("personPhotoUrl",personPhotoUrl);
                i.putExtra("accessToken",accessToken);
                startActivity(i);*/

            } else {
                Toast.makeText(LoginActivity.this, "Google Sign in Failed", Toast.LENGTH_SHORT).show();
                //Signed out, show unauthenticated UI.
                //updateUI(false);
                showProgress(false);
            }
        }
    }
}