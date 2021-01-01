package com.example.xpark.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.example.xpark.R;

public class EntranceDebugActivity extends AppCompatActivity {

    private Button signin;
    private Button signup;
    private Button map;
    private Button qr;
    private Button ent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance_debug);

        signin = findViewById(R.id.sign_in_enter);
        signup = findViewById(R.id.sign_up_enter);
        map = findViewById(R.id.map_enter);
        qr = findViewById(R.id.qrCode_enter);
        ent = findViewById(R.id.entrance);

        ent.setOnClickListener( view -> {
            this.startActivity(new Intent(this, EntranceActivity.class));
        });

        signin.setOnClickListener( view -> {
            Intent intent = new Intent(this, EntranceActivity.class);
            this.startActivity(intent);
            this.finish();
        });

        signup.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            this.startActivity(intent);
        });

        map.setOnClickListener(view ->  {
            Intent intent = new Intent(this, MapsActivity.class);
            this.startActivity(intent);
        });
    }
}