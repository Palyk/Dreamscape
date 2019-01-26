package sddec18_21.multi_effectpedalapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class PlayActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;

    // Create a BroadcastReceiver for ACTION_FOUND.
    //private BroadcastReceiver mReceiver;
    //private BluetoothDevice bDevice = null;
    private ListView discoveries;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> devices;
    private byte[] command;
    //private ConnectThread cthread;
    private Globals g = Globals.getInstance();
    BluetoothService btService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        btService = BluetoothService.getInstance();
        Intent intent = getIntent();
//        String message = intent.getStringExtra(HomeActivity.PLAY);

//        TextView testText = findViewById(R.id.tv_playTest);
//        testText.setText(message);

        TextView currentEffect = findViewById(R.id.tv_playTest);
        currentEffect.setText(g.getData());

        String[] demo =  {"Preset 1", "Preset 2", "Preset 3", "Preset 4",
                          "Preset 5", "Preset 6", "Preset 7", "Preset 8",
                          "Preset 9", "Preset 10"};

        final ListView set = findViewById(R.id.lv_set);
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, demo);
        set.setAdapter(adapter);
        set.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if(btService==null) {
                    Toast.makeText(PlayActivity.this,"Bluetooth not connected. Please connect from home activity",Toast.LENGTH_LONG).show();
                    return;
                }
                String preset = (String) set.getItemAtPosition(pos);
                command = preset.getBytes();
                // Send command to Pi
                sendCommand(command);
            }
        });

        // Block used for sending effect names chosen from "Configure Activity"
//        ListView set = findViewById(R.id.lv_set);
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, g.getEffects());
//        set.setAdapter(adapter);
//        set.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
//                String effect = (String) adapterView.getAdapter().getItem(pos);
//                command = effect.getBytes();
//                // Send command to Pi
//                sendCommand(command);
//            }
//        });

        // Sets up bluetooth adapter and finds paired devices. should only be the pi
        /*final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not supported on this device.",
                           Toast.LENGTH_SHORT).show();
        } else {

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    bDevice = device;
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.d("BLUETOOTH-PAIRED", deviceName);
                    TextView paired = findViewById(R.id.tv_paired);
                    paired.setText(deviceName);
                }
            }



            cthread = new ConnectThread(bDevice);
            cthread.run();
//            discoveriee.setAdapter(arrayAdapter);

        }*/
    }


    public void sendCommand(byte[] c) {
        if (btService.cthread != null)
            btService.cthread.write(c);
        else
            Log.d("RFCOMM_ERROR", "Bluetooth connection has not been made");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        //unregisterReceiver(mReceiver);
    }
}
