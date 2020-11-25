package com.example.xpark;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FirebaseCarparkManager {

    private static final String DB_CARPARK_FIELD = FirebaseDBConstants.DB_CARPARK_FIELD;
    private GoogleMap map;
    private Context cont;

    // store the related listeners about database fields.
    private ArrayList<ChildEventListener> listeners;

    // mapping between car park and marker on the screen
    private HashMap<CarPark,Marker> markersOnScreen;

    public FirebaseCarparkManager(GoogleMap map, Context cont)
    {
        this.map = map;
        this.cont = cont;
        listeners = new ArrayList<>();
        markersOnScreen = new HashMap<>();
    }

    private void showNearestCarParks(String districtName)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(DB_CARPARK_FIELD).child(districtName);

        /* adds event listener for given district, start observe */
        this.addListenerForDistrict(districtName);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot shot : snapshot.getChildren())
                {
                    // to find the center point of the car parks in the map
                    int markCount = 0;
                    double total_latitude = 0;
                    double total_longitude = 0;

                    CarPark newCarPark = new CarPark(shot);
                    total_latitude += newCarPark.getCoordinates().latitude;
                    total_longitude += newCarPark.getCoordinates().longitude;
                    addCarparkToMap(newCarPark,newCarPark.getName(),newCarPark.toString());
                    ++markCount;

                    /* focus on the center of the car parks (marks) */
                    if(markCount > 0)
                        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(total_latitude / markCount, total_longitude / markCount)));
                    else
                    {
                        /* TODO : otopark bulunamadi, ekrana guzel bilgi ver... */
                        Toast.makeText(cont,"Bolgede Otopark Bulunamadi..",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Shows the closest car parks in the given location.
     * UI, user space, call this method by giving the current location of the user.
     * @param currentLocation Location will be searched.
     */
    public void showNearestCarParks(Location currentLocation)
    {
        /* try to find district name from current location */
        String parsedAddr = tryToParseAddress(currentLocation.getLatitude(),currentLocation.getLongitude());
        if(parsedAddr != null)
            this.showNearestCarParks(parsedAddr);
        else
        {
            /* TODO : print error to the toast, carpark not found */
            Toast.makeText(cont,"Bolgede Otopark Bulunamadi..",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Adds new mark to the google map related car park.
     * @param newCarPark new car park object to be added on the map.
     * @param title Title of the map.
     * @param park_info String in the marker, explanation about related car park.
     * @return Newly added marker to google map.
     */
    private Marker addCarparkToMap(CarPark newCarPark,String title, String park_info)
    {
        Marker m = map.addMarker(new MarkerOptions().position(newCarPark.getCoordinates()).title(newCarPark.getName()).snippet(park_info));
        markersOnScreen.put(newCarPark,m);
        return m;
    }

    /**
     * Observer. Adds listener.
     * Starts to listen the close environment in order to catch the updates.
     * This is for keeping the screen (map) update.
     * If any updates caught, the listener will handle it by updating the google map.
     * @param districtName District name to be listen.
     */
    private void addListenerForDistrict(String districtName)
    {
        /* get database reference */
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FirebaseDBConstants.DB_CARPARK_FIELD).child(districtName);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                System.out.println(">> BOLGEDE DEGISIKLIK OLDU <<");
                System.out.println(snapshot);

                CarPark newPark = new CarPark(snapshot);
                /* get related marker on the map */
                Marker marker = markersOnScreen.get(newPark);

                /* remove it */
                marker.remove();

                /* add new marker to the map */
                addCarparkToMap(newPark,newPark.getName(),newPark.toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        this.listeners.add(listener);
        ref.addChildEventListener(listener);
    }

    public void startParking(CarPark carpark, User user)
    {
        // after checking the balance and etc..
        // call registerUserToCarpark here..
    }

    /**
     * This methods register given user to given car park.
     * Field of the car park in the database does not store the users.
     * Users store the id of the car park which he / she parked.
     *
     * This method registers user to car park "directly", not checks the balance or anything..
     * After implementing the startParking method, this method will be private since this
     * method should not be accessible from user space (UI).
     *
     * @param carpark Target car park.
     * @param user User to be registered.
     */
    public void registerUserToCarpark(CarPark carpark, User user)
    {
        /* first find the reference of the given car park */
        String parsedAddr = tryToParseAddress(carpark.getCoordinates().latitude,carpark.getCoordinates().longitude);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FirebaseDBConstants.DB_CARPARK_FIELD).child(parsedAddr).child(carpark.getId());

        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                /* get car park object from database */
                HashMap map = (HashMap)currentData.getValue();
                if(map == null)
                    return Transaction.success(currentData);

                /* get park object from data base */
                CarPark park = new CarPark((HashMap) currentData.getValue());
                /* check available space */
                if(park.getFreeArea() > 0)
                {
                    /* increment used counter */
                    park.incrementUsed();
                    /* update the database */
                    currentData.setValue(park);

                    return Transaction.success(currentData);

                    /* todo : register the car park id to user */
                }
                else
                    return Transaction.abort();
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                System.out.println("Commit check : " + committed + " " + currentData.getValue());
                /* Todo some concurrency error occurred, say user to try again */
            }
        });
    }



    /**
     * Get District (ilce) from given address.
     * @param inputAdress address to be parsed.
     * @return District of given address, if parsed successfully.
     */
    public String parseAddressToDistrict(String inputAdress)
    {
        String[] tokens = inputAdress.split(", ");
        String res = tokens[2].replaceAll("/","_");
        return res.replaceAll(" ","_");
    }

    /**
     * Checks that address is regular address or not.
     * show nearest parks method uses this helper method.
     * @param adress address to be checked
     * @return True if address is successfully parsable.
     */
    private boolean isAdressParsed(String adress)
    {
        if(adress == null)
            return false;

        int counter = 0;
        for (int i = 0; i < adress.length(); i++) {
            if(adress.charAt(i) == '_')
                ++counter;
        }
        return counter == 2;
    }

    /**
     * If adress is not parsable, try to get some meaningful data.
     * @param latitude Latitude of the given address.
     * @param longitude Longitude of the given address.
     * @return parsed address if successful, null on error.
     */
    private String tryToParseAddress(double latitude, double longitude)
    {
        Geocoder gcd = new Geocoder(cont, Locale.getDefault());
        final double INCREMENT_AMOUNT = 0.001;
        final int MAX_TRY = 100;
        int i = 0;
        String parsedAddr = null;
        try {
            /* if address is not parsed yet, try to find */
            while(!isAdressParsed(parsedAddr) && i < MAX_TRY)
            {
                List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0)
                    parsedAddr = parseAddressToDistrict(addresses.get(0).getAddressLine(0));

                if(i++ % 2 == 0)
                    longitude += INCREMENT_AMOUNT;
                else
                    latitude += INCREMENT_AMOUNT;
            }
        }
        catch (IOException ex)
        {
            /* TODO : Handle error */
        }

        return parsedAddr;
    }
}
