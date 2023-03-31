package com.nitap.attende.pages;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ttv.facerecog.databinding.ActivityAttendanceBinding;

public class AttendanceActivity extends AppCompatActivity {

    ActivityAttendanceBinding binding;
    final String bt_name = "CkyM8zssrZQTUzW6cFjFzzroaw8y/ZeB4hxEtssR33k=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.attendanceBtn.setOnClickListener(v -> {
            Boolean flag = setBluetooth(true);

        });

    }

    public boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final long lTimeToGiveUp_ms = System.currentTimeMillis() + 10000;


        if (ContextCompat.checkSelfPermission(AttendanceActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(AttendanceActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
            }

        }

        if (ContextCompat.checkSelfPermission(AttendanceActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }


            if (bluetoothAdapter.isEnabled()) {
                String sOldName = bluetoothAdapter.getName();

                if (sOldName.equalsIgnoreCase(bt_name) == false) {
                    final Handler myTimerHandler = new Handler();
                    myTimerHandler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (bluetoothAdapter.isEnabled()) {

                                        if (ActivityCompat.checkSelfPermission(AttendanceActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                            Toast.makeText(AttendanceActivity.this, "bluetooth is enabled and setting name", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        bluetoothAdapter.setName(bt_name);
                                        if (bt_name.equalsIgnoreCase(bluetoothAdapter.getName()))
                                            Toast.makeText(AttendanceActivity.this, bluetoothAdapter.getName() +"changed", Toast.LENGTH_SHORT).show();

                                    }
                                    if ((bt_name.equalsIgnoreCase(bluetoothAdapter.getName()) == false) && (System.currentTimeMillis() < lTimeToGiveUp_ms))
                                    {
                                        myTimerHandler.postDelayed(this, 500);
                                        if (bluetoothAdapter.isEnabled())
                                            Toast.makeText(AttendanceActivity.this, "Update BT Name: waiting on BT Enable", Toast.LENGTH_SHORT).show();

                                        else
                                            Toast.makeText(AttendanceActivity.this, "Waiting", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } , 500);
                }


            }


        }





        return true;
    }
}