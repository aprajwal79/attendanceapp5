package com.nitap.attende.pages;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nitap.attende.EncryptActivity;
import com.ttv.facerecog.R;
import com.ttv.facerecog.databinding.ActivityAttendanceBinding;
import com.ttv.facerecog.databinding.ActivityTakeAttendanceBinding;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TakeAttendance extends AppCompatActivity {

    ActivityTakeAttendanceBinding binding ;

    Set<String> myset;
    static List<String> presentDevice = new ArrayList<>();
    static ArrayList<String> mylist2 = new ArrayList<String>();
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    String sec_id;

    BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    public static TextView statusTextView;
    public static String s1 = "START";

    void t(String msg) {

        if (myset.add(msg)) {
            s1 = s1 + "    " + msg;
            if (statusTextView != null) {
                statusTextView.setText(s1);
                //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "STATUS_TEXT_VIEW IS NULL", Toast.LENGTH_SHORT).show();
            }

        }


    }



    void display(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private final BroadcastReceiver bluetoothDiscoveryReceiver = new BroadcastReceiver() {
        @SuppressLint({"NotifyDataSetChanged", "MissingPermission"})
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //super.onCreate(savedInstanceState);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                t(device.getName());

                if (!bluetoothDevices.contains(device)) {
                    String decryptedName = EncryptActivity.decrypt(device.getName());

                    bluetoothDevices.add(device);
                    presentDevice.add(decryptedName);
                    // devicesAdapter.notifyDataSetChanged();
                }


            }
        }
    };

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint({"NotifyDataSetChanged", "MissingPermission"})
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //super.onCreate(savedInstanceState);
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                Toast.makeText(context, "finished scanning", Toast.LENGTH_SHORT).show();
                postResult();
            }
        }
    };




    // @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTakeAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button scanButton = findViewById(R.id.take_attendance_btn);

        scanButton.setOnClickListener(v -> {
            scanBluetooth();


        });

    }

    void scanBluetooth(){
        try {
            myset = null;
            myset = new LinkedHashSet<String>();
            myset.clear();


            statusTextView = findViewById(R.id.status_text_view1);

            boolean a1 = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                a1 = checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
            }
            boolean a2 = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                a2 = checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
            }
            if(!a1) { display("Manifest.permission.BLUETOOTH_CONNECT permission required");  }
            if(!a2) { display("Manifest.permission.BLUETOOTH_SCAN permission required");  }
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                statusTextView.setText("bluetooth_not_supported");
            } else {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
                }

                RecyclerView devicesRecyclerView = findViewById(R.id.devices_recycler_view);


//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                        REQUEST_LOCATION_PERMISSION);

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(bluetoothDiscoveryReceiver, filter);

                IntentFilter filter_rec = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(receiver, filter_rec);


                ActivityCompat.requestPermissions(this,new String[] {
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission_group.LOCATION,
                        Manifest.permission_group.NEARBY_DEVICES,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH_ADVERTISE,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_COARSE_LOCATION



                },0);

                display("BLUETOOTH DISCOVERY STATUS :" + bluetoothAdapter.isDiscovering() );

                try {
                    //bluetoothAdapter.cancelDiscovery();
                } catch (Throwable e) {
                    display("DISCOVERY PRE STARTED");
                }

                boolean isCorrect = bluetoothAdapter.startDiscovery();
                if (isCorrect) {
                    display("DISCOVERY STARTED SUCCESSFULLY");
                } else {
                    display("DISCOVERY FAILED TO START");
                }

            }

        } catch (Throwable e) {
            e.printStackTrace();
            display(e.toString());
            if (statusTextView != null) {    statusTextView.setText(e.toString());  }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothDiscoveryReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                statusTextView.setText("location_permission_required");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                statusTextView.setText("bluetooth_enabled");
            } else {
                statusTextView.setText("bluetooth_disabled");
            }
        }
    }

    private String checkSection(){

        String degree = binding.degreeSel.getSelectedItem().toString();
        String course = binding.courseSel.getSelectedItem().toString();
        String year = binding.yearSel.getSelectedItem().toString();
        String section = binding.sectionSel.getSelectedItem().toString();

        String sec_info = degree + course + year + section;

        return sec_info;


    }



    private void postResult(){

        final DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("course");
        final DatabaseReference secRef = FirebaseDatabase.getInstance().getReference().child("section");
        final DatabaseReference seRef = FirebaseDatabase.getInstance().getReference().child("sections");

        final String saveCurrentDate, saveCurrentMonth, saveCurrentTime;
        String section_str = checkSection();
        String subject = binding.subjectSel.getSelectedItem().toString();


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM,dd yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentMonth = new SimpleDateFormat("MMMM");
        saveCurrentMonth = currentMonth.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hmma");
        saveCurrentTime = currentTime.format(calendar.getTime());

        String sessionId = saveCurrentDate+saveCurrentTime;



        secRef.child(section_str).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    sec_id = snapshot.child("section_id").getValue().toString();

                    Intent intent = new Intent(TakeAttendance.this, DisplayAttendance.class);
                    intent.putStringArrayListExtra("rollList", (ArrayList<String>) presentDevice);
                    intent.putExtra("sectionId", section_str);
                    intent.putExtra("sectionCode", sec_id);
                    intent.putExtra("subject", subject);
                    startActivity(intent);

//
                }else{
                    Toast.makeText(TakeAttendance.this, "section not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}