package com.service.iscon.vcr.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.service.iscon.vcr.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class QueryExecutor {

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


    private void deleteTable(SQLiteDatabase db, String tableName) {
        try {
            db.execSQL("delete from " + tableName);
        } catch (Exception e) {
            Log.i("Delete Error", e.getLocalizedMessage());
        }
    }


//    public boolean insertNewCategory(SQLiteDatabase db, String tableName) {
//        List<String> categories = new ArrayList<>();
//        String selectQuery = "SELECT " + KEY_CATEGORY + " from " + TABLE_TO_DO_ITEMS;
//        try {
//            Cursor cursor = db.rawQuery(selectQuery, null);
//            if (cursor.moveToFirst()) {
//                do {
//                    categories.add(cursor.getString(0));
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        } catch (Exception e) {
//            Log.i("DB Error ", e.getLocalizedMessage());
//        }
//        Log.d("categories", categories.toString());
//        if (categories.contains(tableName.toLowerCase())) {
//            Log.d("Aabhas", "Returning false");
//            return false;
//        }
//
//        db.beginTransaction();
//        try {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(KEY_CATEGORY, tableName.toLowerCase());
//            contentValues.put(KEY_ITEM_NUMBER, 0);
//            db.insert(TABLE_TO_DO_ITEMS, null, contentValues);
//            String CREATE_NEW_TABLE = "CREATE TABLE " + tableName.toLowerCase() + "("
//                    + KEY_TITLE + " TEXT PRIMARY KEY, "
//                    + KEY_DESCRIPTION + " TEXT, " + KEY_IF_DONE + " BOOLEAN, " + KEY_IMAGE_PATH + "TEXT )";
//            db.execSQL(CREATE_NEW_TABLE);
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            Log.i("DB Error ", e.getLocalizedMessage());
//        } finally {
//            db.endTransaction();
//        }
//
//        return true;
//
//    }
//
//    public List<ToDoItemCategory> getAllCategoryItems(SQLiteDatabase db) {
//        List<ToDoItemCategory> list = new ArrayList<>();
//        String selectQuery = "SELECT * from " + TABLE_TO_DO_ITEMS;
//        try {
//            Cursor cursor = db.rawQuery(selectQuery, null);
//            if (cursor.moveToFirst()) {
//                do {
//                    ToDoItemCategory category = new ToDoItemCategory();
//                    category.setCategoryTitle(cursor.getString(0));
//                    category.setToDoItemsListCount(cursor.getInt(1));
//                    list.add(category);
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        } catch (Exception e) {
//            Log.i("DB Error ", e.getLocalizedMessage());
//        }
//        return list;
//    }
//
//    public List<ToDoItem> getAllCategoryItemsCategoryWise(SQLiteDatabase db, String categoryName) {
//        List<ToDoItem> list = new ArrayList<>();
//
//        String selectFirstQuery = "SELECT * from " + categoryName;
//        try {
//            Cursor cursor = db.rawQuery(selectFirstQuery, null);
//            if (cursor.moveToFirst()) {
//                do {
//                    ToDoItem category = new ToDoItem();
//                    category.setItemTitle(cursor.getString(0));
//                    category.setItemDescription(cursor.getString(1));
//                    category.setIfDone(cursor.getInt(2) == 1 ? true : false);
//                    category.setImagePath(cursor.getString(3));
//                    list.add(category);
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        } catch (Exception e) {
//            Log.i("DB Error ", e.getLocalizedMessage());
//        }
//        return list;
//    }
//
//
//    public void insertNewItemInCategory(SQLiteDatabase db, String tableName, String title, String description, boolean isCompleted, String imagePath) {
//        db.beginTransaction();
//        try{
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(KEY_TITLE, title.toLowerCase());
//            contentValues.put(KEY_DESCRIPTION, description);
//            contentValues.put(KEY_IF_DONE, isCompleted);
//            contentValues.put(KEY_IMAGE_PATH, imagePath);
//            db.insert(tableName.toLowerCase(), null, contentValues);
//            db.setTransactionSuccessful();
//        }catch (Exception e){
//            Log.i("DB Error ", e.getLocalizedMessage());
//        }finally {
//            db.endTransaction();
//        }
//
//    }


    //Chanting App Database



    public void clearUser(SQLiteDatabase db){
        int c = getUsersCount(db);
        if(c>0) {
            String countQuery = "DELETE  FROM " + TABLE_USER;
            Cursor cursor = db.rawQuery(countQuery, null);
            int deleted_record = cursor.getCount();
        }
    }

    public int getUsersCount(SQLiteDatabase db) {
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        Cursor cursor = db.rawQuery(countQuery, null);
        int c = cursor.getCount();
        cursor.close();
        // return count
        return c;
    }

    // Adding new contact
    public void addUser(SQLiteDatabase db, UserInfo mUserInfo) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, mUserInfo.getId());
        values.put(COLUMN_NAME, mUserInfo.getFullName());
        values.put(COLUMN_EMAIL, mUserInfo.getEmail());
        values.put(COLUMN_PASSWORD, mUserInfo.getPassword());
        values.put(COLUMN_MOBILE, mUserInfo.getMobile());
        values.put(COLUMN_CREATED_DATE, mUserInfo.getCreatedDate());
        values.put(COLUMN_LAST_LOGIN, mUserInfo.getLastLogin());
        values.put(COLUMN_IS_ACTIVE, mUserInfo.getIsActive());
        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    public UserInfo getUserInfo(SQLiteDatabase db){
        Cursor cursor = db.query(TABLE_USER, new String[] { COLUMN_ID,
                        COLUMN_EMAIL,COLUMN_PASSWORD,COLUMN_NAME,COLUMN_MOBILE,COLUMN_CREATED_DATE,COLUMN_LAST_LOGIN,COLUMN_IS_ACTIVE}, COLUMN_ID + "=?",
                new String[] { String.valueOf(1) }, null, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            UserInfo mUserInfo = new UserInfo(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7));
            // return contact
            return mUserInfo;
        }else{
            return null;
        }
    }


    public int updateContact(SQLiteDatabase db, UserInfo mUserInfo) {
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
    public boolean deleteContact(SQLiteDatabase db, UserInfo contact) {
        int deleted_row=db.delete(TABLE_USER, COLUMN_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        db.close();

        return deleted_row > 0;
    }

    // Getting All Contacts
    public List<UserInfo> getAllContacts(SQLiteDatabase db) {
        List<UserInfo> contactList = new ArrayList<UserInfo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER;
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