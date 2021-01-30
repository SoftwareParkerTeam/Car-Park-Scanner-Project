package com.example.xpark.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.craftman.cardform.CardForm;
import com.example.xpark.R;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        CardForm card = (CardForm)findViewById(R.id.cardForm);
        TextView value = (TextView)findViewById(R.id.payment_amount);
        Button pay_button = (Button)findViewById(R.id.btn_pay);

        value.setText("0 ₺");

        pay_button.setOnClickListener(v ->{
            System.out.println("PAYMENT DONE");
        });
    }
}