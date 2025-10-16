package censor;

import java.util.Set;
import java.util.StringJoiner;

/**
 * A greatly simplified abstract base class for censoring messages.
 * It no longer handles I/O and receives its word lists via its constructor.
 */
abstract class AbstractCensor implements ICensor {

	protected Set<String> badWords;
	protected Set<String> goodWords;

	public AbstractCensor(Set<String> badWords, Set<String> goodWords) {
		this.badWords = badWords;
		this.goodWords = goodWords;
	}

	/**
	 * Helper method that censors a decoded leet speak string.
	 * @param result The character array of the original message to modify.
	 * @param leetResult The result of the leet speak decoding.
	 * @param badWords The set of bad words to censor.
	 */
	protected void censorLeetResult(char[] result, LeetSpeakUtil.LeetSpeakResult leetResult, Set<String> badWords) {
        for (String word : badWords) {
            int startIndex = 0;
            while ((startIndex = leetResult.decodedString.indexOf(word, startIndex)) != -1) {
                // Censor the found word, leaving the first letter
                for (int l = 1; l < word.length(); l++) {
                    result[leetResult.indexMap[startIndex + l]] = '*';
                }
                startIndex += word.length();
            }
        }
    }

	protected boolean isBlockOnProfanity() {
		return false;
	}

	@Override
	public String censorMessage(String message) {
		String[] words = message.split("\\s+");

		if (isBlockOnProfanity()) {
			for (String word : words) {
				String lowerCaseWord = word.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
				if (badWords.contains(lowerCaseWord) && !goodWords.contains(lowerCaseWord)) {
					return "[BLOCKED]";
				}
			}
			return message;
		}

		StringJoiner censoredMessage = new StringJoiner(" ");
		for (String word : words) {
			String lowerCaseWord = word.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
			if (badWords.contains(lowerCaseWord) && !goodWords.contains(lowerCaseWord)) {
				censoredMessage.add(handleProfanity(word, lowerCaseWord));
			} else {
				censoredMessage.add(word);
			}
		}
		return censoredMessage.toString();
	}

	protected abstract String handleProfanity(String originalWord, String profaneWord);
}