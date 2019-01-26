package sddec18_21.multi_effectpedalapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothService {

    private static BluetoothService ourInstance = null;
    private BroadcastReceiver mReceiver;
    private BluetoothDevice bDevice = null;
    private ListView discoveries;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> devices;
    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public ConnectThread cthread;
    BluetoothService() {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    bDevice = device;
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.d("BLUETOOTH-PAIRED", deviceName);

                }
            }


            if(bDevice != null) {
                cthread = new ConnectThread(bDevice);
                cthread.run();
            }


    }



    static synchronized BluetoothService getInstance() {
        if(ourInstance == null) {
            ourInstance = new BluetoothService();
            if(ourInstance.cthread==null) ourInstance = null;
            if(ourInstance.cthread.connected==0) ourInstance = null;
        }
        return ourInstance;
    }

}
