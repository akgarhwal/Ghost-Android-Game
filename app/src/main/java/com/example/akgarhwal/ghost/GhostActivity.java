package com.example.akgarhwal.ghost;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.Random;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;


public class GhostActivity extends Activity {
    private static final String COMPUTER_TURN = "Ghost's turn";
    private static final String USER_TURN = StartActivity.user_name+"'s turn";
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

    public static MarkovModel model;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
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
            progressDialog.setTitle("Creating Dictionary");
            progressDialog.setMessage("training markov model...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml

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
        }
        else{
            ghostScore += 1;
            gameStatus.setText(message + "\ngetting Meaning");
        }

        //get Meaning of word
        Meaning meaning = new Meaning(ghostTextView.getText().toString());


        userScoreTextView.setText(USER_SCORE+userScore);
        ghostScoreTextView.setText(GHOST_SCORE+ghostScore);

    }

    private void computerTurn() {
        // Do computer turn stuff then make it the user's turn again

        String wordFragment = ghostTextView.getText().toString();
        if( wordFragment.length() >= 4 && dictionary.isWord(wordFragment) ){
            USER_WON = false;
            endGame("You Losser :( [formed valid word]");
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
                endGame("You Losser :(");
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
