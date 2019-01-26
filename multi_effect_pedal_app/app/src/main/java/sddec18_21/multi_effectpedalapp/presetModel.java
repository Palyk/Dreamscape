package sddec18_21.multi_effectpedalapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

//todone hello here's a list of what to do throughout the rest of this functionality
//regarding this, the activity needs to have onClicks or onTaps or whatever that updates these values accordingly
//readPreset will be called on creation automatically, make sure when the activity gets called that the file is properly passed
//when the activity save button is pressed, call writePreset to save the preset back to the file
//todone writing and reading may need further testing to confirm
//regarding uploading/downloading:
//methods will need to be implemented in configActivity, and will simply send/receive data via bluetooth method
//similar in format as the playactivity
//Will have to wait and see what tyler implements there before editing but
//todone implement upload and download methods
//download: send request command, wait to recieve a response from the pi, read the input into a string, save as a file
//upload: once selected preset spot, header string with "d<x>" where x is the preset number, then append contents of desired
//file and send via bluetooth

public class presetModel {
    static final int NUM_EFFECTS = 3;
    int[][] aOut;
    int[][] aWeight;
    int[][] val1;
    int[][] val2;
    int[][] val3;
    int[][] fromSource;
    int[][] doWipe;
    String name;

    File referenceFileName;

