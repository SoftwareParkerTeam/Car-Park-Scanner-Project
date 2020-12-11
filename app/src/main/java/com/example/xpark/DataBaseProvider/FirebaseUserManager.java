package com.example.xpark.DataBaseProvider;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.xpark.Activities.LoginActivity;
import com.example.xpark.Activities.MapsActivity;
import com.example.xpark.Module.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Author : Goktug Akin.
 */

public class FirebaseUserManager {

    private static final String DB_USER_FIELD = FirebaseDBConstants.DB_USER_FIELD;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final AppCompatActivity activity_ref;

    private volatile static FirebaseUserManager instance;

    private FirebaseUserManager(AppCompatActivity activity)
    {
        this.activity_ref = activity;
    }

    /**
     * Singleton getter method.
     * @return new created or current instance of manager class.
     */
    public static FirebaseUserManager getInstance(AppCompatActivity activity)
    {
        // Double check for multi-threading later.
        if(instance == null)
        {
            synchronized (FirebaseUserManager.class){
                if (instance == null)
                    instance = new FirebaseUserManager(activity);
            }
        }
        return instance;
    }

    /**
     * Login olmayi dener. Basarili olursa intent yaratarak yeni bir activitye gecer.
     * @param email Kullanici maili.
     * @param password Kullanici sifresi.
     * @note Login ekraninda, (LoginActivity) de cagirilmali.
     */
    public void signInUser(String email, String password)
    {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.i("USER LOGIN", "LOGIN SUCCEED");
                Toast.makeText(activity_ref.getApplicationContext(),"Uyelik basariyla yaratildi.",Toast.LENGTH_SHORT);
                startNextActivityAfterLogin(auth.getCurrentUser());
            }
            else {
                Log.i("USER LOGIN ",task.getException().toString());
                Toast.makeText(activity_ref.getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Yeni uyelik acar. Acilan yeni uyeligi DB de auth kismina ve Real Time DB kismina ekler.
     * AUTH ve real time DB kismi senkronize edilmistir.
     * @param user DB ye yeni eklenecek user referansi.
     * @note Bu metod calistiktan sonra user referansi uid degerini elde etmis olur.
     */
    public void createNewUser(User user)
    {
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener((OnCompleteListener<AuthResult>) task -> {
            if(task.isSuccessful()) {
                FirebaseUser created_user = auth.getCurrentUser();
                user.setUid(created_user.getUid());
                FirebaseDatabase.getInstance().getReference().child(DB_USER_FIELD).child(created_user.getUid()).setValue(user);
                Log.i("USER CREATE", "USER CREATE SUCCEED");
                Intent intent = new Intent(activity_ref, LoginActivity.class);
                activity_ref.startActivity(intent);
                activity_ref.finish();
            }
            else
            {
                Log.i("USER CREATE",(task.getException()).toString());
                Toast.makeText(activity_ref.getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
            }
        });

    }
    /**
     * Kullanici verilerini gunceller.
     * @param user kullanici referansi.
     * @param newuser override edilecek yeni kullanici.
     * @note Bu method calistiktan sonra, newuser referansi uid degeri kazanacaktir.
     */
    public void updateUserProperties(User user, User newuser)
    {
        /* find user by uid */
        newuser.setUid(user.getUid());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(DB_USER_FIELD).child(user.getUid());
        ref.setValue(newuser);
    }
    private void startNextActivityAfterLogin(FirebaseUser fbuser)
    {
        /* get User by FirebaseUser with uid */
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(DB_USER_FIELD).child(fbuser.getUid());

        /* get logged in user for ONCE */
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User getted_user = snapshot.getValue(User.class);
                System.out.println(">>>>>>> USER READED FROM DB ::: " + getted_user);

                /* create new intent and start new activity */
                Intent intent = new Intent(activity_ref, MapsActivity.class);
                intent.putExtra("CURRENT_USER",getted_user);
                activity_ref.startActivity(intent);
                activity_ref.finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
