package com.example.xpark.Activities;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.xpark.DataBaseProvider.FirebaseUserManager;
import com.example.xpark.Module.CarPark;
import com.example.xpark.DataBaseProvider.FirebaseCarparkManager;
import com.example.xpark.R;
import com.example.xpark.Module.User;
import com.example.xpark.Utils.ToastMessageConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.time.ZoneId;
import es.dmoral.toasty.Toasty;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    /* UI Components */
    private Button search_button;
    private Button res_button;
    private Button exit_button;
    private GoogleMap map;

    /* Managers, Wrappers and etc */
    private FirebaseCarparkManager DBparkManager;
    private FirebaseUserManager FBUserManager;

    /* logged in user */
    private User currentUser;

    // mapping between car park and marker on the screen
    private HashMap<Marker,CarPark> markersOnScreen;

    /* selected carpark on the map */
    private CarPark selectedCarpark;

    /* selected marker object in the map */
    private Marker selectedMarker;

    /* Lock for selected car park */
    private final Object markers_on_screen_lock = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        /* get logged user */
        init_logged_user();

        /* kullanıcının park edip etmediğini kontrol et */
        checkParkingStatus();

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

        /* initialize DB managers */
        DB_init();
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
                DBparkManager.setMap(map);

                /* update the camera to current location */
                animateCameraToCurrentLocation();
            } catch (SecurityException ex) {
                System.out.println("Ex occured : " + ex.getMessage());
                /* TODO : Handle error */
            }
            /* markera tiklayinca gelecek pencereyi ayarla.. */
            UI_init_mapMarkerType();

            /* marker secilince listener ekle */
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    synchronized (markers_on_screen_lock) {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        selectedCarpark = markersOnScreen.get(marker);
                        selectedMarker = marker;
                        System.out.println("CARPARK SELECT = " + selectedCarpark);
                        return false;
                    }
                }
            });

            map.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
                @Override
                public void onInfoWindowClose(Marker marker) {
                    synchronized (markers_on_screen_lock) {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker());
                        selectedCarpark = null;
                        selectedMarker = null;
                        System.out.println("CARPARK SELECT = marker kapatildi.");
                    }
                }
            });
        }
        else
        {
            /* Todo : map cannot be loaded, handle. */
        }
    }

    /**
     * Initialize UI components.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void UI_init()
    {
        setContentView(R.layout.activity_maps);
        search_button = findViewById(R.id.button_search);
        res_button = findViewById(R.id.button_res);
        exit_button = findViewById(R.id.button_exit);

        // markes on screen as hashmap
        markersOnScreen = new HashMap<>();

        /* search icin listener ekle */
        search_button.setOnClickListener(v -> {
            if(map != null) {
                /* search car park, in new thread. */
                DBparkManager.showNearestCarParks();
            }
        });

        /* rezerve butonu icin listener ekle */
        res_button.setOnClickListener(v -> {
            synchronized (markers_on_screen_lock) {
                if (selectedCarpark != null) {
                    LocalDateTime date = LocalDateTime.now(ZoneId.of("Europe/Istanbul"));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String time = formatter.format(date);
                    DBparkManager.startParking(selectedCarpark, currentUser, time);
                }
                else
                    Toasty.warning(this.getApplicationContext(), ToastMessageConstants.TOAST_MSG_ERROR_INVALID_CPARK_SELECT, Toast.LENGTH_SHORT).show();
            }
        });

        exit_button.setOnClickListener(v -> {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            settings.edit().clear().commit();

            Intent intent = new Intent(this, EntranceDebugActivity.class);
            this.startActivity(intent);
        });

        /* Todo : handle below */
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

                /* If it is selected marker, paint bg with GREEN */
                if(marker.equals(selectedMarker)) {
                    title.setBackgroundColor(Color.GREEN);
                    snippet.setBackgroundColor(Color.GREEN);
                }

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    private void DB_init()
    {
        this.FBUserManager = new FirebaseUserManager(this);
        this.DBparkManager = new FirebaseCarparkManager(this,markersOnScreen,markers_on_screen_lock);
    }

    private void init_logged_user()
    {
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("CURRENT_USER");
        System.out.println("USER GETTED : " + currentUser);
    }

    private void checkParkingStatus() {
        if(currentUser != null)
            if(!currentUser.getCarparkid().equals(User.NOT_PARKED)){
                this.startNextActivity();
            }
    }

    /*starts information activity*/
    private void startNextActivity(){
        Intent intent = new Intent(this, ParkingInformationActivity.class);
        intent.putExtra("CURRENT_USER",currentUser);
        this.startActivity(intent);
        finish();
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
        if(location == null){
            System.out.println("loc bulunamadi");
        }
        else {
            map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
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
