package sddec18_21.multi_effectpedalapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    public static final String PLAY = "sddec18_21.multi_effectpedalapp.PLAY";
    public static final String CONFIG = "sddec18_21.multi_effectpedalapp.CONCIG";
    private final static int REQUEST_ENABLE_BT = 1;
    private Globals g = Globals.getInstance();
    private BluetoothService btService;
    private TextView btStatus;
    private Button retry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btService = BluetoothService.getInstance();
        btStatus = findViewById(R.id.btStatus);
        retry = findViewById(R.id.retry);
        if(btService == null) {
            btStatus.setText("Bluetooth not connected/paired!");
            retry.setVisibility(View.VISIBLE);
        }
        else {
            btStatus.setText("Bluetooth Connected");
            retry.setVisibility(View.GONE);
        }

    }

    // When "Play" button is tapped, opens the play screen
    public void openPlayActivity(View view) {
        if(btService==null) {
            Toast.makeText(this,"Option unavailable while bluetooth disconnected", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, PlayActivity.class);
        String message = "Opened Play Screen";
        intent.putExtra(PLAY, message);
        startActivity(intent);
    }

    public void openConfigActivity(View view) {
        Intent intent = new Intent(this, ConfigActivity.class);
        String message = "Opened Configure Screen";
        intent.putExtra(CONFIG, message);
        startActivity(intent);
    }

    public void openDragDrop(View view) {
        Intent intent = new Intent(this, DragDrop.class);
        String message = "Opened DragDrop Screen";
        intent.putExtra("DragDrop", message);
        startActivity(intent);
    }

    public void onRetryClick(View v) {
        btService = BluetoothService.getInstance();
        if(btService == null) {
            Toast.makeText(this, "Connection failed!", Toast.LENGTH_LONG).show();
        }
        else {
            retry.setVisibility(View.GONE);
            btStatus.setText("Bluetooth Connected");
        }
    }
}
