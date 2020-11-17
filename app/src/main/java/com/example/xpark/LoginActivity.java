package com.example.xpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth fb;
    private DatabaseReference ref;
    private Button but;
    private Button but2;
    FirebaseUserManager DBUserManager;

    /* todo : Managerlari init eden method yazilacak */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        but = findViewById(R.id.button);
        but2 = findViewById(R.id.button2);

        ref = FirebaseDatabase.getInstance().getReference();

        /* initialize FireBaseUserManager */
        DBUserManager = new FirebaseUserManager(this);


        /************** test cases ************/
        User test_user = new User("hknABC@gmail.com","123hakoo");
        test_user.setPhone("05009008754");

        /* yeni uyelik yarat */
        but.setOnClickListener(v -> {
            DBUserManager.createNewUser(test_user);
        });

        /* sign in test */
        but2.setOnClickListener(v -> {
            DBUserManager.signInUser(test_user.getEmail(),test_user.getPassword());
        });

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