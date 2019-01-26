package sddec18_21.multi_effectpedalapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Tyler on 4/2/2018.
 */

public class Globals {
    private static Globals instance;
    private String currentEffect;
    private ArrayList<String> effects = new ArrayList<>();

    private Globals(){}



    public void setData(String effect){
        this.currentEffect = effect;
    }

    public void addEffect (String e) {
        effects.add(e);
    }

    public ArrayList<String> getEffects() {
        return effects;
    }

    public String getData(){
        return this.currentEffect;
    }

    public static synchronized Globals getInstance(){
        if (instance == null) {
            instance = new Globals();
        }
        return instance;
    }
}


