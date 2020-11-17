package com.example.xpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private Button sign_in_button;
    private FirebaseUserManager DBUserManager;
    private TextView email_input;
    private TextView password_input;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /* initialize UI components */
        UI_init();
        DB_init();

    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Initialize ui components.
     */
    private void UI_init()
    {
        setContentView(R.layout.activity_main);
        sign_in_button = findViewById(R.id.sign_in_button);
        email_input = findViewById(R.id.userName);
        password_input = findViewById(R.id.Password);

        /* oturum ac listener baslat */
        sign_in_button.setOnClickListener(v -> {
            DBUserManager.signInUser(email_input.getText().toString(),password_input.getText().toString());
        });
    }

    /**
     * Initialize DB managers & handle connection issues.
     */
    private void DB_init()
    {
        /* get database reference */
        ref = FirebaseDatabase.getInstance().getReference();

        /* initialize FireBaseUserManager */
        DBUserManager = new FirebaseUserManager(this);

        /*
         // Sadece users icin dinleme threadi olustur
        ref.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               Log.i("listener","listener Calisti..");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
         */
    }
}