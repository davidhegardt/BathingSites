package se.miun.dahe1501.bathingsites;

/**
 * Created by Dave on 2017-05-15.
 * Helper class used to parse and trim strings
 */

public final class Helper {

    public static String newline(String word){
        word = word.replaceAll("<br>","\n");        // Replace HTML breakline with string breakline

        return word;
    }

    public static String trimString(String word,String replacer){
        word = word.replaceAll(replacer,"");            // Remove word from string

        return word;
    }
}
