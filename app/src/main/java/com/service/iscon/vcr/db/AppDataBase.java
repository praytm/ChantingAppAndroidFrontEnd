package com.service.iscon.vcr.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Aabhas on
 */
public class AppDataBase extends SQLiteOpenHelper {

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


    private static AppDataBase appDataBase;

    public static synchronized AppDataBase getInstance(Context context){
        if(appDataBase==null){
            appDataBase= new AppDataBase(context.getApplicationContext());
        }
        return appDataBase;
    }

    private AppDataBase(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_MOBILE + " TEXT,"
                + COLUMN_CREATED_DATE + " TEXT,"
                + COLUMN_LAST_LOGIN + " TEXT,"
                + COLUMN_IS_ACTIVE + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS"+TABLE_KNOWN_FOLDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }


}
