package com.example.akgarhwal.ghost;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Ghost turn";
    private static final String USER_TURN = StartActivity.user_name+" turn";
    private static String USER_SCORE = StartActivity.user_name+" Score: ";
    private static final String GHOST_SCORE = "Ghost : ";
    private static final String COLOR_RED = "#e20202";
    private static final String COLOR_GREEN = "#0fde24";
    private static final Integer MARKOV_ORDER = 3;

    private static Boolean USER_WON = false;
    private static int userScore = 0;
    private static int ghostScore = 0;
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private TextView userScoreTextView;
    private TextView ghostScoreTextView;
    private TextView ghostTextView;
    public static TextView gameStatus;
    public static String word_meaning;
    private TextToSpeech tts;
    private View win_toast_view;
    private View loss_toast_view;
    private Toast win_toast;
    private Toast loss_toast;

    public static MarkovModel model;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F50057")));
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });
        //Creating the LayoutInflater instance
        LayoutInflater li = getLayoutInflater();
        win_toast_view = li.inflate(R.layout.win_toast, (ViewGroup) findViewById(R.id.win_toast));
        loss_toast_view = li.inflate(R.layout.loss_toast, (ViewGroup) findViewById(R.id.loss_toast));

        // toast setup
        win_toast = new Toast(getApplicationContext());
        win_toast.setDuration(Toast.LENGTH_SHORT);
        win_toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        win_toast.setView(win_toast_view);//setting the view of custom toast layout
        loss_toast = new Toast(getApplicationContext());
        loss_toast.setDuration(Toast.LENGTH_SHORT);
        loss_toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        loss_toast.setView(loss_toast_view);//setting the view of custom toast layout

        new MyTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,(Void[])null);
    }


    private class MyTask extends AsyncTask<Void,Void,Void>{

        private Void res;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                AssetManager assetManager = getAssets();
                try {
                    model = new MarkovModel(MARKOV_ORDER);
                    dictionary = new FastDictionary(assetManager.open("words.txt"));

                } catch (IOException e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Could not load dictionary", Toast.LENGTH_LONG);
                    toast.show();
                }
                userScoreTextView = (TextView) findViewById(R.id.userScore);
                ghostScoreTextView = (TextView) findViewById(R.id.ghostScore);
                ghostTextView = (TextView)findViewById(R.id.ghostText);
                gameStatus = (TextView) findViewById(R.id.gameStatus);
                userScore = 0;
                ghostScore = 0;

                USER_SCORE = StartActivity.user_name + ": ";

            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            onStart(null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GhostActivity.this);
            progressDialog.setTitle("Reading Dictionary");
            progressDialog.setMessage("training markov model...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        //handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.home_button:
                Intent in = new Intent(this, StartActivity.class);
                startActivity(in);
                finish();
                return true;

            case R.id.speak_button:
                String toSpeak = ghostTextView.getText().toString().trim();
                if ( toSpeak != null && toSpeak != "") {
                    Toast.makeText(getApplicationContext(),toSpeak,Toast.LENGTH_SHORT).show();
                    tts.speak(toSpeak, TextToSpeech.QUEUE_ADD, null, null);
                    if ( word_meaning != null ) {
                        tts.speak(word_meaning, TextToSpeech.QUEUE_ADD, null, null);
                    }
                    Log.v("Word for Speak",toSpeak);

                } else {
                    Toast.makeText(getApplicationContext(),"No word Found.",Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {

        USER_WON = false;
        userScoreTextView.setText(USER_SCORE+userScore);
        ghostScoreTextView.setText(GHOST_SCORE+ghostScore);

        userTurn = random.nextBoolean();
        ghostTextView.setText("");
        if (userTurn) {
            gameStatus.setText(USER_TURN);
        } else {
            gameStatus.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    //do final update of score and gamestatus
    private void endGame(String message){

        userTurn = false;

        if( USER_WON ){
            userScore += 1;
            gameStatus.setText(message + "\ngetting Meaning");
            win_toast.show();
        }
        else{
            ghostScore += 1;
            gameStatus.setText(message + "\ngetting Meaning");
            loss_toast.show();
        }

        //get Meaning of word
        Meaning meaning = new Meaning();
        meaning.updateMeaning(ghostTextView.getText().toString());

        userScoreTextView.setText(USER_SCORE+userScore);
        ghostScoreTextView.setText(GHOST_SCORE+ghostScore);

    }


    private void computerTurn() {
        // Do computer turn stuff then make it the user's turn again

        String wordFragment = ghostTextView.getText().toString();
        if( wordFragment.length() >= 4 && dictionary.isWord(wordFragment) ){
            USER_WON = false;
            endGame("You Looser :( [Valid Word]");
        }
        else{
            Log.d("TAG","Calling getGoodWord for Fast Dictionary");
            String nextWord = dictionary.getGoodWordStartingWith(wordFragment);
            //Log.d("TAG","Word found : "+nextWord);
            if(nextWord == null){
                USER_WON = false;
                endGame("you can't bluff ME!");
            }
            else{
                wordFragment = wordFragment + nextWord.charAt(wordFragment.length());
                ghostTextView.setText(  (wordFragment) );
                //set userTurn True
                userTurn = true;
                gameStatus.setText(USER_TURN);
            }
        }
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("TAG", "onKeyUp: "+keyCode+" "+ (char) event.getUnicodeChar());

        char keyPressed = (char) event.getUnicodeChar();
        if( userTurn == true && (Character.isLetter(keyPressed)) ) {

            String existingWord = ghostTextView.getText().toString();
            existingWord +=  Character.toLowerCase(keyPressed);

            //color text
            ghostTextView.setText( (existingWord) );

            //set userTurn to false because now its computer turn
            userTurn = false;

            //call the computer turn
            computerTurn();

            return true;
        }
        return super.onKeyUp(keyCode, event);
    }



    //Restart Button Function
    // restart the game
    public void restart(View view){
        word_meaning = null;
        onStart(null);
    }

    // challenge Handler function
    public void challenge(View view){
        if(userTurn == false ){      // imp case
            return ;
        }

        String word = ghostTextView.getText().toString();
        if( dictionary.isWord(word) && word.length()>=4 ){
            USER_WON = true;
            endGame("You Won :)");
        }
        else{
            String nextWord = dictionary.getAnyWordStartingWith(word);
            if(nextWord == null){
                USER_WON = true;
                endGame("You Won :) I am learning");
            }
            else{
                USER_WON = false;
                ghostTextView.setText( (nextWord) );
                endGame("You Looser :(");
            }
        }
    }

    //Save Game State
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        TextView ghostTextView = (TextView) findViewById(R.id.ghostText);
        TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
        outState.putString("ghost_text",ghostTextView.getText().toString());
        outState.putString("game_status",gameStatus.getText().toString());
        outState.putBoolean("userTurn",userTurn);
        outState.putInt("userScore",userScore);
        outState.putInt("ghostScore",ghostScore);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        TextView ghostTextView = (TextView) findViewById(R.id.ghostText);
        TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
        userTurn = savedInstanceState.getBoolean("userTurn");
        ghostTextView.setText(savedInstanceState.getString("ghost_text"));
        gameStatus.setText(savedInstanceState.getString("game_status"));
        userScore = savedInstanceState.getInt("userScore");
        ghostScore = savedInstanceState.getInt("ghostScore");
    }

}
