package com.example.xpark.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.xpark.R;

public class QRActivity extends AppCompatActivity {

    private Button scanButton;
    public static TextView res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        scanButton = findViewById(R.id.scan_test);
        res = findViewById(R.id.res_test);

        scanButton.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
        });
    }
}