package com.example.xpark.Utils;

import android.location.Address;
import android.location.Geocoder;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoLocation
{
    public static void getAdress(String locationAdress, final Context context, Handler handler) {
        Thread th = new Thread() {
            @Override
            public void run() {
                Geocoder gcdr = new Geocoder(context, Locale.getDefault());
                String result = null;

                try {
                    List<Address> addressList = gcdr.getFromLocationName(locationAdress, 1);

                    if(addressList != null && addressList.size() > 0) {
                        Address address = (Address) addressList.get(0);

                        StringBuilder sb = new StringBuilder();
                        sb.append(address.getLatitude());
                        sb.append(" ");
                        sb.append(address.getLongitude());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Message msg = Message.obtain();
                    msg.setTarget(handler);

                    if (result != null) {
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        msg.setData(bundle);
                    }
                    msg.sendToTarget();
                }
            }
        };
        th.start();
    }
}
