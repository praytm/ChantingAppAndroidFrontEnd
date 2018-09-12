package com.service.iscon.vcr.Handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.service.iscon.vcr.Model.UserInfo;

/**
 * Created by priyanka on 26-01-2017.
 */

public class UserDB extends SQLiteOpenHelper{

    private Context mContext;
    private static final String DATABASE_NAME = "VCR";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USER = "tblUser";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_NAME = "full_name";
    private static final String COLUMN_MOBILE = "mobile";
    private static final String COLUMN_CREATED_DATE = "create_date";
    private static final String COLUMN_LAST_LOGIN = "last_login";
    private static final String COLUMN_IS_ACTIVE = "is_active";
    private static final String COLUMN_PROFILE_PIC = "profile_pic";
    private static final String CREATE_TABLE = "CREATE TABLE If Not Exists " + TABLE_USER + "(" +
            COLUMN_ID + " Integer Not Null," +
            COLUMN_USER_ID + "Integer Not Null," +
            COLUMN_NAME + " Text," +
            COLUMN_EMAIL + " Text," +
            COLUMN_PASSWORD+ " Text," +
            COLUMN_MOBILE + " Text," +
            COLUMN_CREATED_DATE + " Text," +
            COLUMN_LAST_LOGIN + " Text," +
            COLUMN_IS_ACTIVE + " Integer ," +
            COLUMN_PROFILE_PIC + "Text )";

    public UserDB(Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.getPath();

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Create tables again
        onCreate(db);
    }

    // Adding new User
    public void insertUser(UserInfo mUserInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, 1);
        values.put(COLUMN_USER_ID, mUserInfo.getFullName());
        values.put(COLUMN_NAME, mUserInfo.getFullName());
        values.put(COLUMN_EMAIL, mUserInfo.getEmail());
        values.put(COLUMN_PASSWORD, mUserInfo.getPassword());
        values.put(COLUMN_MOBILE, mUserInfo.getMobile());
        values.put(COLUMN_CREATED_DATE, mUserInfo.getCreatedDate());
        values.put(COLUMN_LAST_LOGIN, mUserInfo.getLastLogin());
        values.put(COLUMN_IS_ACTIVE, mUserInfo.getIsActive());
        values.put(COLUMN_PROFILE_PIC, mUserInfo.getProfilePic());
        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    // Getting single User
    public UserInfo getUserInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[] { COLUMN_ID,
                        COLUMN_EMAIL,COLUMN_PASSWORD,COLUMN_NAME, COLUMN_MOBILE, COLUMN_CREATED_DATE, COLUMN_LAST_LOGIN, COLUMN_IS_ACTIVE,COLUMN_PROFILE_PIC}, COLUMN_ID + "=?",
                new String[] { String.valueOf(1) }, null, null, null, null);

        if(cursor != null && cursor.getCount()>0) {
            UserInfo mUserInfo = new UserInfo(Integer.parseInt(cursor.getString(1)),
                    cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getInt(8),cursor.getString(9));
            db.close(); // Closing database connection
            return mUserInfo;
        }
        return null;
    }
}