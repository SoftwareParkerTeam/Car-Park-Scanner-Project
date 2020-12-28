package com.example.xpark.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xpark.DataBaseProvider.FirebaseUserManager;
import com.example.xpark.Module.User;
import com.example.xpark.R;

import java.io.Serializable;

public class QRActivity extends AppCompatActivity implements Serializable {
    private static final String NOT_PARKED = "NOT_PARKED";
    private FirebaseUserManager FBUserManager;
    /* logged in user */
    private User currentUser;
    public static Boolean qrBoolean;
    private Button scanButton;
    public static TextView res;
    public static String park_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        scanButton = findViewById(R.id.scan_test);
        res = findViewById(R.id.res_test);
        scanButton.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
        });
        init_logged_user();
        qrBoolean = controlID();
        onBackPressed();
    }
    private boolean controlID(){
        if(currentUser.getCarparkid().equals(park_id)){
            return true;
        }
        else {
            return false;
        }
    }

    private void init_logged_user() {
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("CURRENT_USER");
        System.out.println("USER GETTED : " + currentUser);
    }
}