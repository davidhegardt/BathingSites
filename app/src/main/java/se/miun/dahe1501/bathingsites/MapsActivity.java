package se.miun.dahe1501.bathingsites;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.jar.Manifest;

/*
*   Maps Activity - displays bathingsits in a Google maps activity.
*   Retrieves entrys from database updates map and implements
*   location listener. Displays sites based on users current position.
*   Calls function to update sites when user moves.
*
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    /* Inner class used to display when user clicks on marker */
    class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

        private final View contentsView;

        CustomInfoWindow(){

            this.contentsView = getLayoutInflater().inflate(R.layout.custom_info_window,null);          // Inflate custom info window-layout
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tvTitle = ((TextView)contentsView.findViewById(R.id.title));                   // Sets up TextView and display marker title as heading
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView)contentsView.findViewById(R.id.snippet));           // Retrieves marker snippet to display as description
            tvSnippet.setText(marker.getSnippet());
            LatLng current = marker.getPosition();
            Location.distanceBetween();

            return contentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }







    private GoogleMap mMap;
    ArrayList<String> longList = new ArrayList<String>();
    ArrayList<String> latitList = new ArrayList<String>();
    ArrayList<LatLng> coordinates = new ArrayList<>();
    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> descList = new ArrayList<>();
    ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<String> addressList = new ArrayList<>();
    ArrayList<String> ratingList = new ArrayList<>();
    ArrayList<String> dateList = new ArrayList<>();
    ArrayList<String> tempList = new ArrayList<>();
    Circle circleRadius;

    private SharedPreferences preferences;
    private static final int MY_PERMISSION_ACCESS_LOCATION = 1;
    Location home;

    int radius;

    private DatabaseCreator myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myData = new DatabaseCreator(this);                                     // Create database object
        longList = myData.getColumnData("Longitude");                           // Retrieve all database column data and place in arrays
        latitList = myData.getColumnData("Latitude");
        nameList = myData.getColumnData("Name");
        descList = myData.getColumnData("Description");
        addressList = myData.getColumnData("Address");
        ratingList = myData.getColumnData("Rating");
        dateList = myData.getColumnData("Date");
        tempList = myData.getColumnData("Temp");


        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());       // Retrieve preferences

        radius = Integer.parseInt(preferences.getString("radius",""));                  // Parse and retrieve radius

        createMarkers();


    }

    /* Function to parse string data and store as LatLng objects */
    public void createMarkers(){
        ArrayList<Double> doubleLongs = new ArrayList<>();          // Array used to store Longitude values
        ArrayList<Double> doubleLats = new ArrayList<>();           // latitude values

        double currLat = 0;
        double currLong = 0;
        for (String s : longList){                                  // Loop strings of longitudes
            currLong = Double.valueOf(s);                           // cast to Dobule and store in array
            doubleLongs.add(currLong);
        }

        for (String s : latitList){
            currLat = Double.valueOf(s);
            doubleLats.add(currLat);
        }



        for (int i = 0; i < doubleLongs.size(); i++){
            LatLng current = new LatLng(doubleLats.get(i),doubleLongs.get(i));      // Loop both arrays of double and create LatLng
            coordinates.add(current);                                       // objects and add to array
        }
    }

    /* Function to add all markers and display them within radius of user location */
    public void addMarkers(){

        Iterator<LatLng> coordIterator = coordinates.iterator();            // Create iterator from LatLng objects
        Iterator<String> nameIterator = nameList.iterator();                // Create iterator from Name of bathingsites


        LatLng hogdalen = new LatLng(59.263302,18.043111);              // Default position if GPS not avalible
        //LatLng klimpfjall = new LatLng(65.063565,14.80894);
        //LatLng hogakusten = new LatLng(62.802981,17.95173);
        //LatLng lund = new LatLng(55.70466,13.19100);

        // This loops the coordinates and names. Creates new Markers and adds them to the map. The markers
        // are then stored in the markers-arraylist. For each marker, the latLng value is added and as title the name is added.
        // Also sets the markers to be Invisible
        while (coordIterator.hasNext() && nameIterator.hasNext()){
            Marker marker = mMap.addMarker(new MarkerOptions().position(coordIterator.next()).title(nameIterator.next()).visible(false));
            markers.add(marker);
        }

        // Loop and add extra information for bathing sites - if availible
        for (int i = 0; i < markers.size(); i++){

            if (!addressList.get(i).contains("N/A")){               // If address is availible add to snippet for the current marker
                markers.get(i).setSnippet(addressList.get(i));
            }

            if (!descList.get(i).contains("N/A")) {
                String currSnippet = markers.get(i).getSnippet();       // If description is availible -"-
                markers.get(i).setSnippet(currSnippet + "\n" + descList.get(i));

            }
            if (!ratingList.get(i).contains("0.0")){                        // If the rating is not 0.0 we know that rating temperature and date is availible, add to snippet
                String currSnippet = markers.get(i).getSnippet();
                markers.get(i).setSnippet(currSnippet + "\n" + getResources().getString(R.string.rating) + ratingList.get(i));
                String newSnippet = markers.get(i).getSnippet();
                markers.get(i).setSnippet(newSnippet + "\n" + getResources().getString(R.string.water_temp) + tempList.get(i) + "\u00B0 C" + "\n" + getResources().getString(R.string.date_temp) + dateList.get(i));
            }
        }

        LatLng startCoord;          // User start-position as LatLng object
        if (home != null) {
             startCoord = new LatLng(home.getLatitude(), home.getLongitude());          // Create latLng object from user position
        } else {
            startCoord = hogdalen;                  // If GPS not availible, set Start to Högdalen
        }


        circleRadius = mMap.addCircle(new CircleOptions().center(startCoord).radius(radius).strokeColor(Color.CYAN));       // Add circle to show radius

        // Function to loop and check if latlng for bathingsite is within radius of users position
        for (Marker site : markers){
            if (SphericalUtil.computeDistanceBetween(startCoord,site.getPosition()) < radius) {     // Use Google library to check if marker is within the radius of start-position
                site.setVisible(true);                          // Set the site (marker) to be visible

            }
        }

        mMap.setInfoWindowAdapter(new CustomInfoWindow());


        mMap.moveCamera(CameraUpdateFactory.newLatLng(startCoord));     // Move camera to the users position
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));          // Zoom into user position


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * Gets the GPS-provider and sets up location for user - if allowed.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {       // Checks if permission is allowed

                mMap.setMyLocationEnabled(true);                        // If permission is allowed, enable users location
                mMap.getUiSettings().setMyLocationButtonEnabled(true);          // Show my location-button

                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);         // Retrieve GPS service
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 500, this);          // Add a listener on when to update the map - 3000(3sek), 500(meters)

                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = locationManager.getBestProvider(criteria, true);                  // Retrieve the GPS provider

                home = locationManager.getLastKnownLocation(provider);                              // Retrieve users last known location

                addMarkers();                                                   // Call function to add markers to the map

                if (home != null) {
                    double latit = home.getLatitude();

                    double longit = home.getLongitude();

                    LatLng position = new LatLng(latit, longit);

                    mMap.addMarker(new MarkerOptions().position(position).title("Home"));           // Add a marker from where user started - if user gets lost
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position));                       // Move camera to users start position

                }

                UiSettings settings = mMap.getUiSettings();
                settings.setZoomControlsEnabled(true);                                              // Enable the zoom-controls
            } else {
                checkPermissionLocation();                          // If permission is not granted call function

            }


    }

    /* Function called to check permission for user */
    private void checkPermissionLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {       // If permission is not granted

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {          // Show request to enable location
                new AlertDialog.Builder(this)                                                                               // Show explenation on why app needs location
                        .setTitle(getResources().getString(R.string.permission_title))
                        .setMessage(getResources().getString(R.string.permission_message))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSION_ACCESS_LOCATION );                                                    // Enable location
                            }
                        })
                        .create()
                        .show();

            } else {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_ACCESS_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MY_PERMISSION_ACCESS_LOCATION : {                                                                      // If user accepts to enable GPS, setup the map
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);

                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,500,this);

                        Criteria criteria = new Criteria();
                        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                        String provider = locationManager.getBestProvider(criteria,true);

                        Location home = locationManager.getLastKnownLocation(provider);

                        addMarkers();


                        if ( home != null) {
                            double latit = home.getLatitude();

                            double longit = home.getLongitude();

                            LatLng position = new LatLng(latit,longit);

                            mMap.addMarker(new MarkerOptions().position(position).title("Home"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

                        }

                        UiSettings settings = mMap.getUiSettings();
                        settings.setZoomControlsEnabled(true);


                    }

                }
            }
        }
    }

    /* This function is called on user movement - based on the listener species when the function is called */
    @Override
    public void onLocationChanged(Location location) {
        if (circleRadius != null){
            circleRadius.remove();                                      // Remove the currently drawn circle
        }
        LatLng locationCoord = new LatLng(location.getLatitude(),location.getLongitude());                          // Get the new updated location
        circleRadius = mMap.addCircle(new CircleOptions().center(locationCoord).radius(radius).strokeColor(Color.CYAN));        // Create a new circle with user in center

        for (Marker site : markers){                                                                    // Loop markes
            if (SphericalUtil.computeDistanceBetween(locationCoord,site.getPosition()) < radius) {      // Check if within radius of user
                site.setVisible(true);                                                                  // Show the site
            } else {
                site.setVisible(false);                                                     // Else, hide the site
            }
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
