package se.miun.dahe1501.bathingsites;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Dave on 2017-05-12.
 * The Database class - uses PostgreSQL to
 * create DB store data, retrieve and delete data
 * Stores and retrieves bathingsites to be displayed
 * on map and database-list.
 */

public class DatabaseCreator extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_bathing_sites.db";          // Specify DB name
    private static final int DATABASE_VERSION = 1;
    private static final String DB_TABLE = "bathing_sites";                     // sets all column names and datatypes used
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_1 = "site_name text";
    private static final String COLUMN_2 = "site_address text";
    private static final String COLUMN_3 = "site_desc text";
    private static final String COLUMN_4 = "site_long text";
    private static final String COLUMN_5 = "site_lat text";
    private static final String COLUMN_6 = "site_grade float";
    private static final String COLUMN_7 = "site_water_temp double";
    private static final String COLUMN_8 = "site_date text";

    // Query to create the database
    private static final String DATABASE_CREATE = "CREATE TABLE " + DB_TABLE + "(" + COLUMN_ID +
            " integer primary key autoincrement," + COLUMN_1 + "," + COLUMN_2 + "," + COLUMN_3 + "," + COLUMN_4 + "," + COLUMN_5 + "," + COLUMN_6 + "," + COLUMN_7 + "," + COLUMN_8 + ");";

    SQLiteDatabase bathDb;

    public DatabaseCreator(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        //deleteDatabase(context);                                  Function to fast delete the database if needed

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists" + DB_TABLE);                              // Function called if database needs to be updated
        onCreate(db);
    }

    /* Function to insert data into the database - uses a Bathingsite-object to retrieve data and save in fields */
    public void insertData(BathingSite insertSite){
        bathDb = getWritableDatabase();                             // Get the database

        String name = insertSite.getName();
        String address = insertSite.getAddress();
        String desc = insertSite.getDesc();
        String lat = insertSite.getLongitude();
        String longit = insertSite.getLatitude();
        float grade = insertSite.getGrade();
        double water_temp = insertSite.getWaterTemp();
        String date = insertSite.getDateTemp();

        // Query used to insert Bathingsite-data into the fields
        bathDb.execSQL("INSERT INTO " + DB_TABLE + " (site_name,site_address,site_desc,site_long,site_lat,site_grade,site_water_temp,site_date) values('"+ name +"','"+ address +"','" + desc + "','" + lat + "','" + longit + "','"
                                                        + grade + "','" + water_temp + "','" + date + "');");
    }

    /* Function to retrieve everything from the database */
    public void getAll() {
        bathDb = getReadableDatabase();
        Cursor cr = bathDb.rawQuery("SELECT * FROM " + DB_TABLE,null);

        StringBuilder stringBuilder = new StringBuilder();

        while (cr.moveToNext()){
            String s1 = cr.getString(1);
            String s2 = cr.getString(2);
            String s3 = cr.getString(3);
            float grade = cr.getFloat(6);
            double water_temp = cr.getDouble(7);
            stringBuilder.append(s1 + "    " +s2 + "    " +s3 + "\n" + grade + "\n" + water_temp);
            stringBuilder.append(s1);

        }

        //Log.i("DB contents",stringBuilder.toString());

    }


    /* Function to retrieve data from a specific column, returns an array of strings */
    public ArrayList<String> getColumnData(String columnName){

        ArrayList<String> columnList = new ArrayList<String>();

        bathDb = getReadableDatabase();
        Cursor cr = bathDb.rawQuery("SELECT * FROM " + DB_TABLE + " ORDER BY site_name" ,null);         // Select everything and Sort

        int columnId = 0;

        // Column ID is used to specify which column user wants information from
        switch (columnName){
            case "Name" : columnId = 1;
                break;
            case "Address" : columnId = 2;
                break;
            case "Longitude" : columnId = 4;
                break;
            case "Latitude" : columnId = 5;
                break;
            case "Description" : columnId = 3;
                break;
            case "Rating" : columnId = 6;                   // Cast to string before adding to ArrayList
                while (cr.moveToNext()){
                    Float d1 = cr.getFloat(columnId);
                    String s1 = Float.toString(d1);
                    columnList.add(s1);
                }
                return columnList;
            case "Date" : columnId = 8;
                break;
            case "Temp" : columnId = 7;                     // Cast to string before adding to ArrayList
                while (cr.moveToNext()){
                    Double d1 = cr.getDouble(columnId);
                    String s1 = Double.toString(d1);
                    columnList.add(s1);
                }
                return columnList;
        }

        while (cr.moveToNext()){                            // Retrieves data from the choosen column and loop entrys
            String s1 = cr.getString(columnId);
            columnList.add(s1);
        }

        return columnList;                                  // Return column as ArrayList
    }

    /* Deletes the database */
    private void deleteDatabase(Context context){
        context.deleteDatabase(DATABASE_NAME);
        //Log.i("Database:","Deleted");
    }

    /* Checks if bathingsite is already in the database */
    public boolean checkisInDb(String latitude,String Longitude){
        bathDb = getReadableDatabase();
        String Query = "SELECT * FROM " + DB_TABLE + " WHERE " + "site_long" + " = " + Longitude + " AND " + "site_lat" + " = " + latitude;             // Query checks if lat AND long is in DB
        //Log.i("Working query",Query);
        Cursor cursor = bathDb.rawQuery(Query, null);
        //Log.i("Matches","" + cursor.getCount());
        if (cursor.getCount() <= 0 ){                           // If matches are 0 - the object is not in the database and can be added
            cursor.close();
            return false;
        }
        cursor.close();
        return true;                                    // Else, object is already in database
    }

    /* Counts all the rows in the database - returns the number of bathingsites in the database */
    public int getRowCount(){
        bathDb = getReadableDatabase();
        int count = (int) DatabaseUtils.queryNumEntries(bathDb,DB_TABLE);
       // bathDb.close();
        return count;
    }


}
