package com.example.akgarhwal.ghost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RuleActivity extends Activity {

    String[] rules = {"Ghost is a word game in which players take turns adding letters to a growing word fragment, " +
            "trying not to be the one to complete a valid word.",
            "Each incomplete wordmust be the beginning of an actual word, and  minmum length of a word that counts is 4 letters",
            "The player who completes a word loses the round.",
            "A player is chosen at random to start the game, and begins by naming any letter of the alphabet.",
            "Players then take turns to add letters to this fragment, with the aim being to avoid completing an actual word.",
            "The player whose turn it is may - instead of adding a letter - challenge the previous player to prove that the current fragment is actually the beginning of a word. ",
            "If the challenged player can name such a word, the challenger loses the round; otherwise the challenged player loses the round. ",
            "If a player bluffs, or completes a word without other players noticing, then play continues."};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.list_text_view, rules);

        ListView listView = (ListView) findViewById(R.id.rule_list);
        listView.setAdapter(adapter);
    }
    public void home(View view){
        Intent rules = new Intent(this, StartActivity.class);
        startActivity(rules);
        finish();
    }
}
