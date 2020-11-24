package com.example.xpark;

import androidx.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

public class CarPark {

    private String id;
    private String name;
    private String phone;
    private double latitude;
    private double longitude;
    private int capactiy;
    private int used;

    /**
     * Initialize carPark object with given data snap shot - comes from database -
     * @param shot Data comes from real time database.
     */
    public CarPark(DataSnapshot shot)
    {
        this.longitude = (double)shot.child(FirebaseDBConstants.DB_CARPARK_INFO).child(FirebaseDBConstants.DB_CARPARK_CHILD_LONGITUDE).getValue();
        this.latitude = (double)shot.child(FirebaseDBConstants.DB_CARPARK_INFO).child(FirebaseDBConstants.DB_CARPARK_CHILD_LATITUDE).getValue();
        this.id = (String)shot.child(FirebaseDBConstants.DB_CARPARK_INFO).child(FirebaseDBConstants.DB_CARPARK_CHILD_ID).getValue();
        this.capactiy = ((Long)(shot.child(FirebaseDBConstants.DB_CARPARK_INFO).child(FirebaseDBConstants.DB_CARPARK_CHILD_CAPACITY).getValue())).intValue();
        this.used = ((Long)(shot.child(FirebaseDBConstants.DB_CARPARK_INFO).child(FirebaseDBConstants.DB_CARPARK_CHILD_USED).getValue())).intValue();
        this.name = (String)shot.child(FirebaseDBConstants.DB_CARPARK_INFO).child(FirebaseDBConstants.DB_CARPARK_CHILD_NAME).getValue();
        this.phone = (String)shot.child(FirebaseDBConstants.DB_CARPARK_INFO).child(FirebaseDBConstants.DB_CARPARK_CHILD_PHONE).getValue();
    }

    /**
     * Gets the ID of the car park.
     * @return ID of the car park.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the latitude of the car park on the map.
     * @return Latitude of the car park on the map.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude of the car park on the map.
     * @return Longitude of the car park on the map.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Gets the phone number of the car park.
     * @return Phone number of the car park.
     */
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @param used
     */
    public void setUsed(int used) {
        this.used = used;
    }

    /**
     * Gets the name of the car park.
     * @return Name of the car park.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the coordinates ot the car park as LatLng type.
     * @return Coordinates ot the car park as LatLng type.
     */
    public LatLng getCoordinates()
    {
        return new LatLng(this.latitude,this.longitude);
    }

    /**
     * Gets the number of the free parking are in the car park.
     * @return Number of the free parking are in the car park.
     */
    public int getFreeArea()
    {
        return capactiy - used;
    }

    @NonNull
    @Override
    /* TODO : change this in a better way */
    public String toString() {
        return "# CARPARK : " + " (" + longitude + "," + latitude + ") ";
    }
}
