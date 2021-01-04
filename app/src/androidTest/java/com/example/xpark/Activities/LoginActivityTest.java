package com.example.xpark.Activities;

import android.widget.ImageView;

import androidx.test.rule.ActivityTestRule;

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

    @Before
    public void setUp() throws Exception {
        myLoginActivity = loginActivityActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){
        ImageView imageView = myLoginActivity.findViewById(R.id.imageView);
        System.out.println(myLoginActivity.findViewById(R.id.imageView));
        assertNotNull(imageView);
    }

    @After
    public void tearDown() throws Exception {
        myLoginActivity = null;
    }
}