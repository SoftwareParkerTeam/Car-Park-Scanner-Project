package com.example.xpark.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.xpark.DataBaseProvider.FirebaseDBConstants;
import com.example.xpark.Module.CarPark;
import com.example.xpark.Module.User;
import com.example.xpark.R;
import com.example.xpark.Utils.ToastMessageConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import es.dmoral.toasty.Toasty;

public class ParkingInformationActivity extends AppCompatActivity {

    private Button finishPark_button;
    private TextView textTime;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_information);
        System.out.println("PARKING INF ACTIVITY");
        init_logged_user();

        UI_init();
    }

    private void UI_init() {
        finishPark_button = findViewById(R.id.button_finish);
        textTime = findViewById(R.id.text_time);

        textTime.setText(currentUser.getParkingTime());

        /* parkı bitir butonu */
        finishPark_button.setOnClickListener(v -> {
            finishPark();
        });
    }

    private void init_logged_user() {
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("CURRENT_USER");
        System.out.println("USER GETTED : " + currentUser);
    }

    private void finishPark() {
        // if not parked yet, return
        if(currentUser.getCarparkid().equals(User.NOT_PARKED))
            return;

        LocalDateTime finishtime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime parkingtime = LocalDateTime.parse(currentUser.getParkingTime(), formatter);

        calculateTime(finishtime, parkingtime);

        // Todo : handle balance and etc..
        this.removeUserFromCarpark();

    }

    private void calculateTime(LocalDateTime d1, LocalDateTime d2){
        long diff = ChronoUnit.MINUTES.between(d2, d1);

        System.out.println("CALCULATED TIME(minute): " + diff);
    }

    private void removeUserFromCarpark()
    {

        /* parse user */
        String carParkGnrlId = currentUser.getCarparkid();

        /* get district */
        String[] tokens = carParkGnrlId.split("-");
        String db_district_field = tokens[0];
        String db_carpark_id = tokens[1];
        System.out.println("DB DISTRICT = " + db_district_field);
        System.out.println("DB ID = " + db_carpark_id);

        /* find database reference from user */
        DatabaseReference pref = FirebaseDatabase.getInstance().getReference().child(FirebaseDBConstants.DB_CARPARK_FIELD).child(db_district_field).child(db_carpark_id);
        pref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                /* get car park object from database */
                HashMap map = (HashMap)currentData.getValue();
                if(map == null)
                    return Transaction.success(currentData);

                /* get park object from data base */
                CarPark park = new CarPark((HashMap) currentData.getValue());

                /* increment free are in car park */
                park.decrementUsed();

                /* update the database */
                currentData.setValue(park);

                /* find user field in DB */
                DatabaseReference uref = FirebaseDatabase.getInstance().getReference().child(FirebaseDBConstants.DB_USER_FIELD).child(currentUser.getUid());
                currentUser.removeCarparkid();
                currentUser.removeParkingTime();

                /* update the user in DB */
                uref.setValue(currentUser);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                System.out.println("Commit check : " + committed + " " + currentData.getValue());
                if(committed){
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("CURRENT_USER",currentUser);
                    startActivity(intent);
                    finish();
                }
                //this.runOnUiThread(() -> Toasty.warning(this.getApplicationContext(), ToastMessageConstants.TOAST_MSG_INFO_MAP_UPDATED, Toast.LENGTH_SHORT).show());
            }
        });
    }
}