package com.example.xpark.Activities;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xpark.DataBaseProvider.FirebaseUserManager;
import com.example.xpark.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SignUpActivityTest {
    @Rule
    public ActivityTestRule<SignUpActivity> signUpActivityTestRule = new ActivityTestRule<SignUpActivity>(SignUpActivity.class);

    private SignUpActivity signUpActivity = null;
    private FirebaseUserManager DBUserManager;

    @Before
    public void setUp() throws Exception {
        signUpActivity = signUpActivityTestRule.getActivity();
    }

    @Test
    public void testID(){
        ImageView imageView = signUpActivity.findViewById(R.id.imageView3);
        assertNotNull(imageView);
        Button sign_up_button = signUpActivity.findViewById(R.id.button_signup);
        assertNotNull(sign_up_button);
        TextView phone_input = signUpActivity.findViewById(R.id.phone);
        assertNotNull(phone_input);
        TextView email_input = signUpActivity.findViewById(R.id.Email);
        assertNotNull(email_input);
        TextView password_input = signUpActivity.findViewById(R.id.password);
        assertNotNull(password_input);
    }



    @After
    public void tearDown() throws Exception {
        signUpActivity = null;
    }
}
