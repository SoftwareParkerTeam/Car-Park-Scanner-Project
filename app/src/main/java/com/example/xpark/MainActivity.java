package com.example.xpark;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    /* UI Components */
    private Button search_button;
    private GoogleMap map;

    /* Managers, Wrappers and etc */
    private FirebaseCarparkManager DBparkManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // crucial, don't remove.
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }

        /* first check the permissions */
        checkPermission();

        /* initialize ui components */
        UI_init();
    }

    /**
     * Initialize map and necessary map related tools when ready to load.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(map != null)
        {
            try {
                map.setMyLocationEnabled(true);
                DBparkManager = new FirebaseCarparkManager(map, getApplicationContext());

                /***** JUST FOR TEST *****/
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses = gcd.getFromLocation(40.87137360618015, 29.256863354935692, 1);
                    if (addresses != null && addresses.size() > 0) {
                        System.out.println("ILCE2 :: " + DBparkManager.parseAddressToDistrict(addresses.get(0).getAddressLine(0)));
                    }
                } catch (IOException ex) {
                    /* TODO : Handle error */
                }
                /***** JUST FOR TEST *****/

                /* update the camera to current location */
                animateCameraToCurrentLocation();
            } catch (SecurityException ex) {
                System.out.println("Ex occured : " + ex.getMessage());
                /* TODO : Handle error */
            }
            /* markera tiklayinca gelecek pencereyi ayarla.. */
            UI_init_mapMarkerType();
        }
        else
        {
            /* Todo : map cannot be loaded, handle. */
        }
    }

    /**
     * Initialize UI components.
     */
    private void UI_init()
    {
        setContentView(R.layout.activity_main);
        search_button = findViewById(R.id.button_search);

        /* search icin listener ekle */
        search_button.setOnClickListener(v -> {
            if(map != null) {
                /* yakin bolgede otopark ara */
                DBparkManager.showNearestCarParks();
            }
        });

        /**** just for test ****/
        Button res_button = findViewById(R.id.button_res);
        res_button.setOnClickListener(v -> {
            CarPark park = new CarPark();
            park.setId("1000000");
            park.setCoordinates(new LatLng(40.87763699311756,29.231608160645568));
            DBparkManager.registerUserToCarpark(park,new User());
        });
        /**** just for test ****/

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        mapFragment.getMapAsync(this);
    }

    /**
     * Setups, design stuff of the UI of the markers on the Google Map.
     * Call this in UI_init() method in order to design the marker type.
     */
    private void UI_init_mapMarkerType()
    {
        if(map == null)
            return;

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }



    /**
     * Checks the necessary permissions on RUNTIME. If there are missing permission,
     * creates requests for getting permission from user.
     */
    private void checkPermission()
    {
        if (!(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED))
        {
            Toast.makeText(this, "Konum servisleri kullanilamiyor..", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    10);
        }
    }

    private void animateCameraToCurrentLocation()
    {
        if(map == null)
            return;

        Location location = DBparkManager.getLastKnownLocation();
        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /* Below is Android stuff.. */
    @Override
    protected void onSaveInstanceState(Bundle outState) { super.onSaveInstanceState(outState); }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) { super.onRestoreInstanceState(savedInstanceState); }
    @Override
    protected void onPause(){super.onPause();}
    protected void onResume() { super.onResume(); }
    @Override
    protected void onStop() { super.onStop(); }
    @Override
    public void onBackPressed() {/* Crucial, ignore the back button */}
}