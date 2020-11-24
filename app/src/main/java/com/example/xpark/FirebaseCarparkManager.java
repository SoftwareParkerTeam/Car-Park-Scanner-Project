package com.example.xpark;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FirebaseCarparkManager {

    private static final String DB_CARPARK_FIELD = FirebaseDBConstants.DB_CARPARK_FIELD;
    private GoogleMap map;
    private Context cont;

    public FirebaseCarparkManager(GoogleMap map, Context cont)
    {
        this.map = map;
        this.cont = cont;
    }

    public void showNearestCarParks(String districtName)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(DB_CARPARK_FIELD).child(districtName);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // to find the center point of the car parks in the map
                int markCount = 0;
                double total_latitude = 0;
                double total_longitude = 0;

                for(DataSnapshot shot : snapshot.getChildren())
                {
                    CarPark newCarPark = new CarPark(shot);
                    total_latitude += newCarPark.getCoordinates().latitude;
                    total_longitude += newCarPark.getCoordinates().longitude;
                    String park_info = "Musait Yer : " + newCarPark.getFreeArea() + "\n" + "Telefon : " + newCarPark.getPhone();
                    map.addMarker(new MarkerOptions().position(newCarPark.getCoordinates()).title(newCarPark.getName()).snippet(park_info));
                    ++markCount;
                }

                /* focus on the center of the car parks (marks) */
                if(markCount > 0)
                    map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(total_latitude / markCount, total_longitude / markCount)));
                else
                {
                    /* TODO : otopark bulunamadi, ekrana guzel bilgi ver... */
                    Toast.makeText(cont,"Bolgede Otopark Bulunamadi..",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showNearestCarParks(Location currentLocation)
    {
        /* try to find district name from current location */
        final double INCREMENT_AMOUNT = 0.001;

        Geocoder gcd = new Geocoder(cont, Locale.getDefault());
        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();
        int i = 0;
        String parsedAddr = null;
        try {
            /* if address is not parsed yet, try to find */
            while(!isAdressParsed(parsedAddr))
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

        if(parsedAddr != null)
            this.showNearestCarParks(parsedAddr);
        else
        {
            /* TODO : print error to the toast, carpark not found */
            Toast.makeText(cont,"Bolgede Otopark Bulunamadi..",Toast.LENGTH_SHORT).show();
        }
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
}
