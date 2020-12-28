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
        //ParkingInformationActivity.res.setText(result.getText());
        ParkingInformationActivity.park_id = (result.getText());
       /* System.out.println("park qr id");
        System.out.println(ParkingInformationActivity.park_id);
        System.out.println("qr result");
        System.out.println(result.getText());*/
        onBackPressed();
    }

    @Override
    protected void onPause(){
        super.onPause();
        System.out.println(ParkingInformationActivity.park_id);
        Scanner.stopCamera();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Scanner.setResultHandler(this);
        Scanner.startCamera();
    }
}