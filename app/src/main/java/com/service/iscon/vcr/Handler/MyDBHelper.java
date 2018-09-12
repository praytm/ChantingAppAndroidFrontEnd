package com.service.iscon.vcr.Handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.service.iscon.vcr.Model.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin2 on 2/2/17.
 */

public class MyDBHelper extends SQLiteOpenHelper {

    // All Static variables
    // UserInfos table name

    // UserInfos Table Columns names
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
    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create Table userLogin
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_MOBILE + " TEXT,"
                + COLUMN_CREATED_DATE + " TEXT,"
                + COLUMN_LAST_LOGIN + " TEXT,"
                + COLUMN_IS_ACTIVE + " INTEGER,"
                + COLUMN_PROFILE_PIC + " TEXT "
                + ")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(sqLiteDatabase);
    }


    public void clearUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        int c=getUsersCount();
        if(c>0) {
            String countQuery = "DELETE  FROM " + TABLE_USER;
            Cursor cursor = db.rawQuery(countQuery, null);
            int deleted_record = cursor.getCount();
        }
    }

    // Adding new contact
    public void addUser(UserInfo mUserInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, mUserInfo.getId());
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

    // Getting single contact
    public UserInfo getUserInfo(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[] { COLUMN_ID,
                        COLUMN_EMAIL,COLUMN_PASSWORD,COLUMN_NAME,COLUMN_MOBILE,COLUMN_CREATED_DATE,COLUMN_LAST_LOGIN,COLUMN_IS_ACTIVE,COLUMN_PROFILE_PIC}, COLUMN_ID + "=?",
                new String[] { String.valueOf(1) }, null, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            UserInfo mUserInfo = new UserInfo(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7),cursor.getString(8));
            // return contact
            return mUserInfo;
        }else{
            return null;
        }
    }

    // Getting contacts Count
    public int getUsersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int c=cursor.getCount();
        cursor.close();
        // return count
        return c;
    }

    // Updating single contact
    public int updateContact(UserInfo mUserInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, mUserInfo.getFullName());
        values.put(COLUMN_MOBILE, mUserInfo.getMobile());
        values.put(COLUMN_PASSWORD, mUserInfo.getMobile());
        values.put(COLUMN_EMAIL, mUserInfo.getEmail());
        values.put(COLUMN_CREATED_DATE, mUserInfo.getCreatedDate());
        values.put(COLUMN_LAST_LOGIN, mUserInfo.getLastLogin());
        values.put(COLUMN_IS_ACTIVE, mUserInfo.getIsActive());

        // updating row
        int updated_row= db.update(TABLE_USER, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(mUserInfo.getId()) });
        db.close();
        return updated_row;
    }

    // Deleting single contact
    public boolean deleteContact(UserInfo contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted_row=db.delete(TABLE_USER, COLUMN_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        db.close();

        return deleted_row > 0;
    }

    // Getting All Contacts
    public List<UserInfo> getAllContacts() {
        List<UserInfo> contactList = new ArrayList<UserInfo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserInfo mUserInfo = new UserInfo();
                mUserInfo.setId(Integer.parseInt(cursor.getString(0)));
                mUserInfo.setFullName(cursor.getString(1));
                mUserInfo.setMobile(cursor.getString(2));
                // Adding contact to list
                contactList.add(mUserInfo);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }
}
