package com.example.xpark.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.xpark.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView Scanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Scanner = new ZXingScannerView(this);
        setContentView(Scanner);
    }

    @Override
    public void handleResult(Result result){
        QRActivity.res.setText(result.getText());
        QRActivity.park_id = (result.getText());
        onBackPressed();
    }

    @Override
    protected void onPause(){
        super.onPause();
        Scanner.stopCamera();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Scanner.setResultHandler(this);
        Scanner.startCamera();
    }
}