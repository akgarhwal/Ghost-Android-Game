package com.example.akgarhwal.ghost;

/**
 * Created by akgarhwal
 */

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
        TrieNode currentNode = this;
        String temp = "";
        for (int i=0; i< s.length(); i++ ){
            temp += s.charAt(i);
            if( currentNode.children.containsKey(temp) ){
                //looks good keep going
            }
            else{
                //add new Node;
                TrieNode newNode = new TrieNode();
                currentNode.children.put(temp,newNode);
            }
            currentNode = currentNode.children.get(temp);
        }
        currentNode.isWord = true;
    }

    public boolean isWord(String s) {

        TrieNode currentNode = this;
        String temp = "";
        //Log.d("TAG","Checking for "+s);

        for (int i=0; i< s.length(); i++){
            temp += s.charAt(i);
            if( !(currentNode.children.containsKey(temp)) ){
                //string does not exist in trie so return false;
                return false;
            }
            currentNode = currentNode.children.get(temp);
        }
        //Log.d("TAG", "Searching for word completed");
        // word exist so return true;
        if( currentNode.isWord ){
            return true;
        }

        return false;
    }

    public String getAnyWordStartingWith(String s) {
        // Log.d("TAG","getAnyWord function Start");
        TrieNode currentNode = this;
        String temp = "";
        Random random = new Random();
        if(s == ""){
            //select random char for computer for first time.
            int randInt = random.nextInt(26);
            s +=  (char)(randInt+97);
        }

        for (int i=0; i < s.length(); i++){
            temp += s.charAt(i);
            if( !( currentNode.children.containsKey(temp)) ){
                return null;
            }
            currentNode = currentNode.children.get(temp);
        }
        //Now we process the prefix and we need to select a word randomly that is completed.
        Set<String> keySet;
        //Log.d("TAG","getAnyWord function process prefix.");
        while( !(currentNode.isWord) ){
            keySet = currentNode.children.keySet();
            temp = keySet.toArray()[random.nextInt(keySet.size())].toString();
            Log.d("TAG",""+temp);
            currentNode = currentNode.children.get(temp);
        }
        // Log.d("TAG","getAnyWord function Finish");
        return temp;
    }

    public String getGoodWordStartingWith(String s) {

        TrieNode currentNode = this;
        String temp = "";
        Random random = new Random();
        if(s == ""){
            //select random char for computer for first time.
            int randInt = random.nextInt(26);
            s +=  (char)(randInt+97);
        }

        for (int i=0; i < s.length(); i++){
            temp += s.charAt(i);
            if( !( currentNode.children.containsKey(temp)) ){
                return null;
            }
            currentNode = currentNode.children.get(temp);
        }
        //now select best possible word for computer
        ArrayList<String> keySet = new ArrayList<>(currentNode.children.keySet());
        ArrayList<String> notCompleteWord = new ArrayList<>();

        for (int i=0; i< keySet.size();i++){
            if( !(currentNode.children.get( keySet.get(i) ).isWord) ){
                notCompleteWord.add( keySet.get(i) );
            }
        }
        //  Log.d("TAG","KeySet : "+keySet);
        Log.d("TAG","incomplete word : "+notCompleteWord);
        //select word which is not complete
        if( !(notCompleteWord.isEmpty()) ){
            return notCompleteWord.get( random.nextInt(notCompleteWord.size()) );
        }
        //OOPS I am going to lose.....
        // lets use markov mo+del to cheat user
        Character next = GhostActivity.model.nextLetter(s);
        if(next == null){
            return keySet.get( random.nextInt(keySet.size()) );
        }
        Log.d("TAG","Mrakov Model Used");
        return s+next;
    }
}