    presetModel() {
        val1 = new int[NUM_EFFECTS][NUM_EFFECTS];
        val2 = new int[NUM_EFFECTS][NUM_EFFECTS];
        val3 = new int[NUM_EFFECTS][NUM_EFFECTS];
        aOut = new int[NUM_EFFECTS][NUM_EFFECTS];
        aWeight = new int[NUM_EFFECTS][NUM_EFFECTS];
        fromSource = new int[NUM_EFFECTS][NUM_EFFECTS];
    }
    presetModel(File f) throws FileNotFoundException {
        val1 = new int[NUM_EFFECTS][NUM_EFFECTS];
        val2 = new int[NUM_EFFECTS][NUM_EFFECTS];
        val3 = new int[NUM_EFFECTS][NUM_EFFECTS];
        aOut = new int[NUM_EFFECTS][NUM_EFFECTS];
        aWeight = new int[NUM_EFFECTS][NUM_EFFECTS];
        fromSource = new int[NUM_EFFECTS][NUM_EFFECTS];
        doWipe = new int[NUM_EFFECTS][NUM_EFFECTS];

        referenceFileName = f;
        readPreset(f);
    }
    public void readPreset(File f) throws FileNotFoundException {
        Scanner scanner = new Scanner(f);
        String inLine;
        int isValid = 0;
        int isStart = 0;
        int i=0;
        int j=0;

        while(scanner.hasNext()) {
            inLine = scanner.next();
            Log.d("LOOK", inLine);
            if(!inLine.equals("447448")&& isValid == 0) {
                return;
            }
            else if(inLine.equals("447448")) {
                isValid = 1;
            }
            else if(inLine.equals("NAME")) {
                name = scanner.next();
            }
            else if(inLine.equals("START")) {
                isStart = 1;
            }
            else if(isStart == 0) {
                return;
            }
            else if(inLine.equals("STEP")) {
                ++i;
                j=0;
            }
            else if(inLine.equals("CLEAN")) {
                aOut[i][j] = 0;
                aWeight[i][j] = scanner.nextInt();
                fromSource[i][j] = scanner.nextInt();
                ++j;
            }
            else if(inLine.equals("BITCRUSH")) {
                aOut[i][j] = 1;
                val1[i][j] = scanner.nextInt();
                aWeight[i][j] = scanner.nextInt();
                fromSource[i][j] = scanner.nextInt();
                ++j;
            }
            else if(inLine.equals("BOOSTER")) {
                aOut[i][j] = 2;
                val1[i][j] = scanner.nextInt();
                aWeight[i][j] = scanner.nextInt();
                fromSource[i][j] = scanner.nextInt();
                ++j;
            }
            else if(inLine.equals("DELAY")) {
                aOut[i][j] = 3;
                val1[i][j] = scanner.nextInt();
                aWeight[i][j] = scanner.nextInt();
                fromSource[i][j] = scanner.nextInt();
                ++j;
            }
            else if(inLine.equals("DISTORT")) {
                aOut[i][j] = 4;
                val1[i][j] = scanner.nextInt();
                aWeight[i][j] = scanner.nextInt();
                fromSource[i][j] = scanner.nextInt();
                ++j;
            }
            else if(inLine.equals("ECHO")) {
                aOut[i][j] = 5;
                val1[i][j] = scanner.nextInt();
                aWeight[i][j] = scanner.nextInt();
                fromSource[i][j] = scanner.nextInt();
                ++j;
            }
            else if(inLine.equals("FUZZ")) {
                aOut[i][j] = 6;
                val1[i][j] = scanner.nextInt();
                aWeight[i][j] = scanner.nextInt();
                fromSource[i][j] = scanner.nextInt();
                ++j;
            }
            else if(inLine.equals("LOOPER")) {
                aOut[i][j] = 7;
                val1[i][j] = scanner.nextInt();
                doWipe[i][j] = scanner.nextInt();
                aWeight[i][j] = scanner.nextInt();
                fromSource[i][j] = scanner.nextInt();
                ++j;
            }
            else if(inLine.equals("OCTAVER")) {
                aOut[i][j] = 8;
                val1[i][j] = scanner.nextInt();
                val2[i][j] = scanner.nextInt();
                doWipe[i][j] = scanner.nextInt();
                aWeight[i][j] = scanner.nextInt();
                fromSource[i][j] = scanner.nextInt();
                ++j;
            }
            else if(inLine.equals("REVERB")) {
                aOut[i][j] = 9;
                val1[i][j] = scanner.nextInt();
                val2[i][j] = scanner.nextInt();
                val3[i][j] = scanner.nextInt();
                aWeight[i][j] = scanner.nextInt();
                fromSource[i][j] = scanner.nextInt();
                ++j;
            }
            else if(inLine.equals("TREMOLO")) {
                aOut[i][j] = 10;
                val1[i][j] = scanner.nextInt();
                aWeight[i][j] = scanner.nextInt();
                fromSource[i][j] = scanner.nextInt();
                ++j;
            }
            else if(inLine.equals("RLOOPER")) {
                aOut[i][j] = 11;
                val1[i][j] = scanner.nextInt();
                doWipe[i][j] = scanner.nextInt();
                aWeight[i][j] = scanner.nextInt();
                fromSource[i][j] = scanner.nextInt();
                ++j;
            }
            else if(inLine.equals("NOSOUND")) {
                aOut[i][j] = 12;
                ++j;
            }
            else if(inLine.equals("FBLOOPER")) {
                aOut[i][j] = 13;
                val1[i][j] = scanner.nextInt();
                doWipe[i][j] = scanner.nextInt();
                aWeight[i][j] = scanner.nextInt();
                fromSource[i][j] = scanner.nextInt();
                ++j;
            }
            else if(inLine.equals("END")) {
                scanner.close();
                return;
            }
            else {
                Log.d("LOOK", "Something got read wrong");
            }
        }
    }
    public void writePreset(File f) throws IOException {
        //reverse, writing the preset manually
        FileWriter writer = new FileWriter(f);
        writer.write("447448\n");
        writer.write("NAME "+name+"\n");
        writer.write("START\n\n");
        for(int i=0; i<NUM_EFFECTS; ++i) {
            for(int j=0; j<NUM_EFFECTS; ++j) {
               switch(aOut[i][j]) {
                   case 0: writer.write("CLEAN " + aWeight[i][j] + " " + fromSource[i][j] + "\n");
                        break;
                   case 1: writer.write("BITCRUSH " + val1[i][j] + " " + aWeight[i][j] + " " + fromSource[i][j] + "\n");
                        break;
                   case 2: writer.write("BOOSTER " + val1[i][j] + " " + aWeight[i][j] + " " + fromSource[i][j] + "\n");
                        break;
                   case 3: writer.write("DELAY " + val1[i][j] + " " + aWeight[i][j] + " " + fromSource[i][j] + "\n");
                        break;
                   case 4: writer.write("DISTORT " + val1[i][j] + " " + aWeight[i][j] + " " + fromSource[i][j] + "\n");
                       break;
                   case 5: writer.write("ECHO " + val1[i][j] + " " + aWeight[i][j] + " " + fromSource[i][j] + "\n");
                       break;
                   case 6: writer.write("FUZZ " + val1[i][j] + " " + aWeight[i][j] + " " + fromSource[i][j] + "\n");
                       break;
                   case 7: writer.write("LOOPER " + val1[i][j] + " " + doWipe[i][j] + " " + aWeight[i][j] + " " + fromSource[i][j] + "\n");
                       break;
                   case 8: writer.write("OCTAVER " + val1[i][j] + " " + val2[i][j] + " " + doWipe[i][j] + " " + aWeight[i][j] + " " + fromSource[i][j] + "\n");
                       break;
                   case 9: writer.write("REVERB " + val1[i][j] + " " + val2[i][j] + " " + val3[i][j] + " " + aWeight[i][j] + " " + fromSource[i][j] + "\n");
                       break;
                   case 10: writer.write("TREMOLO " + val1[i][j] + " " + aWeight[i][j] + " " + fromSource[i][j] + "\n");
                       break;
                   case 11: writer.write("RLOOPER " + val1[i][j] + " " + doWipe[i][j] + " " + aWeight[i][j] + " " + fromSource[i][j] + "\n");
                       break;
                   case 12: writer.write("NOSOUND" + "\n");
                    break;
                   case 13: writer.write("FBLOOPER " + val1[i][j] + " " + doWipe[i][j] + " " + aWeight[i][j] + " " + fromSource[i][j] + "\n");
                       break;
                   default: writer.write("ERROR\n");
                    break;
                }
            }
            writer.write("STEP\n");
        }
        writer.write("END");
        writer.close();
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while((line = br.readLine()) != null) {
            Log.d("FileReadingDebug", line);
        }
        return;
    }
    //called if the activity for a specific button is GONE, sets all values for that effect to NOSOUND
    public void voidEffect(int i, int j) {
        aOut[i][j]=12;
        aWeight[i][j]=0;
        val1[i][j]=0;
        val2[i][j]=0;
        val3[i][j]=0;
        fromSource[i][j]=0;
    }
}
