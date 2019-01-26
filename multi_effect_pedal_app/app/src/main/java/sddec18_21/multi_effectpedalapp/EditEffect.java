package sddec18_21.multi_effectpedalapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EditEffect extends AppCompatActivity {
    //will be called when an effect from ConfigActivity is selected
    //Will pass over the file selected for testing
    //When done, will go back to the ConfigActivity
    //Creating a new file will also call this activity as well
    //The file will be created and instantiated as a "clean" effect


    //this is the activity, so make sure active parts are in every view
    String fileName;
    presetModel model;
    File file;
    int curi = -1;
    int curj = -1;
    int hasASelect = 0;
    LinearLayout editEffect;
    Button[][] buttonArray;

    Spinner effectSel;
    CheckBox useOrigin;
    CheckBox useOne;
    CheckBox doAWipe;
    CheckBox BTF;
    EditText weight;
    EditText val1;
    EditText val2;
    EditText val3;
    TextView values;
    TextView weightTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_effect);
        fileName = getIntent().getStringExtra("FILENAME");

        file = new File(fileName);
        try {
            model = new presetModel(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //readFile();

        editEffect=findViewById(R.id.editEffect);
        effectSel = findViewById(R.id.effectSel);
        useOrigin = findViewById(R.id.checkBox);
        useOne = findViewById(R.id.checkBox3);
        doAWipe = findViewById(R.id.checkBox2);
        BTF = findViewById(R.id.checkBox4);
        weight = findViewById(R.id.effectWeight);
        val1 = findViewById(R.id.effectNum);
        val2 = findViewById(R.id.effectNum2);
        val3 = findViewById(R.id.effectNum3);
        values = findViewById(R.id.textView5);
        weightTitle = findViewById(R.id.textView4);
        buttonArray = new Button[][] { {findViewById(R.id.button11), findViewById(R.id.button12), findViewById(R.id.button13)},
                                        {findViewById(R.id.button21),findViewById(R.id.button22), findViewById(R.id.button23)},
                                        {findViewById(R.id.button31), findViewById(R.id.button32), findViewById(R.id.button33)}};
        for(int i=0; i<3; ++i) {
            for(int j=0; j<3; ++j) {
                buttonArray[i][j].setText(effectSel.getItemAtPosition(model.aOut[i][j]).toString());
                if(model.aOut[i][j] == 12) { //if nosound, just automatically hide the effect
                    buttonArray[i][j].setVisibility(View.GONE);
                }
            }
        }

        effectSel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelEffect();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public void onSaveClicked(View v) throws IOException {
        //Convert the current values into a proper file format and save

        if(editEffect.getVisibility()==View.VISIBLE) { //files were still being edited
            onDoneClicked(findViewById(R.id.done)); //call the function to save the current edits to the model
        }
        for(int i=0; i<3; ++i) {
            for(int j=0; j<3; ++j) {
                if(buttonArray[i][j].getVisibility()==View.GONE) {
                    model.voidEffect(i,j); //void out nulled effects
                }
            }
        }
        model.writePreset(file);
        Intent toConfigure = new Intent(this, ConfigActivity.class);
        startActivity(toConfigure);

    }

    public void on11Clicked(View v) {
        onDoneClicked(null); //won't do anything in most cases, but if moving from one to another make sure to save changes
        unselectEffect();
        hasASelect = 1;
        curi = 0;
        curj = 0;
        buttonArray[curi][curj].setBackgroundResource(R.drawable.border1);
        showEditBar();
    }
    public void on12Clicked(View v) {
        onDoneClicked(null);
        unselectEffect();
        hasASelect = 1;
        curi = 0;
        curj = 1;
        buttonArray[curi][curj].setBackgroundResource(R.drawable.border1);
        showEditBar();
    }
    public void on13Clicked(View v) {
        onDoneClicked(null);
        unselectEffect();
        hasASelect = 1;
        curi = 0;
        curj = 2;
        buttonArray[curi][curj].setBackgroundResource(R.drawable.border1);
        showEditBar();
    }
    public void on21Clicked(View v) {
        onDoneClicked(null);
        unselectEffect();
        hasASelect = 1;
        curi = 1;
        curj = 0;
        buttonArray[curi][curj].setBackgroundResource(R.drawable.border2);
        showEditBar();
    }
    public void on22Clicked(View v) {
        onDoneClicked(null);
        unselectEffect();
        hasASelect = 1;
        curi = 1;
        curj = 1;
        buttonArray[curi][curj].setBackgroundResource(R.drawable.border2);
        showEditBar();
    }
    public void on23Clicked(View v) {
        onDoneClicked(null);
        unselectEffect();
        hasASelect = 1;
        curi = 1;
        curj = 2;
        buttonArray[curi][curj].setBackgroundResource(R.drawable.border2);
        showEditBar();
    }
    public void on31Clicked(View v) {
        onDoneClicked(null);
        unselectEffect();
        hasASelect = 1;
        curi = 2;
        curj = 0;
        buttonArray[curi][curj].setBackgroundResource(R.drawable.border3);
        showEditBar();
    }
    public void on32Clicked(View v) {
        onDoneClicked(null);
        unselectEffect();
        hasASelect = 1;
        curi = 2;
        curj = 1;
        buttonArray[curi][curj].setBackgroundResource(R.drawable.border3);
        showEditBar();
    }
    public void on33Clicked(View v) {
        onDoneClicked(null);
        unselectEffect();
        hasASelect = 1;
        curi = 2;
        curj = 2;
        buttonArray[curi][curj].setBackgroundResource(R.drawable.border3);
        showEditBar();
    }

    void unselectEffect() {
        for(int i=0; i<3; ++i) {
            buttonArray[0][i].setBackgroundResource(R.drawable.gradient1);
            buttonArray[1][i].setBackgroundResource(R.drawable.gradient2);
            buttonArray[2][i].setBackgroundResource(R.drawable.gradient3);
        }
        hasASelect = 0; //if 1, change to 0 now that the selection is unselecting
    }


    public void onadd1Clicked(View v) {
        if(buttonArray[0][0].getVisibility() == View.GONE) {
            buttonArray[0][0].setVisibility(View.VISIBLE);
        }
        else if(buttonArray[0][1].getVisibility() == View.GONE) {
            buttonArray[0][1].setVisibility(View.VISIBLE);
        }
        else if(buttonArray[0][2].getVisibility()== View.GONE) {
            buttonArray[0][2].setVisibility(View.VISIBLE);
        }
    }
    public void onremove1Clicked(View v) {
        if(buttonArray[0][2].getVisibility() == View.VISIBLE) {
            buttonArray[0][2].setVisibility(View.GONE);
        }
        else if(buttonArray[0][1].getVisibility() == View.VISIBLE) {
            buttonArray[0][1].setVisibility(View.GONE);
        }
        else if(buttonArray[0][0].getVisibility() == View.VISIBLE) {
            buttonArray[0][0].setVisibility(View.GONE);
        }
    }
    public void onadd2Clicked(View v) {
        if(buttonArray[1][0].getVisibility() == View.GONE) {
            buttonArray[1][0].setVisibility(View.VISIBLE);
        }
        else if(buttonArray[1][1].getVisibility() == View.GONE) {
             buttonArray[1][1].setVisibility(View.VISIBLE);
        }
        else if(buttonArray[1][2].getVisibility()== View.GONE) {
            buttonArray[1][2].setVisibility(View.VISIBLE);
        }
    }
    public void onremove2Clicked(View v) {
        if(buttonArray[1][2].getVisibility() == View.VISIBLE) {
            buttonArray[1][2].setVisibility(View.GONE);
        }
        else if(buttonArray[1][1].getVisibility() == View.VISIBLE) {
            buttonArray[1][1].setVisibility(View.GONE);
        }
        else if(buttonArray[1][0].getVisibility() == View.VISIBLE) {
            buttonArray[1][0].setVisibility(View.GONE);
        }
    }
    public void onadd3Clicked(View v) {
        if(buttonArray[2][0].getVisibility() == View.GONE) {
            buttonArray[2][0].setVisibility(View.VISIBLE);
        }
        else if(buttonArray[2][1].getVisibility() == View.GONE) {
            buttonArray[2][1].setVisibility(View.VISIBLE);
        }
        else if(buttonArray[2][2].getVisibility()== View.GONE) {
            buttonArray[2][2].setVisibility(View.VISIBLE);
        }
    }
    public void onremove3Clicked(View v) {
        if(buttonArray[2][2].getVisibility() == View.VISIBLE) {
            buttonArray[2][2].setVisibility(View.GONE);
        }
        else if(buttonArray[2][1].getVisibility() == View.VISIBLE) {
            buttonArray[2][1].setVisibility(View.GONE);
        }
        else if(buttonArray[2][0].getVisibility() == View.VISIBLE) {
            buttonArray[2][0].setVisibility(View.GONE);
        }
    }


    public void onDoneClicked(View v) {
        if(hasASelect == 0) {
            return; //do nothing
        }

        unselectEffect();
        //unembolden the previously selected one
        hideEditBar();

        if(val1.getText().toString().length() == 0) model.val1[curi][curj] = 0;
        else model.val1[curi][curj] = Integer.parseInt(val1.getText().toString());
        if(val2.getText().toString().length() == 0) model.val2[curi][curj] = 0;
        else model.val2[curi][curj] = Integer.parseInt(val2.getText().toString());
        if(val3.getText().toString().length() == 0) model.val3[curi][curj] = 0;
        else model.val3[curi][curj] = Integer.parseInt(val3.getText().toString());
        if(weight.getText().toString().length()==0) model.aWeight[curi][curj] = 0;
        else model.aWeight[curi][curj] = Integer.parseInt(weight.getText().toString());
        if(doAWipe.isChecked()) model.doWipe[curi][curj] = 1;
        else model.doWipe[curi][curj] = 0;
        if(useOrigin.isChecked()) model.fromSource[curi][curj] = 1;
        else if(useOne.isChecked()) model.fromSource[curi][curj] = 2;
        else model.fromSource[curi][curj] = 0;
        model.aOut[curi][curj] = effectSel.getSelectedItemPosition();
        buttonArray[curi][curj].setText(effectSel.getSelectedItem().toString());
        if(BTF.isChecked()) model.doWipe[curi][curj] += 4; //for configuration on Octaver

    }
    void showEditBar() {

        editEffect.setVisibility(View.VISIBLE);
        val1.setText(String.valueOf(model.val1[curi][curj]));
        val2.setText(String.valueOf(model.val2[curi][curj]));
        val3.setText(String.valueOf(model.val3[curi][curj]));
        weight.setText(String.valueOf(model.aWeight[curi][curj]));
        effectSel.setSelection(model.aOut[curi][curj]);
        if(model.fromSource[curi][curj] == 1) useOrigin.setChecked(true);
        else useOrigin.setChecked(false);
        if(model.fromSource[curi][curj] == 2) useOne.setChecked(true);
        else useOne.setChecked(false);
        //Toast.makeText(this, "doWipe = "+model.doWipe[curi][curj], Toast.LENGTH_LONG).show();
        if((model.doWipe[curi][curj] & 3) == 1) doAWipe.setChecked(true);
        else doAWipe.setChecked(false);
        if((model.doWipe[curi][curj] & 4) == 4) BTF.setChecked(true);
        else BTF.setChecked(false);
        setSelEffect();

    }
    void hideEditBar() {
        editEffect.setVisibility(View.GONE);
    }
    public void readFile(){
        try {
            model.readPreset(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    void setSelEffect() {
        //todo set/add recommended values, modify possible values when converting to 12-bit
        weight.setVisibility(View.VISIBLE);
        weightTitle.setVisibility(View.VISIBLE);
        if(curi==0) {
            useOrigin.setVisibility(View.GONE);
            useOne.setVisibility(View.GONE);
        }
        if(curi==1) {
            useOrigin.setVisibility(View.VISIBLE);
            useOne.setVisibility(View.GONE);
        }
        if(curi==2) {
            useOrigin.setVisibility(View.VISIBLE);
            useOne.setVisibility(View.VISIBLE);
        }
        switch(effectSel.getSelectedItem().toString()) {
            case "Clean":
                doAWipe.setVisibility(View.GONE);
                BTF.setVisibility(View.GONE);
                val1.setVisibility(View.GONE);
                val2.setVisibility(View.GONE);
                val3.setVisibility(View.GONE);
                values.setVisibility(View.GONE);
                break;
            case "Bitcrusher":
                doAWipe.setVisibility(View.GONE);
                BTF.setVisibility(View.GONE);
                val1.setVisibility(View.VISIBLE);
                val2.setVisibility(View.GONE);
                val3.setVisibility(View.GONE);
                values.setVisibility(View.VISIBLE);
                values.setText("Bitcrush value (1-12)");
                break;
            case "Booster":
                doAWipe.setVisibility(View.GONE);
                BTF.setVisibility(View.GONE);
                val1.setVisibility(View.VISIBLE);
                val2.setVisibility(View.GONE);
                val3.setVisibility(View.GONE);
                values.setVisibility(View.VISIBLE);
                values.setText("Booster value (0-32768)");
                break;
            case "Delay":
                doAWipe.setVisibility(View.GONE);
                BTF.setVisibility(View.GONE);
                val1.setVisibility(View.VISIBLE);
                val2.setVisibility(View.GONE);
                val3.setVisibility(View.GONE);
                values.setVisibility(View.VISIBLE);
                values.setText("Delay time (~4000=1 second)");
                break;
            case "Distortion":
                doAWipe.setVisibility(View.GONE);
                BTF.setVisibility(View.GONE);
                val1.setVisibility(View.VISIBLE);
                val2.setVisibility(View.GONE);
                val3.setVisibility(View.GONE);
                values.setVisibility(View.VISIBLE);
                values.setText("Distort value (0-32768)");
                break;
            case "Echo":
                doAWipe.setVisibility(View.GONE);
                BTF.setVisibility(View.GONE);
                val1.setVisibility(View.VISIBLE);
                val2.setVisibility(View.GONE);
                val3.setVisibility(View.GONE);
                values.setVisibility(View.VISIBLE);
                values.setText("Echo time (~4000=1 second)");
                break;
            case "Fuzz":
                doAWipe.setVisibility(View.GONE);
                BTF.setVisibility(View.GONE);
                val1.setVisibility(View.VISIBLE);
                val2.setVisibility(View.GONE);
                val3.setVisibility(View.GONE);
                values.setVisibility(View.VISIBLE);
                values.setText("Fuzz value (0-32768)");
                break;
            case "Looper":
                doAWipe.setVisibility(View.VISIBLE);
                BTF.setVisibility(View.GONE);
                val1.setVisibility(View.VISIBLE);
                val2.setVisibility(View.GONE);
                val3.setVisibility(View.GONE);
                values.setVisibility(View.VISIBLE);
                values.setText("Looper time (~4000=1 second, 0=dynamic length)");
                break;
            case "Octaver":
                doAWipe.setVisibility(View.VISIBLE);
                BTF.setVisibility(View.VISIBLE);
                BTF.setText("Looper?");
                val1.setVisibility(View.VISIBLE);
                val2.setVisibility(View.VISIBLE);
                val3.setVisibility(View.GONE);
                values.setVisibility(View.VISIBLE);
                values.setText("Left: Array/Looper time (~4000=1 second, 0=dynamic length for looper only) | Right: Octaver type (0=half, 1=normal, 2=double)");
                break;
            case "Reverb":
                doAWipe.setVisibility(View.GONE);
                BTF.setVisibility(View.GONE);
                val1.setVisibility(View.VISIBLE);
                val2.setVisibility(View.VISIBLE);
                val3.setVisibility(View.VISIBLE);
                values.setVisibility(View.VISIBLE);
                values.setText("Echo times (~4000=1 second), up to 3 echos in reverb");
                break;
            case "Tremolo":
                doAWipe.setVisibility(View.GONE);
                BTF.setVisibility(View.GONE);
                val1.setVisibility(View.VISIBLE);
                val2.setVisibility(View.GONE);
                val3.setVisibility(View.GONE);
                values.setVisibility(View.VISIBLE);
                values.setText("Tremolo Speed (0-999, the smaller the value the faster the tremolo)");
                break;
            case "Reverse Looper":
                doAWipe.setVisibility(View.VISIBLE);
                BTF.setVisibility(View.VISIBLE);
                BTF.setText("Back-to-Front?");
                val1.setVisibility(View.VISIBLE);
                val2.setVisibility(View.GONE);
                val3.setVisibility(View.GONE);
                values.setVisibility(View.VISIBLE);
                values.setText("Looper Time(~4000=1 second, 0=dynamic length)");
                break;
            case "No Sound":
                doAWipe.setVisibility(View.GONE);
                useOne.setVisibility(View.GONE);
                useOrigin.setVisibility(View.GONE);
                BTF.setVisibility(View.GONE);
                val1.setVisibility(View.GONE);
                val2.setVisibility(View.GONE);
                val3.setVisibility(View.GONE);
                values.setVisibility(View.GONE);
                weight.setVisibility(View.GONE);
                weightTitle.setVisibility(View.GONE);
                break;
            case "Forward/Backward Looper":
                val1.setVisibility(View.VISIBLE);
                val2.setVisibility(View.GONE);
                val3.setVisibility(View.GONE);
                values.setVisibility(View.VISIBLE);
                values.setText("Looper Time (~4000=1 second, 0=dynamic length)");
                break;
            default:
                Toast.makeText(this, "You shouldn't see this", Toast.LENGTH_LONG).show();
                break;
        }
    }


}
