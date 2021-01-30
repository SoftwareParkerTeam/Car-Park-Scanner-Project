package com.example.xpark.Activities;

import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import com.example.xpark.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import static org.junit.Assert.*;

public class PaymentActivityTest {
    @Rule
    public ActivityTestRule<PaymentActivity> paymentActivityActivityTestRule = new ActivityTestRule<PaymentActivity>(PaymentActivity.class);
    private PaymentActivity myPaymentActivity = null;

    @Before
    public void setUp() throws Exception {
        myPaymentActivity = paymentActivityActivityTestRule.getActivity();
    }

    @Rule
    public void testID(){
        TextView textValue = myPaymentActivity.findViewById(R.id.payment_amount);
        assertNotNull(textValue);
    }

    @After
    public void tearDown() throws Exception {
        myPaymentActivity = null;
    }
}