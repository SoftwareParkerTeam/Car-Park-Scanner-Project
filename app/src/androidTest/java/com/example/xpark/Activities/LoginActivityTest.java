
/**
 * Author : Dilara Karakas.
 */


package com.example.xpark.Activities;

import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;
import com.example.xpark.DataBaseProvider.FirebaseUserManager;
import com.example.xpark.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginActivityTest {
    @Rule
    public ActivityTestRule<LoginActivity> loginActivityActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    private LoginActivity myLoginActivity = null;
    private FirebaseUserManager DBUserManager;

    @Before
    public void setUp() throws Exception {
        myLoginActivity = loginActivityActivityTestRule.getActivity();
    }

    @Test
    public void testID_imageView(){
        ImageView imageView = myLoginActivity.findViewById(R.id.imageView);
        assertNotNull(imageView);
    }

    @Test
    public void testID_sign_in(){
        Button sign_in_button = myLoginActivity.findViewById(R.id.sign_in_button);
        assertNotNull(sign_in_button);
    }
    @Test
    public void testID_sign_up(){
        Button sign_up_button = myLoginActivity.findViewById(R.id.sign_up_button);
        assertNotNull(sign_up_button);
    }
    @Test
    public void testID_email(){
        TextView email_input = myLoginActivity.findViewById(R.id.userName);
        assertNotNull(email_input);
    }
    @Test
    public void testID_password(){
        TextView password_input = myLoginActivity.findViewById(R.id.Password);
        assertNotNull(password_input);
    }

    @After
    public void tearDown() throws Exception {
        myLoginActivity = null;
    }
}