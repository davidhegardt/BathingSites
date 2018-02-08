package se.miun.dahe1501.bathingsites;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Handler;

public class NewBathingSiteActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText nameInput;
    private EditText descInput;
    private EditText addressInput;
    private EditText longInput;
    private EditText latInput;
    private EditText waterTemp;
    private EditText dateTemp;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private DatabaseCreator myData;
    String currentError = "";


    private String theRating;
    private float ratingValue;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bathing_site);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the database-object
        myData = new DatabaseCreator(this);

        // Find and declare member variables
        nameInput = (EditText)findViewById(R.id.txtName);
        descInput = (EditText)findViewById(R.id.txtDesc);
        addressInput = (EditText) findViewById(R.id.txtAddress);
        longInput = (EditText) findViewById(R.id.txtLongitude);
        latInput = (EditText) findViewById(R.id.txtLatitude);
        waterTemp =(EditText) findViewById(R.id.txtWaterTemp);
        dateTemp =(EditText) findViewById(R.id.txtTempDate);

        // Register listener for Ratingbar
        ratingBarListener();

        // Set default preferences if first time running app
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        PreferenceManager.setDefaultValues(this,R.xml.preferences, false);



    }
    /* Function called to save sites - performs checks and verifies before adding */
    public void doSave(){

        String[] stringArray = new String[8];                   // Declare array for storage
        stringArray[0] = nameInput.getText().toString();        // Add all the input fields to the array from textfields
        stringArray[1] = descInput.getText().toString();
        stringArray[2] = addressInput.getText().toString();
        stringArray[3] = longInput.getText().toString();
        stringArray[4] = latInput.getText().toString();
        stringArray[5] = theRating;
        stringArray[6] = waterTemp.getText().toString();
        stringArray[7] = dateTemp.getText().toString();

        if (checkEmpty(stringArray) && checkFormatting()) {        // Call function to check that fields are not empty
                                                                    // and check that data types entered are correct
            BathingSite newSite = new BathingSite();                // Create new BathingSite object and add data for member variables
            newSite.setName(nameInput.getText().toString());
            newSite.setDesc(descInput.getText().toString());
            newSite.setAddress(addressInput.getText().toString());
            newSite.setLongitude(longInput.getText().toString());
            newSite.setLatitude(latInput.getText().toString());
            newSite.setGrade(ratingValue);
            newSite.setWaterTemp(Double.parseDouble(waterTemp.getText().toString()));
            newSite.setDateTemp(dateTemp.getText().toString());
            if (!myData.checkisInDb(latInput.getText().toString(),longInput.getText().toString())) {        // Check that lat / long is not alreade in DB
                myData.insertData(newSite);                                                                 // Add bathingsite-object to database
                snackBar(nameInput.getText().toString() + getResources().getString(R.string.saved_database));               // Show snackbar that entry is saved
                clearAll();
                finish();
            } else {
                snackBar(getResources().getString(R.string.already_in_db));                                     // Show error if present in database
            }


        } else {
            snackBar(getResources().getString(R.string.save_error));                                      // Show error if user did not fill in fields
        }
    }

    public void doLoad(){
        myData.getAll();
    }

    // Inflates the menu in the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_new_bathing_site, menu);

        return true;
    }


    /* Function to set output date from chosen date in calendar */
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            dateTemp.setText("" + year + "/" + monthOfYear + "/" + dayOfMonth);
    }

    /* Datepicker fragment class - used for user to display a calendar to pick date from */
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private int mYear;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new  DatePickerDialog(getActivity(),this,year,month,day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            ((DatePickerDialog.OnDateSetListener) getActivity()).onDateSet(view, year,
                    month, dayOfMonth);

        }

        public int getDate(){
            return mYear;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                // Save contact information
                if (checkFields()){                                         // Verify that fields are filled in correctly
                    String message = "";
                    message += getResources().getString(R.string.name) + nameInput.getText() + "\n";
                    message += getResources().getString(R.string.desc) + descInput.getText() + "\n";
                    message += getResources().getString(R.string.address) + addressInput.getText() + "\n";
                    message += getResources().getString(R.string.longit) + longInput.getText() + "\n";
                    message += getResources().getString(R.string.latit) + latInput.getText() + "\n";
                    message += getResources().getString(R.string.grade) + theRating + "\n";
                    message += getResources().getString(R.string.water_temp) + waterTemp.getText() + "\n";
                    message += getResources().getString(R.string.date_temp) + dateTemp.getText() + "\n";

                    snackBar(message);                                               // Show snackbar information
                    doSave();                                   // Call save function


                }

                return true;

            case R.id.action_clear:
                // Clear data
                clearAll();
                return true;

            case R.id.action_weather:
                // Button to get weather

                if (checkFields()) {                    // Call function to check if fields are filled in
                    String weatherURL = inputToURL();                   // Call function to retrieve URL
                    progressDialog = new ProgressDialog(this);          // create new progressDialog
                    progressDialog.setMax(100);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);           // Sets progressbar to show percent
                    //Log.wtf("input location",weatherURL);
                    new AsyncParser().execute(weatherURL);                              // Launch new thread to retrieve weather
                }
                return true;

            case R.id.settings:
                // Starts Settings- activity to change options
                startActivity(new Intent(NewBathingSiteActivity.this,SettingsActivity.class));
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /* Function to modify the URL based on user input */
    private String inputToURL(){
        String baseURL = preferences.getString("weatherurl","");                            // Retrieve the URL preference

        if (TextUtils.isEmpty(longInput.getText()) && TextUtils.isEmpty(latInput.getText())){       // If the long/lat input is not availible, use the address
            String subAddress = addressInput.getText().toString();
            baseURL += subAddress;
        } else {
            String latLong = longInput.getText() + "|" + latInput.getText();                     // Else, use the lat/long, ensures that lat/long is prioritized
            baseURL += latLong;
        }

        baseURL += "&" + getResources().getString(R.string.lang_se);                                // Add the language string to the url

        return baseURL;                         // Return the full working address
    }

    /* Function to show the datepicker on click */
    public void showDatePickerDialog(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(),"datePicker");

    }

    /* Function to clear all fields fast - calls function to clear field */
    private void clearAll(){
        clearFields(nameInput);
        clearFields(descInput);
        clearFields(addressInput);
        clearFields(longInput);
        clearFields(latInput);
        clearFields(waterTemp);
        clearFields(dateTemp);
    }

    /* Clears the current editText-field */
    private void clearFields(EditText editText){
        editText.getText().clear();
    }

    /* Generic function to display a snackbar with parameter as message */
    private void snackBar(String message){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView tv= (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setMaxLines(9);
        snackbar.show();
    }

    /* A listener for the ratingbar, updates the rating when user uses the ratingbar */
    private void ratingBarListener(){

        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                ratingValue = rating;                                   // Stores rating as float
                theRating = String.valueOf(rating);                     // Stores rating as string
            }
        });
    }

    /* Check if any of the values in the array are empty */
    private boolean checkEmpty(String[] stringArray){
        boolean notEmpty = true;
        for (int i = 0; i < stringArray.length; i++){
            if (TextUtils.isEmpty(stringArray[i])){
                notEmpty = false;
            }
        }

        return notEmpty;
    }

    /* Function to check the datatype input for those fields - ensure invalid user input does not crash app */
    private boolean checkFormatting(){
        boolean good = true;

        if (!extraCheckDouble(waterTemp)){                          // All fields needs to be numberic in order to pass
            good = false;
        }
        if (!extraCheckDouble(longInput)){
            good = false;
        }
        if (!extraCheckDouble(latInput)){
            good = false;
        }

        return good;
    }



    /* Function to check fields and display error */
    private boolean checkFields() {
        boolean good = true;

        if (TextUtils.isEmpty(nameInput.getText())) {
            nameInput.setError(getResources().getString(R.string.name_error));
            good = false;
        }
        // If both Address and lat/long are not filled in, display error
        if (TextUtils.isEmpty(addressInput.getText()) && TextUtils.isEmpty(longInput.getText()) && TextUtils.isEmpty(latInput.getText())){
            addressInput.setError(getResources().getString(R.string.address_error));
            good = false;
        }
        // If address and long is not filled in, display error
        if (TextUtils.isEmpty(longInput.getText()) && TextUtils.isEmpty(addressInput.getText())) {
            longInput.setError(getResources().getString(R.string.longit_error));
            good = false;
        }
        // If address and latit is not filled in, display error
        if (TextUtils.isEmpty(latInput.getText()) && TextUtils.isEmpty(addressInput.getText())){
            latInput.setError(getResources().getString(R.string.latit_error));
            good = false;
        }

        return good;
    }

    /* Generic function to display errormessage as toast */
    public void showError(){
        Toast.makeText(getBaseContext(), currentError , Toast.LENGTH_SHORT).show();
    }



    /* Function to check if value can be parsed as double - verifies invalid user input that is not numeric */
    private boolean extraCheckDouble(EditText inputField){
        boolean extra = true;

        try {
            double value = Double.parseDouble(inputField.getText().toString());
        } catch (NumberFormatException e){
            inputField.setError(getResources().getString(R.string.invalid_format));
            extra = false;
        }

        return extra;
    }
    /* Inner class that connects to weather site and retrieve weatherdata */
    private class AsyncParser extends AsyncTask<String,Double,String> {

        private String imageURL;
        Bitmap weatherIMG;
        ProgressDialog mProgressDialog;
        private ArrayList arrayList = new ArrayList();
        AlertDialog alertDialog;
        ImageView dialogImage;

        @Override
        protected String doInBackground(String... params) {
            URL url;
            String response = "";
            try {

                if (!URLUtil.isValidUrl(params[0])){            // Check if the input string is URL
                    cancel(true);           // If not, abort thread
                }


                url = new URL(params[0]);               // If URL is an address, create URL object



                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){            // Check that connection can be established, else cancel thread

                    cancel(true);

                }


                String line;
                int progress = 0;
                int contentLenght = conn.getContentLength();            // Calculate lenght of connetion
                double percent;



                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));       // Create bufferedreader to read from Inpustream, the connection

                while ((line = reader.readLine()) != null) {            // Read until the end of file
                    response += line;

                    if (response.contains("null")){                     // If any of the values are null, abort the download and stop the thread
                        currentError = getResources().getString(R.string.location_error);           // Display error that location is invalid
                        cancel(true);

                    }

                    arrayList.add(line);                                    // Add the read line from weather script file

                    progress += line.getBytes("ISO-8859-2").length +2;          // convert progress to number of bytes

                    percent = (double) progress / contentLenght;            // Convert to percent of the total file lenght

                    publishProgress(percent*100);                           // Call to update progress of file download


                }

            } catch (Exception e) {
                e.printStackTrace();
                currentError = getResources().getString(R.string.server_error);         // If any error occur during retrieval, cancel the download and stop task
                cancel(true);
            }

            imageURL = parseImage(response);            // Call function to retrieve the image

            weatherIMG = getBitmapFromURL(imageURL);        // Call function to retrieve bitmap from image file link


            return response;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mProgressDialog.cancel();               // Cancel the progressdialog and show error message
            showError();
        }

        /* Parse the link to the image based on input */
        private String parseImage(String input){
            String imageUrl = "";
            imageUrl = input.substring(input.indexOf("image") + 6, input.indexOf("gif") + 3);
            Log.wtf("image",imageUrl);
            return imageUrl;
        }

        /* Parse the temperature from input */
        private String parseTemp(String input){
            String temp = "";
            temp = input.substring(input.indexOf("temp_c"), input.indexOf("<br>"));
            Log.w("temp:",temp);
            return temp;
        }

        /* Download and craete bitmap object based on image link */
        public Bitmap getBitmapFromURL(String src) {
            try{
                URL url = new URL(src);                                                     // Open new connection to image location
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();                     // Get the inputstream data to save to file
                Bitmap newBitmap = BitmapFactory.decodeStream(inputStream);                 // Crate new bitmap from the input stream

                return newBitmap;                                                           // Return the bitmap
            } catch (IOException e){

                return null;
            }
        }

        /* Called before Task is started - prepares dialog */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();



            alertDialog = new AlertDialog.Builder(NewBathingSiteActivity.this).create();        // Crate alertDialog to display weather info
            mProgressDialog = new ProgressDialog(NewBathingSiteActivity.this);              // Setup the progressDialog
            mProgressDialog.setTitle(getResources().getString(R.string.get_weather));
            mProgressDialog.setMessage(getResources().getString(R.string.download_text));
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.show();

        }

        @Override
        protected void onProgressUpdate(Double... values) {
            super.onProgressUpdate(values);
            int y = (int)Math.round(values[0]);             // Cast double to int

            mProgressDialog.setProgress(y);                 // Display percentage downloaded

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            mProgressDialog.dismiss();                                  // Remove the progressdialog on finish

            String currLocation = (String) arrayList.get(0);                    // Parse the retrieved data
            currLocation = Helper.trimString(currLocation,"address:");          // Calls Helper class to trim and display data
            currLocation = Helper.newline(currLocation);

            String currLat = (String) arrayList.get(1);
            currLat = Helper.newline(currLat);

            String currLong = (String) arrayList.get(2);
            currLong = Helper.newline(currLong);

            String currCond = (String) arrayList.get(3);
            currCond = Helper.trimString(currCond,"condition:");
            currCond = Helper.newline(currCond);

            String currTemp = (String) arrayList.get(4);
            currTemp = Helper.trimString(currTemp,"temp_c:");
            currTemp += "\u00B0 C";
            currTemp = Helper.trimString(currTemp,"<br>");


            String currHumidity = (String) arrayList.get(5);
            currHumidity = Helper.newline(currHumidity);

            String currWind = (String) arrayList.get(6);
            currWind = Helper.newline(currWind);

            String currKph = (String) arrayList.get(7);
            currKph = Helper.trimString(currKph,"wind_kph:");
            currKph = Helper.trimString(currKph,"<br>");
            currKph += getResources().getString(R.string.wind_speed) + "\n";

            alertDialog.setTitle(getResources().getString(R.string.weather_dialog_title));
            alertDialog.setMessage(currLocation + currLat + currLong + currKph + currCond + currTemp );     // Show the formatted data in alertDialog-message
            dialogImage = new ImageView(NewBathingSiteActivity.this);                           // Create imageView

            dialogImage.setImageBitmap(weatherIMG);                             // set Image to the retrieved weather
            alertDialog.setView(dialogImage);                                   // set view to the weather image

            Drawable icon = new BitmapDrawable(getResources(),weatherIMG);          // Crate an icon from the image

            alertDialog.setIcon(icon);                                      // Sets icon and show the dialog

            alertDialog.show();

        }



    }



}
