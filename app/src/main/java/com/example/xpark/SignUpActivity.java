package com.example.xpark;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth fb;
    private DatabaseReference ref;
    private FirebaseUserManager DBUserManager;
    private Button signUp_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUp_button = findViewById(R.id.button_signup);

        ref = FirebaseDatabase.getInstance().getReference();
        DBUserManager = new FirebaseUserManager(this);

        signUp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User test_user = new User(
                        ((TextView) findViewById(R.id.name)).getText().toString(),
                        ((TextView) findViewById(R.id.surname)).getText().toString(),
                        ((TextView) findViewById(R.id.username)).getText().toString(),
                        ((TextView) findViewById(R.id.password)).getText().toString(),
                        ((TextView) findViewById(R.id.phone)).getText().toString(),
                        ((TextView) findViewById(R.id.Email)).getText().toString(),
                        50
                );
                DBUserManager.createNewUser(test_user);
                finish();
            }
        });
    }
}