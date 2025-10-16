package censor;

import java.util.Set;

/**
 * A censor that replaces profanity with asterisks in a case-sensitive manner.
 */
class CensorProfanityCaseSensitive extends AbstractCensor {

  public CensorProfanityCaseSensitive(Set<String> badWords, Set<String> goodWords) {
    super(badWords, goodWords);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String censorMessage(String message) {
    char[] result = message.toCharArray();

    for (int i = 0; i < message.length(); i++) {
      for (String badWord : badWords) {
        if (message.startsWith(badWord, i)) {
          for (int j = 1; j < badWord.length(); j++) {
            result[i + j] = '*';
          }
        }
      }
    }

    return new String(result);
  }

  @Override
  protected String handleProfanity(String originalWord, String profaneWord) {
    return originalWord;
  }
}
