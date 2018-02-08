package se.miun.dahe1501.bathingsites;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Dave on 2017-05-16.
 * Helper class used to Parse CSV data and save in Database
 */

public class CSVparser {

    private String fullPath;
    private BufferedReader br;
    private String line = "";
    private String response = "";
    private List<String> theList;
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
    private int count = 0;
    private int realCount = 0;
    private DatabaseCreator myData;
    //private BathingSite readSite;

    /*
        * Constructor - Creates the parser
        * parameters : Path to CSV file, contect from activity
     */
    public CSVparser(String Path, Context ctx){
        fullPath = Path;
        theList = new ArrayList<>();            // Initiate list of data
        myData = new DatabaseCreator(ctx);      // Retrieve the database object
    }

    /* Function to parse the CSV file from input */
    public void ParseData(){

        try {
            br = new BufferedReader(new FileReader(fullPath));          // Create bufferedReader and FileReader to read the csv file

            while ((line = br.readLine()) != null){                     // Loop every line
                //response += line + "\n";
                String[] location = line.split(",");                    // Split by comma, creates a comma separated array of strings from csv file
                parseLine(location);                                    // Parse and format the data from the array
                //realCount++;

            }

            //Log.i("Content",response);
            //Log.i("Total lines", "" + realCount);

            br.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }


    }

    /* Function used to parse CSV input array and save in a Bathingsites-object */
    public void parseLine(String[] line){
        BathingSite readSite = new BathingSite();

        if (line.length > 1){
            line[0] = Helper.trimString(line[0],Character.toString(DEFAULT_QUOTE));     // 0 contains longitude - 1 contains latitude
            line[0] = line[0].replaceAll("[\\uFEFF-\\uFFFF]","");                       //Character to remove in order to read into database
            line[1] = line[1].replaceAll("[\\uFEFF-\\uFFFF]","");

            readSite.setLongitude(line[0]);
            readSite.setLatitude(line[1]);
        }
        if (line.length > 2){                                                           // If array contains 2 entrys, set the Name from entry 2
            line[2] = Helper.trimString(line[2],Character.toString(DEFAULT_QUOTE));

            readSite.setName(line[2]);
        }

        if (line.length > 3){
            line[3] = Helper.trimString(line[3],Character.toString(DEFAULT_QUOTE));     // If array containts 3 entrys, set the address from entry 3

            readSite.setAddress(line[3]);

        } else {
            readSite.setAddress("N/A");                                                 // Else set address to N/A
        }

        if (line.length > 4){
            line[4] = Helper.trimString(line[4],Character.toString(DEFAULT_QUOTE));     // If array contains 4 entrys, concat 3 and 4 to form complete address
            line[3] = line[3] + "," + line[4];

        }


        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");                            // Create new date and set to today, dummy value for date for temp
        String today = df.format(new Date());

        readSite.setDesc("N/A");                                                        // None contains description, so set to N/A
        readSite.setWaterTemp(0.0);                                                     // None contains water temp, so set to 0.0
        readSite.setDateTemp(today);
        readSite.setGrade((float) 0.0);                                                 // None contains rating, so set to 0.0
        writeToDB(readSite);                                                     // Send to database storage

    }

    /* Calls database object and checks that site is not already in DB, if not save to DB */
    public void writeToDB(BathingSite readSite){
        if(!myData.checkisInDb(readSite.getLatitude(),readSite.getLongitude())){
            myData.insertData(readSite);
        }
    }


}
