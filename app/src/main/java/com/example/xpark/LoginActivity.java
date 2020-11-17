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

    private FirebaseAuth fb;
    private DatabaseReference ref;
    private Button sign_in_button;
    private FirebaseUserManager DBUserManager;
    private int test = 3000;
    private TextView email_input;
    private TextView password_input;

    /* todo : Managerlari init eden method yazilacak */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sign_in_button = findViewById(R.id.sign_in_button);

        email_input = findViewById(R.id.userName);
        password_input = findViewById(R.id.Password);

        ref = FirebaseDatabase.getInstance().getReference();

        /* initialize FireBaseUserManager */
        DBUserManager = new FirebaseUserManager(this);


        /************** test cases ************/
        //User test_user = new User("hknABCasd@gmail.com","123hakoasdo");
        //test_user.setPhone("05009008754");


        /* oturum aç */
        sign_in_button.setOnClickListener(v -> {

            User test_user = new User(
                    email_input.getText().toString(),
                    password_input.getText().toString());

            DBUserManager.createNewUser(test_user);
        });

        /* sign in test
        but2.setOnClickListener(v -> {
            DBUserManager.signInUser(test_user.getEmail(),test_user.getPassword());
        });*/

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
        /************** test cases ************/
    }
}