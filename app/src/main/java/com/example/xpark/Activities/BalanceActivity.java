package com.example.xpark.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.example.xpark.Module.User;
import com.example.xpark.R;

public class BalanceActivity extends AppCompatActivity {

    private User currentUser;
    private Button nextButton;
    private TextView textAmount;
    private TextView textBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        init_logged_user();
        init_ui();
    }

    private void init_ui(){
        nextButton = findViewById(R.id.button_next);
        textAmount = findViewById(R.id.text_amount);
        textBalance = findViewById(R.id.text_balance);

        String msg = "Your remaining balance: " + currentUser.getCreditbalance() + "₺";
        textBalance.setText(msg);

        nextButton.setOnClickListener(v -> {
            double amount = Double.parseDouble(textAmount.getText().toString());

            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("CURRENT_USER",currentUser);
            intent.putExtra("AMOUNT",amount);
            startActivity(intent);
        });
    }

    private void init_logged_user() {
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("CURRENT_USER");
        System.out.println("USER GETTED : " + currentUser);
    }
}