package com.nitap.attende;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ttv.facerecog.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BluetoothScannerActivity extends AppCompatActivity {

    public static String key = "aq5s4z069w0WE4NPxidwqw==";
    public static String name = "UNKNOWN" ;
    Set<String> myset ;
   static List<String> mlist ;
   static ArrayList<String> mylist2 = new ArrayList<String>();
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

     BluetoothAdapter bluetoothAdapter;
    private final List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
     //DevicesAdapter devicesAdapter;
    public static TextView statusTextView;
    public static String s1 = "START";

    void t(String msg) {
        /*
        if (mylist2.contains(msg)) {
            return;
        }
        //String s = statusTextView.getText().toString();
        s1 = s1 + "    " + msg;
        if (statusTextView != null) {
            statusTextView.setText(s1);
        } else {
            Toast.makeText(this, "STATUS_TEXT_VIEW IS NULL", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, s1, Toast.LENGTH_LONG).show();

         */
        if(myset.add(msg)) {
            s1 = s1 + "    " + msg;
            if (statusTextView != null) {
                statusTextView.setText(s1);
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "STATUS_TEXT_VIEW IS NULL", Toast.LENGTH_SHORT).show();
            }

        }



    }

    static void display(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

     void display( String msg) {
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
                //if (!mylist2.contains(device.getAddress())) { mylist2.add(device.getAddress() ); }
                if (!bluetoothDevices.contains(device)) {
                 //   bluetoothDevices.add(device);
                    // devicesAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    // @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            myset = null;
            myset = new LinkedHashSet<String>();
            myset.clear();
            /*
            mlist = new LinkedList<>();
            mlist.add("mNULL");*/
            //display("STARTED");
            setContentView(R.layout.activity_bluetooth);

            statusTextView = findViewById(R.id.status_text_view);

            boolean a1 = checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
            boolean a2 = checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
            if(!a1) { display("Manifest.permission.BLUETOOTH_CONNECT permission required");  }
            if(!a2) { display("Manifest.permission.BLUETOOTH_SCAN permission required");  }
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
           /* BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
             bluetoothAdapter = bluetoothManager.getAdapter();*/
            if (bluetoothAdapter == null) {
                statusTextView.setText("bluetooth_not_supported");
            } else {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
                }

                // devicesAdapter = new DevicesAdapter(this, bluetoothDevices);
                RecyclerView devicesRecyclerView = findViewById(R.id.devices_recycler_view);
                // devicesRecyclerView.setAdapter(devicesAdapter);

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(bluetoothDiscoveryReceiver, filter);


                    ActivityCompat.requestPermissions(this,new String[] {
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission_group.LOCATION,
                            Manifest.permission_group.NEARBY_DEVICES,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.BLUETOOTH_CONNECT


                    },0);
                    /*
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(this,new String[] {
                              Manifest.permission.NEARBY_WIFI_DEVICES
                        },0);
                    }
                    */

                    // TODO_ Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

               // display("ABOUT TO START BT SCAN");
                display("BLUETOOTH DISCOVERY STATUS :" + bluetoothAdapter.isDiscovering() );

                try {
                    //bluetoothAdapter.cancelDiscovery();
                } catch (Throwable e) {
                    display("DISCOVERY PRE STARTED");
                }

                boolean isCorrect = bluetoothAdapter.startDiscovery();
                if (isCorrect) {
                    display("DISCOVERY STARTED SUCCESSFULLY");
                    name = bluetoothAdapter.getName();
                    boolean success = bluetoothAdapter.setName(key);
                    display("Set name to " + key + " : " + success );

                } else {
                    display("DISCOVERY FAILED TO START");
                }


    }

} catch (Throwable e) {
    e.printStackTrace();
   // Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
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
}
