package com.example.xpark.Activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xpark.DataBaseProvider.FirebaseUserManager;
import com.example.xpark.R;
import com.example.xpark.Utils.ToastMessageConstants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private Button sign_in_button;
    private Button sign_up_button;
    private FirebaseUserManager DBUserManager;
    private TextView email_input;
    private TextView password_input;
    private Button forgotPw;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        super.onCreate(savedInstanceState);

        // kritik
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }

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

    private void resetPW_INIT() {

        forgotPw = findViewById(R.id.forgotPw);
        fAuth = FirebaseAuth.getInstance();

        forgotPw.setOnClickListener( v -> {
            final EditText resetMail = new EditText(v.getContext());
            final AlertDialog.Builder resetDialog = new AlertDialog.Builder(v.getContext());

            resetDialog.setTitle("Şifre Yenile");
            resetDialog.setMessage("Yenileme linki için yenileme linki gönder");
            resetDialog.setView(resetMail);

            resetDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String mail = resetMail.getText().toString();

                    fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toasty.success(LoginActivity.this, ToastMessageConstants.TOAST_MSG_VALID_MAIL, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(LoginActivity.this, ToastMessageConstants.TOAST_MSG_ERROR_INVALID_MAIL, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            resetDialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            resetDialog.create().show();
        });
    }

    /**
     * Initialize ui components.
     */
    private void UI_init() {
        setContentView(R.layout.activity_login);

        sign_in_button = findViewById(R.id.sign_in_button);
        sign_up_button = findViewById(R.id.sign_up_button);
        email_input = findViewById(R.id.userName);
        password_input = findViewById(R.id.Password);

        resetPW_INIT();

        /* oturum ac listener baslat */
        sign_in_button.setOnClickListener(v -> {
            DBUserManager.signInUser(email_input.getText().toString(), password_input.getText().toString());
        });

        /* yeni uyelik ac listener baslat */
        sign_up_button.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            this.startActivity(intent);
        });
    }

    /**
     * Initialize DB managers & handle connection issues.
     */
    private void DB_init() {
        /* get database reference */
        ref = FirebaseDatabase.getInstance().getReference();

        /* initialize FireBaseUserManager */
        DBUserManager = new FirebaseUserManager(this);

        // deneme, mainactivityde sonra kullanilacak..
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
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        /* ignore it */
    }
}
