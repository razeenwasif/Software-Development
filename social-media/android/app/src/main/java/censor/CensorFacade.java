package censor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides a simplified interface to the complex censoring subsystem, implementing the <b>Facade
 * pattern</b>.
 */
public class CensorFacade {

  private final Set<String> badWords;
  private final Set<String> goodWords;

  /** Constructor for the facade. Initializes word lists from the hardcoded data class. */
  public CensorFacade() {
    this.badWords = new HashSet<>(Arrays.asList(WordListData.BAD_WORDS));
    this.goodWords = new HashSet<>(Arrays.asList(WordListData.GOOD_WORDS));
  }

  /**
   * Censors a message using a specified censoring strategy.
   *
   * @param message The message to be censored.
   * @param type The {@link CensorType} defining which strategy to use.
   * @return The censored message.
   */
  public String censorMessage(String message, CensorType type) {
    ICensor censor;

    switch (type) {
      case BLOCK:
        censor = new BlockProfanity(this.badWords, this.goodWords);
        break;
      case BERNARDO:
        censor = new CensorBernardo(this.badWords, this.goodWords);
        break;
      case REPLACE_STARS:
        censor = new CensorProfanityCaseSensitive(this.badWords, this.goodWords);
        break;
      case ALGORITHM:
      default:
        censor = new CensorAlgorithm(this.badWords, this.goodWords);
        break;
    }

    return censor.censorMessage(message);
  }
}
