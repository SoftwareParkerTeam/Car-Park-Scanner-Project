package com.example.xpark.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.xpark.R;

import static android.content.Context.SENSOR_SERVICE;

public class EntranceActivity extends AppCompatActivity implements SensorEventListener {

    private SeekBar throttleSeekBar;
    private Button buttonConnect;
    private TextView wheelAngleTextView;

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    private static final int OPACITY_NO_TOUCH = 100;
    private static final int OPACITY_TOUCH = 255;

    // Direksiyon aci bilgileri
    float[] mGravity;
    float[] mGeomagnetic;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_entrance);

        /* --------------------------------------------------------------------------------------- */
        this.init_gui();
        this.init_listeners();

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }


    public void onSensorChanged(SensorEvent event)
    {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if (mGravity != null && mGeomagnetic != null)
        {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                double rotation = Math.atan2(R[6],R[7]);
                if(rotation > 3.12)
                    rotation = 3.12;
                if(rotation < -3.12)
                    rotation = -3.12;

                float roll = orientation[2]; // orientation contains: azimut, roll and roll
                int roll_mapped = (int)((((roll)*180/3.12)));

                /*
                To map
                [A, B] --> [a, b]
                [0, 3.12] --> [-90,90]
                use this formula
                (val - A)*(b-a)/(B-A) + a
                */
                int rotation_mapped = (int)((((rotation)*180/3.12) - 90));
                if(rotation_mapped < - 180)
                    rotation_mapped = 90;
                if(rotation_mapped < - 90 && rotation_mapped > -180)
                    rotation_mapped = -90;

                this.wheelAngleTextView.setText(rotation_mapped + "");
                int abs_roll = (int)Math.abs(roll_mapped);
                if(abs_roll < 10 || abs_roll > 170 || roll_mapped > 0)
                {
                    // device is flat.
                    // Yaw angle is not trustable.
                    this.wheelAngleTextView.setTextColor(Color.RED);
                }
                else
                {
                    // device is non-flat.
                    // Yaw angle is trustable.
                    this.wheelAngleTextView.setTextColor(Color.WHITE);
                }
            }
        }
    }

    private void init_gui()
    {
        /* GAS SEEK BAR */
        this.throttleSeekBar = (SeekBar)findViewById(R.id.seekBarThrottle);
        this.throttleSeekBar.setMax(255);
        this.throttleSeekBar.getThumb().mutate().setAlpha(OPACITY_NO_TOUCH);
        /* GAS SEEK BAR */

        /* CONNECT BUTTON */
        this.buttonConnect = (Button)findViewById(R.id.connect_button);
        /* CONNECT BUTTON */

        /* wheel angle text view */
        this.wheelAngleTextView = (TextView)findViewById(R.id.wheelAngleText);
        /* wheel angle text view */
    }

    private void init_listeners()
    {
        this.throttleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                System.out.println("Gaz komut bitti");
                seekBar.setProgress(0);
                seekBar.getThumb().mutate().setAlpha(OPACITY_NO_TOUCH);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                System.out.println("Gaz komut basladi");
                seekBar.getThumb().mutate().setAlpha(OPACITY_TOUCH);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                // Yeni deger araca gonderilecek.
            }
        });
    }
}