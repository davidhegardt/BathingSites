package se.miun.dahe1501.bathingsites;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.NameList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/*
* Site Registry - used to display the data
* in the database, the bathingsites ordered by Name
*
 */

public class SiteRegistry extends AppCompatActivity {

    ArrayList<String> nameList = new ArrayList<String>();           // Setup arraylists to retrieve from database
    ArrayList<String> locationList = new ArrayList<String>();
    ArrayList<String> longList = new ArrayList<String>();
    ArrayList<String> latitList = new ArrayList<String>();

    TableLayout tl;                                         // Dynamically setup table layout
    TableRow tableRow;
    TextView nameTV,locationTV,latitTV,longitTV;

    private DatabaseCreator myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_registry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SiteRegistry.this, NewBathingSiteActivity.class));
            }
        });


        myData = new DatabaseCreator(this);                         // Create database object
        nameList = myData.getColumnData("Name");                    // Retrieve column data from database
        locationList = myData.getColumnData("Address");
        longList = myData.getColumnData("Longitude");
        latitList = myData.getColumnData("Latitude");


        tl = (TableLayout) findViewById(R.id.maintable);
        addHeaders();
        addData();
    }

    /* Dynamically create headers for the data-table view */
    public void addHeaders(){
        tableRow = new TableRow(this);
        tableRow.setLayoutParams(new Toolbar.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT ));

        /** Creating a TextView to add to the row **/
        final TextView nameTV = new TextView(this);
        nameTV.setText(getResources().getString(R.string.name));
        nameTV.setTextColor(Color.GRAY);
        nameTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        nameTV.setPadding(2, 2, 2, 0);
        tableRow.addView(nameTV);  // Adding textView to tablerow.

        /** Creating another textview **/
        TextView locationTV = new TextView(this);
        locationTV.setText(getResources().getString(R.string.location));
        locationTV.setTextColor(getResources().getColor(R.color.lightGray));
        locationTV.setPadding(2, 2, 2, 0);
        locationTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tableRow.addView(locationTV); // Adding textView to tablerow.

        TextView latitTV = new TextView(this);
        latitTV.setText(getResources().getString(R.string.latit));
        latitTV.setTextColor(getResources().getColor(R.color.lightGray));
        latitTV.setPadding(2, 2, 2, 0);
        latitTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tableRow.addView(latitTV); // Adding textView to tablerow.

        TextView longitTV = new TextView(this);
        longitTV.setText(getResources().getString(R.string.longit));
        longitTV.setTextColor(getResources().getColor(R.color.lightGray));
        longitTV.setPadding(2, 2, 2, 0);
        longitTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tableRow.addView(longitTV); // Adding textView to tablerow.


        // Add the TableRow to the TableLayout
        tl.addView(tableRow, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));


    }


    /* Function to create and populate rows with data from database */
    public void addData(){
        for (int i = 0; i < nameList.size(); i++){

            tableRow = new TableRow(this);                                  // Loop and crate tablerows
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            /** Creating a TextView to add to the row **/
            nameTV = new TextView(this);
            nameTV.setText(nameList.get(i));
            nameTV.setTextColor(getResources().getColor(R.color.darkGreen));
            nameTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            nameTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            nameTV.setPadding(2, 2, 2, 0);
            tableRow.addView(nameTV);  // Adding textView to tablerow.

            /** Creating another textview **/
            locationTV = new TextView(this);
            locationTV.setText(locationList.get(i));
            locationTV.setTextColor(getResources().getColor(R.color.plainGreen));
            locationTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            locationTV.setPadding(2, 2, 2, 0);
            locationTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tableRow.addView(locationTV); // Adding textView to tablerow.

            /** Creating another textview **/
            latitTV = new TextView(this);
            latitTV.setText(latitList.get(i));
            latitTV.setTextColor(getResources().getColor(R.color.plainGreen));
            latitTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            latitTV.setPadding(2, 2, 2, 0);
            latitTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tableRow.addView(latitTV); // Adding textView to tablerow.

            /** Creating another textview **/
            longitTV = new TextView(this);
            longitTV.setText(longList.get(i));
            longitTV.setTextColor(getResources().getColor(R.color.plainGreen));
            longitTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            longitTV.setPadding(2, 2, 2, 0);
            longitTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tableRow.addView(longitTV); // Adding textView to tablerow.


            // Add the TableRow to the TableLayout
            tl.addView(tableRow, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));


        }
    }


}
