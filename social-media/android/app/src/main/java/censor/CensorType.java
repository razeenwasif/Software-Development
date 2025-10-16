package censor;

/**
 * Represents the type of censoring strategy to be applied. Used by the {@link CensorFacade} to
 * select the correct implementation.
 */
public enum CensorType {
  /** A custom censoring algorithm that handles leet speak. */
  ALGORITHM,
  /** Replaces profane words with asterisks. */
  REPLACE_STARS,
  /** Blocks the entire message if profanity is found. */
  BLOCK,
  /** A custom censoring algorithm for the word "bernardo". */
  BERNARDO
}
