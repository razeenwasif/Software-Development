package censor;

import java.util.Set;

/**
 * A highly specialized censor for the word "bernardo", including "leet speak" variations.
 */
class CensorBernardo extends AbstractCensor {

	public CensorBernardo(Set<String> badWords, Set<String> goodWords) {
		super(badWords, goodWords);
		// This censor should only use its own specific word list.
		this.badWords.clear();
		this.badWords.add("bernardo");
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public String censorMessage(String message) {
        char[] result = message.toCharArray();
        LeetSpeakUtil.LeetSpeakResult leetResult = LeetSpeakUtil.decodeLeetSpeak(message);

        // Censor the message using the shared helper method
        censorLeetResult(result, leetResult, this.badWords);

        return new String(result);
    }

    @Override
    protected String handleProfanity(String originalWord, String profaneWord) {
        // Not used in this implementation.
        return originalWord;
    }
}