package censor;

import java.util.Set;

/** 
 * A package-private utility class that contains all logic for handling "leet speak" decoding and censoring. 
 */
class LeetSpeakUtil {

    /**
     * A public data class to hold the result of leet speak decoding. 
     */
    public static class LeetSpeakResult {
        public final String decodedString;
        public final int[] indexMap;

        /**
         * Constructs a LeetSpeakResult.
         * @param decodedString The decoded string.
         * @param indexMap The map of indices.
         */
        LeetSpeakResult(String decodedString, int[] indexMap) {
            this.decodedString = decodedString;
            this.indexMap = indexMap;
        }
    }

    /**
     * Utility method to decode a message containing "leet speak".
     * @param message The message to decode.
     * @return A {@link LeetSpeakResult} containing the decoded string and index map.
     */
    public static LeetSpeakResult decodeLeetSpeak(String message) {
        char[] f = new char[message.length()];
        int[] m = new int[f.length];
        int j = 0;
        for (int i = 0; i < message.length(); i++) {
            char c = message.toLowerCase().charAt(i);
            switch (c) {
                case '1': case '|': case '/': case '\\': f[j] = 'l'; break;
                case '3': f[j] = 'e'; break;
                case '4': case '@': f[j] = 'a'; break;
                case '5': case '$': f[j] = 's'; break;
                case '6': f[j] = 'b'; break;
                case '9': f[j] = 'g'; break;
                case '0': f[j] = 'o'; break;
                case '!': f[j] = 'i'; break;
                default:
                    if (Character.isAlphabetic(c)) { f[j] = c; } else { continue; }
                    break;
            }
            m[j] = i; j++;
        }
        return new LeetSpeakResult(new String(f, 0, j), m);
    }
}