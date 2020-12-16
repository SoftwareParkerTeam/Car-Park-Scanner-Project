package com.example.xpark.Activities;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.xpark.DataBaseProvider.FirebaseUserManager;
import com.example.xpark.R;
import com.example.xpark.Utils.EncodeAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private Button sign_in_button;
    private Button sign_up_button;
    private FirebaseUserManager DBUserManager;
    private TextView email_input;
    private TextView password_input;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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

    /**
     * Initialize ui components.
     */
    private void UI_init()
    {
        setContentView(R.layout.activity_login);
        sign_in_button = findViewById(R.id.sign_in_button);
        sign_up_button = findViewById(R.id.sign_up_button);
        email_input = findViewById(R.id.userName);
        password_input = findViewById(R.id.Password);

        /* oturum ac listener baslat */
        sign_in_button.setOnClickListener(v -> {
            DBUserManager.signInUser(email_input.getText().toString(), password_input.getText().toString());
        });

        /* yeni uyelik ac listener baslat */
        sign_up_button.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            this.startActivity(intent);
            this.finish();
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
    protected void onResume()
    {
        super.onResume();
    }
    @Override
    public void onBackPressed()
    {
        /* ignore it */
    }
}
