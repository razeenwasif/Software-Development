package censor;

/**
 * Defines the contract for a censoring strategy.
 * All censor implementations must provide a method to process and censor a string message.
 */
public interface ICensor {
	/**
	 * Censors a given message according to a specific implementation's rules.
	 * @param message the message to censor
	 * @return the censored message
	 */
	public String censorMessage(String message);
}
