package se.miun.dahe1501.bathingsites;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private DatabaseCreator myData;
    private int siteCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);                 // Setup toolbar
        setSupportActionBar(toolbar);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());       // Get the preferences
        PreferenceManager.setDefaultValues(this,R.xml.preferences, false);                  // Set default values if first-run

        myData = new DatabaseCreator(this);                                 // Create database-object
        siteCount = myData.getRowCount();                                   // Retrieve number of bathing sites


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this, NewBathingSiteActivity.class));         // FAB button starts activity to create new bathingsite
               // new PHParser().execute("test","test","test");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);                          // Inflate the toolbarmenu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(MainActivity.this,SettingsActivity.class));            // Start Settings-activity
            return true;


        }

        if (id == R.id.sites_database) {
            //myData.getColumnData("Name");

            startActivity(new Intent(MainActivity.this, SiteRegistry.class));               // Start the Database-view
        }

        if (id == R.id.download_sites){

            startActivity(new Intent(MainActivity.this, DownloadSitesActivity.class));      // Start activity to download sites
        }
        if ( id == R.id.maps_activity){
            startActivity(new Intent(MainActivity.this, MapsActivity.class));               // Start the Maps activity
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        siteCount = myData.getRowCount();


    }
}
