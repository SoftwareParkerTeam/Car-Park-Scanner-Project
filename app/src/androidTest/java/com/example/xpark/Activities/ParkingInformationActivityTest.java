package com.example.xpark.Activities;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import com.craftman.cardform.CardForm;
import com.example.xpark.Module.CarPark;
import com.example.xpark.Module.User;
import com.example.xpark.R;
import com.google.android.material.navigation.NavigationView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class ParkingInformationActivityTest {
    @Rule
    public ActivityTestRule<ParkingInformationActivity> parkingInformationActivityActivityTestRule = new ActivityTestRule<ParkingInformationActivity>(ParkingInformationActivity.class);
    private ParkingInformationActivity myParkingInformationActivity = null;

    @Before
    public void setUp() throws Exception {
        myParkingInformationActivity = parkingInformationActivityActivityTestRule.getActivity();
    }

    @Test
    public void testID_toolbar(){
        Toolbar toolbar = myParkingInformationActivity.findViewById(R.id.toolbar2);
        assertNotNull(toolbar);
    }

    @Test
    public void testID_drawerL(){
        DrawerLayout drawerLayout = myParkingInformationActivity.findViewById(R.id.drawer_layout_2);
        assertNotNull(drawerLayout);
    }

    @Test
    public void testID_drawerN(){
        NavigationView drawerN = myParkingInformationActivity.findViewById(R.id.nav_view2);
        assertNotNull(drawerN);
    }

    @Test
    public void testID_finishPArk_button(){
        Button finishPArk = myParkingInformationActivity.findViewById(R.id.button_finish);
        assertNotNull(finishPArk);
    }

    @Test
    public void testID_textTime(){
        TextView texttime = myParkingInformationActivity.findViewById(R.id.text_time);
        assertNotNull(texttime);
    }

    @Test
    public void testID_qrScan_button(){
        Button qrScanButton = myParkingInformationActivity.findViewById(R.id.QrScanner);
        assertNotNull(qrScanButton);
    }

    @Test
    public void espressoTest(){
        try{
            Espresso.onView(withId(R.id.text_time)).perform(typeText("text_time"));
            onView(withId(R.id.QrScanner)).perform(scrollTo()).perform(click());
            onView(withId(R.id.button_finish)).perform(scrollTo()).perform(click());
            onView(withId(R.id.QrScanner)).check(matches(isDisplayed()));
            onView(withId(R.id.button_finish)).check(matches(isDisplayed()));
            onView(withId(R.id.nav_view2)).check(matches(isDisplayed()));
            onView(withId(R.id.drawer_layout_2)).check(matches(isDisplayed()));
            onView(withId(R.id.toolbar2)).check(matches(isDisplayed()));
        }
        catch (Exception e){
            Log.i("error",e.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception {
        myParkingInformationActivity = null;
    }
}