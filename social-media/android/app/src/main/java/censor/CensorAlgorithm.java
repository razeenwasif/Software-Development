package censor;

import java.util.Set;

/**
 * A complex, specialized censor that handles "leet speak" and restores "good words" that may contain profanity.
 */
class CensorAlgorithm extends AbstractCensor {

  public CensorAlgorithm(Set<String> badWords, Set<String> goodWords) {
    super(badWords, goodWords);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String censorMessage(String message) {
    char[] result = message.toCharArray();
    LeetSpeakUtil.LeetSpeakResult leetResult = LeetSpeakUtil.decodeLeetSpeak(message);

    // Step 1: Aggressively censor all potential bad words.
    censorLeetResult(result, leetResult, this.badWords);

    // Step 2: Restore any good words that may have been censored, but ONLY if they are not also bad words.
    for (int i = 0; i < message.length(); i++) {
      for (String goodWord : goodWords) {
        // This is the critical check:
        if (this.badWords.contains(goodWord)) {
            continue;
        }

        if (message.toLowerCase().startsWith(goodWord, i)) {
          for (int l = 0; l < goodWord.length(); l++) {
            result[i + l] = message.charAt(i + l);
          }
        }
      }
    }

    return new String(result);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String handleProfanity(String originalWord, String profaneWord) {
      // Not used in this implementation.
      return originalWord;
  }
}