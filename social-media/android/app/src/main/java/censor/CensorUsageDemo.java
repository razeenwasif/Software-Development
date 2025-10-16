package censor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A demo class to show how to instantiate the various censor implementations.
 */
public class CensorUsageDemo {

    // Initialize word lists from the hardcoded data class.
    private static final Set<String> badWords = new HashSet<>(Arrays.asList(WordListData.BAD_WORDS));
    private static final Set<String> goodWords = new HashSet<>(Arrays.asList(WordListData.GOOD_WORDS));

    /**
     * Provides a censor that handles leet speak and complex cases.
     * @return An ICensor instance with the default algorithm.
     */
    public static ICensor censorProfanity() {
        return new CensorAlgorithm(badWords, goodWords);
    }

    /**
     * Provides a censor that blocks the entire message if profanity is found.
     * @return An ICensor instance with the blocking strategy.
     */
    public static ICensor blockProfanity() {
        return new BlockProfanity(badWords, goodWords);
    }

    /**
     * Provides a censor that only censors case-sensitive profanity.
     * @return An ICensor instance with the case-sensitive strategy.
     */
    public static ICensor censorProfanityCaseSensitive() {
        return new CensorProfanityCaseSensitive(badWords, goodWords);
    }

    /**
     * Provides a censor that only censors the word 'Bernardo'.
     * @return An ICensor instance with the Bernardo-specific strategy.
     */
    public static ICensor censorBernardo() {
        return new CensorBernardo(badWords, goodWords);
    }
}
