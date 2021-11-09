package com.example.xpark.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xpark.R;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class EntranceActivity extends AppCompatActivity implements SensorEventListener {

    private SeekBar throttleSeekBar;
    private Button buttonConnect;
    private Button buttonFireTrigger;
    private TextView wheelAngleTextView;
    private TextView btBaglantiDurumuTextView;
    private Switch gearPositionSwitch;

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
    private int RC_komut_steeringAngle = 0;
    private int RC_komut_throttlePos = 0;
    private int RC_komut_gearPosition = 0;
    private int RC_komut_fireTrigger = 0;

    private Thread rcCommandSenderThread;
    private static final int RC_KOMUT_GONDERIM_BEKLEME_SURESI = 10; // 10 milisecond.

    private static final byte RC_COMMAND_PACKET_HEADER_1 = 0x33;
    private static final byte RC_COMMAND_PACKET_HEADER_2 = 0x44;

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

        this.startRcCommandSender();
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
                rotation_mapped *= -1;

                // Gonderilecek komut guncellenir.
                this.RC_komut_steeringAngle = (rotation_mapped + 90);

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

        /* FIRE BUTTON */
        this.buttonFireTrigger = (Button)findViewById(R.id.fire_button);
        /* FIRE BUTTON */

        /* wheel angle text view */
        this.wheelAngleTextView = (TextView)findViewById(R.id.wheelAngleText);
        /* wheel angle text view */

        /* Bluetooth Aygit Spinner */
        this.bluetoothDevicesSpinner = (Spinner) findViewById(R.id.bluuetooth_device_select_spinner);
        /* Bluetooth Aygit Spinner */

        /* Gear Position Switch */
        this.gearPositionSwitch = (Switch)findViewById(R.id.gear_switch);
        /* Gear Position Switch */

        this.baglanilanAracDeviceNameTextView = (TextView)findViewById(R.id.kullanici_skor_bilgi_textView);
        this.kullaniciSkorTextView = (TextView)findViewById(R.id.kullanici_skor_textView);

        this.rakipAracDeviceNameTextView = (TextView)findViewById(R.id.rakip_skor_bilgi_textView);
        this.rakipSkorTextView = (TextView)findViewById(R.id.rakip_skor_textView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init_listeners()
    {
        this.throttleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                System.out.println("Gaz komut bitti");
                seekBar.setProgress(0);
                seekBar.getThumb().mutate().setAlpha(OPACITY_NO_TOUCH);

                // Gonderilecek komut guncellenir.
                EntranceActivity.this.RC_komut_throttlePos = 0;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                System.out.println("Gaz komut basladi");
                seekBar.getThumb().mutate().setAlpha(OPACITY_TOUCH);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // RC komut guncellenir.
                if(progress>=0 && progress<= 255 && !EntranceActivity.this.isDeviceFlat)
                {
                    EntranceActivity.this.RC_komut_throttlePos = progress;
                }
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

        this.buttonFireTrigger.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                EntranceActivity.this.RC_komut_fireTrigger = 1;
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                EntranceActivity.this.RC_komut_fireTrigger = 0;
            }
            return true;
        });

        this.gearPositionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
            {
                EntranceActivity.this.RC_komut_gearPosition = 1;
            }
            else
            {
                EntranceActivity.this.RC_komut_gearPosition = 0;
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

    private long CRC32(int[] data, long n, long poly, long xor)
    {
        long g = 1L << n | poly;
        long crc = 0xFFFFFFFF;
        for(int b : data)
        {
            crc ^= (long) b << (n - 8);
            for (int i = 0; i < 8; i++)
            {
                crc <<=1 ;
                if(4294967296L == (crc & (1L << n)))
                {
                    crc ^= g;
                }
            }
        }

        return crc ^ xor;
    }

    private void startRcCommandSender()
    {
        this.rcCommandSenderThread = new Thread(() ->
        {
            while(true)
            {
                try
                {
                    if(btBaglantiDurumu && !this.isDeviceFlat)
                    {
                        sendRCcommandPacket();
                    }

                    Thread.sleep(RC_KOMUT_GONDERIM_BEKLEME_SURESI);

                }catch (Exception ex)
                {
                    System.out.println("RC komut gonderme hata : " + ex.getMessage());
                }
            }
        });
        this.rcCommandSenderThread.start();
    }

    private byte[] rcDataToByteArray()
    {
        byte[] byte_array = new byte[4];
        byte_array[0] = (byte)this.RC_komut_steeringAngle;
        byte_array[1] = (byte)this.RC_komut_throttlePos;
        byte_array[2] = (byte)this.RC_komut_gearPosition;
        byte_array[3] = (byte)this.RC_komut_fireTrigger;
        return byte_array;
    }

    private int[] rcDataToIntArray()
    {
        int[] byte_array = new int[4];
        byte_array[0] = this.RC_komut_steeringAngle;
        byte_array[1] = this.RC_komut_throttlePos;
        byte_array[2] = this.RC_komut_gearPosition;
        byte_array[3] = this.RC_komut_fireTrigger;
        return byte_array;
    }

    private void sendRCcommandPacket()
    {
        try
        {
            byte[] gonderilecek_paket       = new byte[10];
            byte[] RC_komut_paketi_bytes    = rcDataToByteArray();
            int[]  RC_komut_paketi_int      = rcDataToIntArray();

            long crc_32 = CRC32(RC_komut_paketi_int, 32, 0x04C11DB7, 0);

            /* Gonderilecek veri hazirlanir */
            gonderilecek_paket[0] = RC_COMMAND_PACKET_HEADER_1;
            gonderilecek_paket[1] = RC_COMMAND_PACKET_HEADER_2;
            gonderilecek_paket[2] = RC_komut_paketi_bytes[0];
            gonderilecek_paket[3] = RC_komut_paketi_bytes[1];
            gonderilecek_paket[4] = RC_komut_paketi_bytes[2];
            gonderilecek_paket[5] = RC_komut_paketi_bytes[3];

            // CRC hesabi eklenir.
            gonderilecek_paket[6] = (byte) ((crc_32 >> 24) & 0xFF);
            gonderilecek_paket[7] = (byte) ((crc_32 >> 16) & 0xFF);
            gonderilecek_paket[8] = (byte) ((crc_32 >> 8) & 0xFF);
            gonderilecek_paket[9] = (byte) ((crc_32) & 0xFF);
            /* Gonderilecek veri hazirlanir */

            // Veri gonderilir.
            btOutputStream.write(gonderilecek_paket,0,10);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }
}