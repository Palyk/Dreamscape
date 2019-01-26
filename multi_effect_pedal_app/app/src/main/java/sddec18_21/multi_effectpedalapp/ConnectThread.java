package sddec18_21.multi_effectpedalapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Tyler on 4/19/2018.
 */

public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothAdapter mBluetoothAdapter = null;
    private final String py_uuid = "00001101-0000-1000-8000-00805F9D34FB";
    private final UUID uuid = UUID.fromString(py_uuid);

    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private String test = "Test message from app";
    private byte[] mmBuffer; // = test.getBytes(); // bit-stream storage
    private android.os.Handler mHandler; // handles bluetooth services
    private static final int DATA_TRANSFER_FAIL = -1;
    public int connected = 0;

    public ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mmDevice = device;
        InputStream tempIn = null;
        OutputStream tempOut = null;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e("SOCKET_ERROR", "Socket's create() method failed", e);
        }
        try {
            // get input stream
            tempIn = tmp.getInputStream();
        } catch (IOException e) {
            Log.e("SOCKET_ERROR", "Failed to get input stream", e);
        }
        try {
            // get output stream
            tempOut = tmp.getOutputStream();
        } catch (IOException e) {
            Log.e("SOCKET_ERROR", "Failed to get output stream", e);
        }
        mmSocket = tmp;
        mmInStream = tempIn;
        mmOutStream = tempOut;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        mBluetoothAdapter.cancelDiscovery();
        //mmBuffer = new byte[1024];

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
                Log.e("CONNECT_ERROR", "Could not connect");
            } catch (IOException closeException) {
                Log.e("SOCKET_ERROR", "Could not close the client socket", closeException);
            }
            return;
        }
        connected = 1;

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        manageMyConnectedSocket(mmSocket);
        //write(mmBuffer);
    }

    public void write(byte[] bytes) {
        try {
            Log.d("DATA", bytes.toString());
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e("DATA_ERROR", "Data wasn't sent.");
        }
    }

    public byte[] read() {
        try {
            byte[] inStream = new byte[1024];
            mmInStream.read(inStream);
            Log.d("DATA", inStream.toString());
            return inStream;
        } catch (IOException e) {
            Log.e("DATA_ERROR", "Data wasn't received.");
            return null;
        }

    }

    private void manageMyConnectedSocket(BluetoothSocket mmSocket) {

    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("SOCKET_ERROR", "Could not close the client socket", e);
        }
    }


}