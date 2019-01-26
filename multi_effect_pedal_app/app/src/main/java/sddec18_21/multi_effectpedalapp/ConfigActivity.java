package sddec18_21.multi_effectpedalapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConfigActivity extends AppCompatActivity {

    private ListView effectsList;
    Globals g = Globals.getInstance();
    BluetoothService btService;
    ArrayAdapter<String> adapter;
    ArrayList<String> fileDisplay;
    File[] fileList;
    LinearLayout selMenu;
    LinearLayout inputName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        btService = BluetoothService.getInstance();
        Intent intent = getIntent();

        String message = intent.getStringExtra(HomeActivity.CONFIG);

        TextView testText = findViewById(R.id.tv_configTest);
        testText.setText(message);
        selMenu = findViewById(R.id.SelectMenu);
        fileList = getFilesDir().listFiles();
        fileDisplay = new ArrayList<String>();
        inputName = findViewById(R.id.CreateFile);
        for (File aFileList : fileList) {
            fileDisplay.add(aFileList.toString());
        }
        final String[] effects = {"Bitcrusher", "Booster", "Clean", "Delay",
                "Distortion", "Echo", "Fuzz", "Looper",
                "Octaver", "Reverb", "Tremolo"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileDisplay);
        effectsList = findViewById(R.id.lv_effects);
        effectsList.setAdapter(adapter);
        //effectsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        effectsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        effectsList.setSelector(getDrawable(R.drawable.state_selector));
        effectsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String fileSel = (String) effectsList.getItemAtPosition(pos);
                Log.e("Notice:","list item selected");
                //Note: The below two lines are for testing the new activity's look
                effectsList.setSelection(pos);

                effectsList.setItemChecked(pos,true);
                selMenu.setVisibility(View.VISIBLE);

                inputName.setVisibility(View.GONE);
                //Intent newIntent = new Intent(getApplicationContext(), EditEffect.class);
                //startActivity(newIntent);



            }
        });

    }

    public void onNewFileClick(View view) throws IOException {

        inputName.setVisibility(View.VISIBLE);
        selMenu.setVisibility(View.GONE);
    }


    public void onCreateClick(View view) throws IOException {
        EditText fileName = findViewById(R.id.fileName);
        LinearLayout inputName = findViewById(R.id.CreateFile);
        FileOutputStream outputStream = openFileOutput(fileName.getText().toString(),Context.MODE_PRIVATE);
        inputName.setVisibility(View.GONE);
        String preset = "447448\n" +
                "NAME "+ fileName.getText().toString() +"\n" +
                "START \n" +
                "\n" +
                "CLEAN 1 0\n" +
                "NOSOUND\n" +
                "NOSOUND\n" +
                "\n" +
                "STEP\n" +
                "\n" +
                "CLEAN 0 0\n" +
                "NOSOUND\n" +
                "NOSOUND\n" +
                "\n" +
                "STEP\n" +
                "\n" +
                "CLEAN 0 0\n" +
                "NOSOUND\n" +
                "NOSOUND\n" +
                "\n" +
                "END";
        outputStream.write(preset.getBytes());
        //fileDisplay.add(outputStream.getChannel().toString());
        //adapter.notifyDataSetChanged();
        outputStream.close();
        fileDisplay.clear();
        fileList = getFilesDir().listFiles();
        for (File aFileList : fileList) {
            fileDisplay.add(aFileList.toString());
        }
        adapter.notifyDataSetChanged();

    }
    public void onBackClicked(View v) {
        selMenu.setVisibility(View.GONE);
        effectsList.setItemChecked(effectsList.getSelectedItemPosition(),false);
        effectsList.setSelection(View.NO_ID);
        effectsList.setBackgroundColor(Color.TRANSPARENT); //Todo find out how to reset the background since this isn't working
    }
    public void onEditClicked(View v) {
        String fileName = effectsList.getAdapter().getItem(effectsList.getCheckedItemPosition()).toString();
        Intent edit = new Intent(this, EditEffect.class);
        edit.putExtra("FILENAME", fileName);
        startActivity(edit);
    }
    public void onUploadClicked(View v) throws IOException {
        if(btService==null) {
            Toast.makeText(this,"Bluetooth not connected. Please try again.",Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ConfigActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        mBuilder.setTitle("Select a preset #");
        final Spinner mSpinner = (Spinner) mView.findViewById(R.id.PresetPicker);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(ConfigActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.presets_array));
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!mSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose a Preset...")) {
                    String fileName = effectsList.getAdapter().getItem(effectsList.getCheckedItemPosition()).toString();
                    File file = new File(fileName);
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    byte[] data = new byte[(int) file.length()];
                    try {
                        fis.read(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String str = null;
                    try {
                        str = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String str2 = "d" + mSpinner.getSelectedItem().toString() + str; //replace 1 with proper preset # to replace later
                    sendCommand(str2.getBytes());
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ConfigActivity.this, "Upload: " + mSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

        /*String fileName = effectsList.getAdapter().getItem(effectsList.getCheckedItemPosition()).toString();
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String str = new String(data, "UTF-8");
        String str2 = "d" + 1 + str; //replace 1 with proper preset # to replace later
        sendCommand(str2.getBytes());
        fis.close();*/
    }

    public void onDownloadClicked(View v) throws IOException {
        if(btService==null) {
            Toast.makeText(this,"Bluetooth not connected. Please try again.",Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ConfigActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        mBuilder.setTitle("Select a preset #");
        final Spinner mSpinner = (Spinner) mView.findViewById(R.id.PresetPicker);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(ConfigActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.presets_array));
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!mSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose a Preset...")) {
                    String command = "u" + mSpinner.getSelectedItem().toString(); //replace the 1 with the proper preset #
                    sendCommand(command.getBytes());
                    byte[] input = btService.cthread.read();
                    String parseInput = null;
                    try {
                        parseInput = new String(input, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Scanner readName = new Scanner(parseInput);
                    readName.nextLine();
                    String title = readName.nextLine().substring(5);
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = openFileOutput(title,Context.MODE_PRIVATE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        outputStream.write(input);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    readName.close();
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ConfigActivity.this, "Download: " + mSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    fileDisplay.clear();
                    fileList = getFilesDir().listFiles();
                    for (File aFileList : fileList) {
                        fileDisplay.add(aFileList.toString());
                    }
                    adapter.notifyDataSetChanged();
                    selMenu.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            }
        });
        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

        /*String command = "u" + 1; //replace the 1 with the proper preset #
        sendCommand(command.getBytes());
        byte[] input = btService.cthread.read();
        String parseInput = new String(input, "UTF-8");

        Scanner readName = new Scanner(parseInput);
        readName.nextLine();
        String title = readName.nextLine().substring(5);
        FileOutputStream outputStream = openFileOutput(title,Context.MODE_PRIVATE);
        outputStream.write(input);
        readName.close();
        outputStream.close();*/


    }
    public void onDeleteClicked(View v) {
        String fileName = effectsList.getAdapter().getItem(effectsList.getCheckedItemPosition()).toString();
        //deleteFile(fileName);
        File file = new File(fileName);
        file.delete();
        fileDisplay.clear();
        fileList = getFilesDir().listFiles();
        for (File aFileList : fileList) {
            fileDisplay.add(aFileList.toString());
        }
        adapter.notifyDataSetChanged();
        selMenu.setVisibility(View.GONE);
    }


    //helper commands
    public void sendCommand(byte[] c) {
        if (btService.cthread != null)
            btService.cthread.write(c);
        else
            Log.d("RFCOMM_ERROR", "Bluetooth connection has not been made");
    }
}
