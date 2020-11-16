package com.example.xpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fb;
    private DatabaseReference ref;
    private Button but;
    FirebaseUserManager DBUserManager;

    /* login olduktan sonra kullanici bu ref de tutulacak.., getCurrentUser() ile *
    * veya user tipinde
     */
    FirebaseUser me;

    /* todo : Managerlari init eden method yazilacak */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        but = findViewById(R.id.button);
        ref = FirebaseDatabase.getInstance().getReference();

        /* initialize FireBaseUserManager */
        DBUserManager = new FirebaseUserManager(this);

        /* yeni uyelik yarat */
        but.setOnClickListener(v -> {
            DBUserManager.createNewUser(new Member("hkn@gm","123hakoo"));
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
    }
}