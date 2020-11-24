package com.example.xpark;

import androidx.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

public class CarPark {

    private String id;
    private String name;
    private String phone;
    private String province;
    private String district;
    private double latitude;
    private double longitude;
    private int capactiy;
    private int used;

    public CarPark(DataSnapshot shot)
    {
        this.longitude = (double)shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_LONGITUDE).getValue();
        this.latitude = (double)shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_LATITUDE).getValue();
        this.id = (String)shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_ID).getValue();
        this.capactiy = ((Long)(shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_CAPACITY).getValue())).intValue();
        this.used = ((Long)(shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_USED).getValue())).intValue();
        this.name = (String)shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_NAME).getValue();
        this.phone = (String)shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_PHONE).getValue();
        this.province = (String)shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_PROVINCE).getValue();
        this.district = (String)shot.child(FirebaseDBConstants.DB_CARPARK_CHILD_DISTRICT).getValue();
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getCapactiy() {
        return capactiy;
    }

    public void setCapactiy(int capactiy) {
        this.capactiy = capactiy;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getCoordinates()
    {
        return new LatLng(this.latitude,this.longitude);
    }

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
