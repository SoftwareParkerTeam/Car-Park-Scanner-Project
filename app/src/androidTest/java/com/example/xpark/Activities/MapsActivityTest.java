package com.example.xpark.Activities;

import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.rule.ActivityTestRule;

import com.example.xpark.DataBaseProvider.FirebaseCarparkManager;
import com.example.xpark.DataBaseProvider.FirebaseUserManager;
import com.example.xpark.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class MapsActivityTest
{
    @Rule
    public ActivityTestRule<MapsActivity> loginActivityActivityTestRule = new ActivityTestRule<>(MapsActivity.class);

    private MapsActivity myMapsActivity = null;
    private FirebaseUserManager DBUserManager;

    @Before
    public void setUp() throws Exception {
        myMapsActivity = loginActivityActivityTestRule.getActivity();
    }

    @Test
    public void UI_INIT_TEST() {
        FloatingActionButton search_button = myMapsActivity.findViewById(R.id.button_search);
        assertNotNull(search_button);
        Button res_button = myMapsActivity.findViewById(R.id.button_res);
        assertNotNull(res_button);
        Button inf_button = myMapsActivity.findViewById(R.id.button_inf);
        assertNotNull(inf_button);
        Toolbar toolbar = (Toolbar) myMapsActivity.findViewById(R.id.toolbar);
        assertNotNull(toolbar);
        DrawerLayout mDrawer = (DrawerLayout) myMapsActivity.findViewById(R.id.drawer_layout);
        assertNotNull(mDrawer);
        NavigationView nvDrawer = (NavigationView) myMapsActivity.findViewById(R.id.nav_view);
        assertNotNull(nvDrawer);
    }


    @Test
    public void testOnConfigurationChanged() {
    }

    @Test
    public void testOnOptionsItemSelected() {
    }

    @Test
    public void testOnMapReady() {
    }

    @After
    public void tearDown() throws Exception {
        myMapsActivity = null;
    }
}
