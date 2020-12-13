package com.example.xpark.Module;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.xpark.DataBaseProvider.FirebaseDBConstants;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

public class CarPark {
    private String id;
    private String generalid;
    private String name;
    private String phone;
    private LatLng coordinates;
    private int capacity;
    private int used;

    public CarPark(){
    }

    /**
     * Initialize carPark object with given data snap shot - comes from database -
     * @param shot Data comes from real time database.
     */
    public CarPark(DataSnapshot shot)
    {
        double longitude = (double)shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_COORDINATES).child(FirebaseDBConstants.DB_CARPARK_CHILD_LONGITUDE).getValue();
        double latitude = (double)shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_COORDINATES).child(FirebaseDBConstants.DB_CARPARK_CHILD_LATITUDE).getValue();
        this.coordinates = new LatLng(latitude,longitude);
        this.id = (String)shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_ID).getValue();
        this.capacity = ((Long)(shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_CAPACITY).getValue())).intValue();
        this.used = ((Long)(shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_USED).getValue())).intValue();
        this.name = (String)shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_NAME).getValue();
        this.phone = (String)shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_PHONE).getValue();
        this.generalid = (String)shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_GENERALID).getValue();
    }

    /**
     * Initialize carPark object with given hash map - comes from database -
     * @param map Map to be parsed into car park object.
     */
    public CarPark(HashMap map)
    {
        HashMap coord_map = (HashMap) map.get(FirebaseDBConstants.DB_CARPARK_CHILD_COORDINATES);
        double longitude = (double)coord_map.get(FirebaseDBConstants.DB_CARPARK_CHILD_LONGITUDE);
        double latitude = (double)coord_map.get(FirebaseDBConstants.DB_CARPARK_CHILD_LATITUDE);
        this.coordinates = new LatLng(latitude,longitude);
        this.capacity = ((Long)map.get(FirebaseDBConstants.DB_CARPARK_CHILD_CAPACITY)).intValue();
        this.used = ((Long)map.get(FirebaseDBConstants.DB_CARPARK_CHILD_USED)).intValue();
        this.id = (String)map.get(FirebaseDBConstants.DB_CARPARK_CHILD_ID);
        this.phone = (String)map.get(FirebaseDBConstants.DB_CARPARK_CHILD_PHONE);
        this.name =  (String)map.get(FirebaseDBConstants.DB_CARPARK_CHILD_NAME);
        this.generalid = (String)map.get(FirebaseDBConstants.DB_CARPARK_CHILD_GENERALID);
    }

    /**
     * Gets the ID of the car park.
     * @return ID of the car park.
     */
    public String getId() {
        return id;
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
        return coordinates;
    }

    /**
     * Gets the number of the free parking are in the car park.
     * @return Number of the free parking are in the car park.
     */
    public int getFreeArea()
    {
        return getCapacity() - getUsed();
    }

    public String getGeneralid() {
        return generalid;
    }

    public void setGeneralid(String general_id) {
        this.generalid = general_id;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getUsed() {
        return used;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void incrementUsed(){
        this.setUsed(this.getUsed() + 1);
    }


    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    @NonNull
    @Override
    /* TODO : change this in a better way */
    public String toString()
    {
        return "Tel : " + this.getPhone() + "\n" + "Boş Yer : " + this.getFreeArea();
    }

    @Override
    public int hashCode()
    {
        String s = " " + coordinates.latitude + coordinates.longitude;
        return s.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        CarPark test = (CarPark)obj;
        return test.hashCode() == this.hashCode();
    }

}
