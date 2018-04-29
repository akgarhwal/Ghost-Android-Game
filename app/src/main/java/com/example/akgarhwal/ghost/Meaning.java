package com.example.akgarhwal.ghost;

/**
 * Created by akgarhwal
 */

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import static com.example.akgarhwal.ghost.GhostActivity.gameStatus;
import static com.example.akgarhwal.ghost.GhostActivity.word_meaning;

public class Meaning {

    public void updateMeaning(String word) {
        new CallbackTask().execute(dictionaryEntries(word));
    }

    private String dictionaryEntries(String word) {
        final String language = "en";
        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
    }

    private class CallbackTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {

            final String app_id = "XXXXXXX";
            final String app_key = "XXXXXXXXXXXXXXXXXXXXXXXX";
            try {

                Log.d("Tag", "doInBackground: ");
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("app_id",app_id);
                urlConnection.setRequestProperty("app_key",app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                return stringBuilder.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Tag1", result);
            System.out.print(result);

            String meaning = "Meaning not Found.";
            String text = gameStatus.getText().toString();
            try {
                int start = result.indexOf("\"definitions\": [");
                int end = result.indexOf("]", start);
                Log.d("Tag", "start" + start + " end" + end);
                meaning = result.substring(start, end);
                meaning = meaning.substring(16);

                Log.d("Tag", "onPostExecute: " + text.substring(text.length() - 15, text.length()));
                if (text.substring(text.length() - 15, text.length()).equals("getting Meaning")) {
                    gameStatus.setText(text.substring(0, text.length() - 15) + meaning.trim());
                    word_meaning = meaning.trim();
                }

            }
            catch(Exception e){
                try {
                    if (text.substring(text.length() - 15, text.length()).equals("getting Meaning")) {
                        gameStatus.setText(text.substring(0, text.length() - 15) + meaning);
                        word_meaning = null;
                    }
                }
                catch (Exception exce){
                    // donothing
                }
            }
        }
    }
}
