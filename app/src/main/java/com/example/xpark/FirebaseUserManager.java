package com.example.xpark;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicBoolean;

public class FirebaseUserManager {

    private static final String DB_USER_FIELD = "USERS";

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseAuthException ex = null;
    private AppCompatActivity activity_ref;

    public FirebaseUserManager(AppCompatActivity activity)
    {
        this.activity_ref = activity;
    }

    /* todo : catch exception, return User
    * Integrate User with FirebaseUser
     */
    public FirebaseUser signInUser(String email, String password)
    {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Log.i("USER LOGIN",((FirebaseAuthException)task.getException()).toString());
                    /* todo : print error to activity using activity_ref */
                }
                else {
                    Log.i("USER LOGIN", "LOGIN SUCCEED");

                    /* create new intent, todo send user object into new intent */
                }
            }
        });

        return auth.getCurrentUser();
    }

    /* todo :  change pw section, change parameter to Firebase user after extending from it..
     */
    public void createNewUser(Member user)
    {
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPass()).addOnCompleteListener((OnCompleteListener<AuthResult>) task -> {
            if(task.isSuccessful()) {
                FirebaseDatabase.getInstance().getReference().child(DB_USER_FIELD).push().setValue(user);
                Log.i("USER CREATE", "USER CREATE SUCCEED");
            }
            else
            {
                Log.i("USER CREATE",((FirebaseAuthException)task.getException()).toString());
                ex = (FirebaseAuthException)task.getException();
                Toast.makeText(activity_ref.getApplicationContext(),ex.toString(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    /* todo : catch exception
     */
    public void deleteUser(FirebaseUser user)
    {

    }

    public void updateUserProperties(FirebaseUser user, FirebaseUser newuser)
    {

    }
}
