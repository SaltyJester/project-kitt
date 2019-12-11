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

    /*
      This function takes a FoodDetail object and stores its fields into a SQLite table. At the same
      time this function is also initializing the FireStoreDB.class and using its functions to send
      the fields to the Firestore database.
     */
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

    /*
      Exact same function as above but edited to be specifically used in the FireStoreDB.class
      Does not write to Firestore database.
     */
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

        return newRowId;
    }

    /*
      Given an Int ID, this function can delete a specific food item from the SQLite table. It'll
      also delete the food item from the Firestore database.
     */
    public void removeFood(int index, Context ctx){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_FOOD_ID + "=?", new String[]{Integer.toString(index)});

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            FirestoreDB firestoreDB = new FirestoreDB(null);
            firestoreDB.deleteFood(index);
        }
        for(int i=0; i<4; i++){
            // add i to the id (this is what we set the alarmid as
            String appendIndex = String.valueOf(index) + String.valueOf(i);
            int id = Integer.parseInt(appendIndex);
            Intent intent = new Intent(ctx.getApplicationContext(), myReceiver.class);
            boolean alarmUp = (PendingIntent.getBroadcast(ctx.getApplicationContext(), id, intent, PendingIntent.FLAG_NO_CREATE) != null);
            // check is alarm id is valid, if so delete the alarm created
            if(alarmUp){
                AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                PendingIntent pintent = PendingIntent.getBroadcast(ctx.getApplicationContext(), id, intent, 0);
                alarm.cancel(pintent);
                pintent.cancel();
            }
        }
    }

    /*
      This function will drop all entries from the SQLite table
     */
    public void deleteTable(String tableName)
    {
        String selectQuery = "DELETE FROM " + tableName;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(selectQuery);
        db.close();
    }

    /*
      This function gets all entries from the SQLite table and puts the data into a FoodDetail
      object. It returns an array of FoodDetail objects.
     */
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

    /*
      This function checks to see if a food entry with a certain ID already exists in the table.
      This is currently unimplemented and untested.
     */
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

    /*
      This function is used to get the amount of entries in the SQLite table
     */
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
