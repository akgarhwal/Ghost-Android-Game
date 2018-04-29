package com.example.akgarhwal.ghost;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    private EditText name;

    public static String user_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F50057")));
        name = (EditText) findViewById(R.id.editText);

    }

    public void startGame(View view){
        user_name = name.getText().toString();
        user_name = user_name.substring(0,Math.min(user_name.length(),7));
        user_name = user_name.trim();
        if(user_name.length()==0 ){
            Toast.makeText(getApplicationContext(),"Enter Valid Name",Toast.LENGTH_SHORT).show();
        }
        else if(user_name.equalsIgnoreCase("ghost")){
            Toast.makeText(getApplicationContext(),"Your name can not be "+(user_name),Toast.LENGTH_SHORT).show();
        }
        else {
            Intent startGame = new Intent(this, GhostActivity.class);
            startActivity(startGame);
            finish();
        }
    }
    public void rule(View view){
        Intent rules = new Intent(this, RuleActivity.class);
        startActivity(rules);
        finish();
    }

    public void exit(View view){
        System.exit(0);
    }

}
