package com.example.akgarhwal.ghost;

/**
 * Created by akgarhwal
 */

import android.util.Log;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

public class MarkovModel {
    private HashMap<String,Bag> model;
    public static int ORDER;

    public MarkovModel(){
        model = new HashMap<>();
        ORDER = 1;
    }

    public MarkovModel(int order) {
        model = new HashMap<>();
        ORDER = order;
    }


    public void addText(String input){

        for (int i=0; i < ( input.length()-ORDER ) ; i++){
            String ngram = input.substring( i , i + ORDER);
            Character nextLetter = input.charAt( i + ORDER);

            //Log.d("TAG","Ngram : "+ngram + "  nextLetter : "+nextLetter);

            if(model.containsKey(ngram) ){
                Bag bag = model.get(ngram);
                bag.add(nextLetter);
                model.put(ngram,bag);
            }
            else{
                Bag bag = new Bag();
                bag.add(nextLetter);
                model.put(ngram,bag);
            }
        }
    }

    public Character nextLetter(String state){
        int len = state.length();
        state = state.substring(len-ORDER,len);
        if(model.containsKey(state)){
            return model.get(state).getNextLetter();
        }
        return null;
    }

    public void loadFile(InputStream wordListStream){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(wordListStream));
            String line;
            while ((line = br.readLine()) != null) {
                this.addText(line);
            }
            br.close();

        } catch (IOException e) {
            Log.d("TAG","ERROR: unable to read file.");
            e.printStackTrace();
        }
    }
}
