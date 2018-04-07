package com.example.akgarhwal.ghost;

/**
 * Created by akgarhwal
 */

public interface GhostDictionary {
    public final static int MIN_WORD_LENGTH = 4;
    boolean isWord(String word);
    String getAnyWordStartingWith(String prefix);
    String getGoodWordStartingWith(String prefix);
}
