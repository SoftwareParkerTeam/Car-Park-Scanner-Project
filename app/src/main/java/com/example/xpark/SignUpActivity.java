package com.example.xpark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private FirebaseUserManager DBUserManager;
    private Button signUp_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //kritik
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }

        DB_init();
        UI_init();

    }
    @Override
    public void onBackPressed()
    {
        /* ignore it */
    }

    private void DB_init()
    {
        ref = FirebaseDatabase.getInstance().getReference();
        DBUserManager = new FirebaseUserManager(this);
    }

    private void UI_init()
    {
        setContentView(R.layout.activity_sign_up);
        signUp_button = findViewById(R.id.button_signup);

        signUp_button.setOnClickListener(v -> {

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
        });
        TextView name_field = (TextView) findViewById(R.id.name);
        name_field.setOnClickListener(v -> name_field.setText(""));
        TextView surname_field = (TextView) findViewById(R.id.surname);
        surname_field.setOnClickListener(v -> surname_field.setText(""));
        TextView username_field = (TextView) findViewById(R.id.username);
        username_field.setOnClickListener(v -> username_field.setText(""));
        TextView password_field = (TextView) findViewById(R.id.password);
        password_field.setOnClickListener(v -> password_field.setText(""));
        TextView phone_field = (TextView) findViewById(R.id.phone);
        phone_field.setOnClickListener(v -> phone_field.setText(""));
        TextView email_field = (TextView) findViewById(R.id.Email);
        email_field.setOnClickListener(v -> email_field.setText(""));
    }
}