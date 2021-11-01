package com.example.xpark.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xpark.R;

import static android.content.Context.SENSOR_SERVICE;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class EntranceActivity extends AppCompatActivity implements SensorEventListener {

    private SeekBar throttleSeekBar;
    private Button buttonConnect;
    private TextView wheelAngleTextView;
    private TextView btBaglantiDurumuTextView;

    private TextView rakipAracDeviceNameTextView; // rakip arac bluetooth cihaz ismi.
    private TextView baglanilanAracDeviceNameTextView; // baglanilan aracin bluetooth cihaz ismi.
    private TextView rakipSkorTextView; // rakip skor bilgisi.
    private TextView kullaniciSkorTextView; // kullanici (ben) skor bilgisi.

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    private static final int OPACITY_NO_TOUCH = 100;
    private static final int OPACITY_TOUCH = 255;

    // Direksiyon aci bilgileri
    float[] mGravity;
    float[] mGeomagnetic;

    // Bluetooth islemleri.
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private Spinner bluetoothDevicesSpinner;
    private static final UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket btSocket;
    private OutputStream btOutputStream;
    private InputStream btInputStream;
    private boolean btBaglantiDurumu = false;

    // Gonderilecek RC komut paketi.
    private byte RC_komut_steeringAngle;
    private byte RC_komut_gearPosition;
    private byte RC_komut_fireTrigger;
    private byte RC_komut_throttlePos;

    private boolean isDeviceFlat = false;

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

        /* --------------------------------------------------------------------------------------- */
        // Bluetooth islemleri.
        this.init_bluetooth();
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
                [0, 3.12] --> [-90,90] [0,180]
                use this formula
                (val - A)*(b-a)/(B-A) + a
                */
                int rotation_mapped = (int)((((rotation)*180/3.12) - 90));
                if(rotation_mapped < - 180)
                    rotation_mapped = 90;
                if(rotation_mapped < - 90 && rotation_mapped > -180)
                    rotation_mapped = -90;

                this.RC_komut_steeringAngle = (byte)(rotation_mapped + 90);

                this.wheelAngleTextView.setText(rotation_mapped + "");
                int abs_roll = (int)Math.abs(roll_mapped);
                if(abs_roll < 10 || abs_roll > 170 || roll_mapped > 0)
                {
                    // device is flat.
                    // Yaw angle is not trustable.
                    this.wheelAngleTextView.setTextColor(Color.RED);
                    this.isDeviceFlat = true;
                }
                else
                {
                    // device is non-flat.
                    // Yaw angle is trustable.
                    this.wheelAngleTextView.setTextColor(Color.WHITE);
                    this.isDeviceFlat = false;
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
        this.btBaglantiDurumuTextView = (TextView)findViewById(R.id.baglantiDurumuTextView);
        this.btBaglantiDurumuTextView.setTextColor(Color.RED);
        /* CONNECT BUTTON */

        /* wheel angle text view */
        this.wheelAngleTextView = (TextView)findViewById(R.id.wheelAngleText);
        /* wheel angle text view */

        /* Bluetooth Aygit Spinner */
        this.bluetoothDevicesSpinner = (Spinner) findViewById(R.id.bluuetooth_device_select_spinner);
        /* Bluetooth Aygit Spinner */

        this.baglanilanAracDeviceNameTextView = (TextView)findViewById(R.id.kullanici_skor_bilgi_textView);
        this.kullaniciSkorTextView = (TextView)findViewById(R.id.kullanici_skor_textView);

        this.rakipAracDeviceNameTextView = (TextView)findViewById(R.id.rakip_skor_bilgi_textView);
        this.rakipSkorTextView = (TextView)findViewById(R.id.rakip_skor_textView);
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

        this.buttonConnect.setOnClickListener(v -> {
            try
            {
                if(!btBaglantiDurumu)
                {
                    BA.cancelDiscovery();

                    /* Secilen bluetooth aygiti elde edilir */
                    String secilenAygitName = EntranceActivity.this.bluetoothDevicesSpinner.getSelectedItem().toString();
                    BluetoothDevice secilenDevice = null;
                    for(BluetoothDevice device : EntranceActivity.this.pairedDevices)
                    {
                        if(device.getName().equals(secilenAygitName))
                        {
                            secilenDevice = device;
                            break;
                        }
                    }

                    if(null != secilenDevice)
                    {
                        connectTargetDevice(secilenDevice);
                    }
                }
                else
                {
                    disconnectTargetDevice();
                }

            }
            catch (Exception ex)
            {
                Toast.makeText(EntranceActivity.this, "Hata : " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init_bluetooth()
    {
        this.BA = BluetoothAdapter.getDefaultAdapter();
        if(null == BA)
        {
            Toast.makeText(this, "Bluetooth Desteklenmiyor !", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<String> pairedDevicesNameList = new ArrayList<>();
        this.pairedDevices = BA.getBondedDevices();
        for(BluetoothDevice device : this.pairedDevices)
        {
            pairedDevicesNameList.add(device.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, pairedDevicesNameList);
        this.bluetoothDevicesSpinner.setAdapter(adapter);
    }

    private void connectTargetDevice(BluetoothDevice secilenDevice)
    {
        try
        {
            EntranceActivity.this.btSocket = secilenDevice.createRfcommSocketToServiceRecord(BT_UUID);
            EntranceActivity.this.btSocket.connect();

            // Input output stream elde edilir.
            EntranceActivity.this.btOutputStream = EntranceActivity.this.btSocket.getOutputStream();
            EntranceActivity.this.btInputStream = EntranceActivity.this.btSocket.getInputStream();

            this.btBaglantiDurumuTextView.setTextColor(Color.GREEN);
            this.btBaglantiDurumuTextView.setText(R.string.baglanti_durumu_bagli_text);
            this.buttonConnect.setText(R.string.disconnect_button_text);
            this.bluetoothDevicesSpinner.setEnabled(false);
            this.baglanilanAracDeviceNameTextView.setText(secilenDevice.getName()); // Baglanilan cihaz ismi arayuzde gosterilir.
            this.btBaglantiDurumu = true;
        }
        catch (Exception ex)
        {
            Toast.makeText(EntranceActivity.this, "Hata : " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void disconnectTargetDevice()
    {
        try
        {
            btSocket.close();
            btInputStream.close();
            btOutputStream.close();

            this.btBaglantiDurumuTextView.setTextColor(Color.RED);
            this.btBaglantiDurumuTextView.setText(R.string.baglanti_durumu_bagli_degil_text);
            this.buttonConnect.setText(R.string.connect_button_text);
            this.bluetoothDevicesSpinner.setEnabled(true);
            this.baglanilanAracDeviceNameTextView.setText("-");
            this.btBaglantiDurumu = false;
        }
        catch (Exception ex)
        {
            Toast.makeText(EntranceActivity.this, "Hata : " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}