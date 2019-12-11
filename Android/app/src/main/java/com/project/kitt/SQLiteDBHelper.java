package com.project.kitt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SQLiteDBHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FoodDetails";

    public static final String TABLE_NAME = "food";
    public static final String KEY_FOOD_ID = "food_id";
    public static final String KEY_FOOD_NAME = "food_name";
    public static final String KEY_FOOD_DAY = "food_day";
    public static final String KEY_FOOD_MON = "food_mon";
    public static final String KEY_FOOD_YR = "food_yr";

    // Create Table Query
    private static String SQL_CREATE_FOOD =
            "CREATE TABLE food (" + KEY_FOOD_ID + " INTEGER PRIMARY KEY, " + KEY_FOOD_NAME + " TEXT, " + KEY_FOOD_DAY
            + " INTEGER, " + KEY_FOOD_MON + " INTEGER, " + KEY_FOOD_YR + " INTEGER);";

    public static final String SQL_DELETE_FOOD =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public SQLiteDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FOOD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the table while upgrading the database
        // such as adding new column or changing existing constraint
        db.execSQL(SQL_DELETE_FOOD);
        this.onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        this.onUpgrade(db, oldVersion, newVersion);
    }

    public long addFood(FoodDetail food)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues food_detail = new ContentValues();
        food_detail.put(KEY_FOOD_NAME, food.getFoodName());
        food_detail.put(KEY_FOOD_DAY,food.getFoodDay());
        food_detail.put(KEY_FOOD_MON,food.getFoodMon());
        food_detail.put(KEY_FOOD_YR,food.getFoodYr());

        long newRowId = db.insert(TABLE_NAME, null, food_detail);
        db.close();

        // While we're here, we might as well add the food object to the Firestore
        FirestoreDB firestoreDB = new FirestoreDB(null);
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            food.setFoodID((int) newRowId);
            firestoreDB.addFood(food, (int) newRowId);
        }

        return newRowId;
    }

    public long addFoodFromFirebase(FoodDetail food)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues food_detail = new ContentValues();
        food_detail.put(KEY_FOOD_NAME, food.getFoodName());
        food_detail.put(KEY_FOOD_DAY,food.getFoodDay());
        food_detail.put(KEY_FOOD_MON,food.getFoodMon());
        food_detail.put(KEY_FOOD_YR,food.getFoodYr());

        long newRowId = db.insert(TABLE_NAME, null, food_detail);
        db.close();

        // While we're here, we might as well add the food object to the Firestore
//        FirestoreDB firestoreDB = new FirestoreDB(null);
//        food.setFoodID((int) newRowId);
//        firestoreDB.addFood(food, (int) newRowId);

        return newRowId;
    }

//    public FoodDetail getFood(int food_id)
//    {
//        FoodDetail foodDetail = new FoodDetail();
//        SQLiteDatabase db = this.getReadableDatabase();
//        // specify the columns to be fetched
//        String[] columns = {KEY_FOOD_ID, KEY_FOOD_NAME, KEY_FOOD_DATE};
//        // select condition
//        String selection = KEY_FOOD_ID + " = ?";
//        // arguments for selection
//        String[] selectionArgs = {String.valueOf(food_id)};
//
//        Cursor cursor = db.query(TABLE_NAME, columns, selection,
//                selectionArgs, null, null, null);
//        if(null != cursor)
//    }

    public void removeFood(int index, Context ctx){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_FOOD_ID + "=?", new String[]{Integer.toString(index)});

        // While we're here we might as well delete the food object from the Firestore
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            FirestoreDB firestoreDB = new FirestoreDB(null);
            firestoreDB.deleteFood(index);
        }
        for(int i=0; i<4; i++){
            String appendIndex = String.valueOf(index) + String.valueOf(i);
            int id = Integer.parseInt(appendIndex);

            Intent intent = new Intent(ctx.getApplicationContext(), myReceiver.class);
            boolean alarmUp = (PendingIntent.getBroadcast(ctx.getApplicationContext(), id, intent, PendingIntent.FLAG_NO_CREATE) != null);
            if(alarmUp){
                //System.out.println(id + "IS VALID ALARM");
                AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                PendingIntent pi = PendingIntent.getBroadcast(ctx.getApplicationContext(), id, intent, 0);
                am.cancel(pi);
                pi.cancel();
            }
        }
    }

    public void deleteTable(String tableName)
    {
        String selectQuery = "DELETE FROM " + tableName;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(selectQuery);
        db.close();
    }

    public FoodDetail[] getAllFood()
    {
        int length = getCount();
        int i = length - 1;
        FoodDetail[] foodDetailsArray = new FoodDetail[length];

        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " ORDER BY " + KEY_FOOD_YR + " DESC, " + KEY_FOOD_MON + " DESC, "
                + KEY_FOOD_DAY + " DESC;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        // if TABLE has rows
        if(cursor.moveToFirst())
        {
            // loop through the table rows
            do
                {
                    FoodDetail foodDetail = new FoodDetail();
                    foodDetail.setFoodID(cursor.getInt(0));
                    foodDetail.setFoodName(cursor.getString(1));
                    foodDetail.setFoodDay(cursor.getInt(2));
                    foodDetail.setFoodMon(cursor.getInt(3));
                    foodDetail.setFoodYr(cursor.getInt(4));

                    foodDetailsArray[i] = foodDetail;
                    i--;
                } while(cursor.moveToNext());
        }

        db.close();
        return foodDetailsArray;
    }

    public boolean hasObject(String id)
    {
        SQLiteDatabase db = getWritableDatabase();
        String selectString = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_FOOD_ID + " =?";

        Cursor cursor = db.rawQuery(selectString, new String[] {id});

        boolean hasObject = false;
        if(cursor.moveToFirst())
        {
            hasObject = true;
        }

        cursor.close();
        db.close();
        return hasObject;
    }

    public int getCount()
    {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery("select * from " + TABLE_NAME + ";", null );
        StringBuilder sr = new StringBuilder();
        while(cr.moveToNext())
        {
            count++;
        }
        cr.close();
        db.close();
        return count;
    }
}
