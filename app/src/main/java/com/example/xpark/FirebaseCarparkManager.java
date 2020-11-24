package com.example.xpark;

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

public class FirebaseCarparkManager {

    private static final String DB_CARPARK_FIELD = FirebaseDBConstants.DB_CARPARK_FIELD;

    private GoogleMap map;

    public FirebaseCarparkManager(GoogleMap map)
    {
        this.map = map;
    }

    public void showNearestCarParks(String districtName)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(DB_CARPARK_FIELD).child(districtName);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int markCount = 0;
                double total_latitude = 0;
                double total_longitude = 0;
                for(DataSnapshot shot : snapshot.getChildren()) {
                    CarPark newCarPark = new CarPark(shot);
                    total_latitude += newCarPark.getCoordinates().latitude;
                    total_longitude += newCarPark.getCoordinates().longitude;
                    String park_info = "Musait Yer : " + newCarPark.getFreeArea() + "\n" + "Telefon : " + newCarPark.getPhone();
                    map.addMarker(new MarkerOptions().position(newCarPark.getCoordinates()).title(newCarPark.getName()).snippet(park_info));
                    ++markCount;
                }

                /* focus on the center of the car parks (marks) */
                map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(total_latitude / markCount, total_longitude / markCount)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
