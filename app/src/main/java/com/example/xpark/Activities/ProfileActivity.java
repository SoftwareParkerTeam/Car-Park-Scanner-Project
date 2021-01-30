package com.example.xpark.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xpark.DataBaseProvider.FirebaseUserManager;
import com.example.xpark.Module.User;
import com.example.xpark.R;
import com.example.xpark.Utils.ToastMessageConstants;

import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity {

    private User currentUser;
    private TextView textEmail;
    private TextView textPhone;
    private TextView textEditPhone;
    private Button bPhone;
    private Button bUpdate;
    private FirebaseUserManager fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fbUser = new FirebaseUserManager(this);

        init_logged_user();
        init_uit();
    }

    private void init_uit(){
        textEmail = findViewById(R.id.text_email);
        textPhone = findViewById(R.id.text_phone);
        textEditPhone = findViewById(R.id.text_edit_phone);
        bPhone = findViewById(R.id.button_phone);
        bUpdate = findViewById(R.id.button_update);

        bUpdate.setVisibility(View.INVISIBLE);
        textEditPhone.setVisibility(View.INVISIBLE);

        textEmail.setText("Email: " + currentUser.getEmail());
        textPhone.setText("Telefon: " + currentUser.getPhone());

        bPhone.setOnClickListener(v ->{
            textPhone.setVisibility(View.INVISIBLE);
            textEditPhone.setVisibility(View.VISIBLE);
            bUpdate.setVisibility(View.VISIBLE);
        });

        bUpdate.setOnClickListener(v ->{
            String newPhone = textEditPhone.getText().toString();
            if(TextUtils.isDigitsOnly(newPhone)){
                fbUser.updatePhone(currentUser,newPhone);
                this.recreate();
            }
            else{
                Toasty.error(this.getApplicationContext(), ToastMessageConstants.TOAST_MSG_ERROR_INVALID_PHONE, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void init_logged_user() {
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("CURRENT_USER");
        System.out.println("USER GETTED : " + currentUser);
    }
}