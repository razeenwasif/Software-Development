package com.example.comp2100miniproject.src.persistentdata.formatted;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import com.example.comp2100miniproject.src.persistentdata.PersistentDataException;

/**
 * A concrete implementation of {@link FormattedReader} for the CSV format.
 *
 * <p>This class reads from a {@link Reader} and parses the data according to the rules of a {@link
 * CSVFormat} object. It uses a state machine to handle the complexities of the CSV format, such as
 * escaped fields, doubled escape characters, and malformed records.
 */
public class CSVReader implements FormattedReader<String[]> {
  private final CSVFormat format;
  private final Reader reader;

  /**
   * A lookahead buffer that holds the next record to be returned. This allows the {@link
   * #hasNext()} method to know if a subsequent call to {@link #getNext()} will succeed.
   */
  private String[] nextRecord;

  /** A flag indicating that the end of the underlying reader has been reached. */
  private boolean eof = false;

  /**
   * Represents the current parsing state of the character-by-character CSV reader. This state
   * machine is essential for correctly handling fields that are escaped (quoted) versus those that
   * are not.
   */
  private enum State {
    /** The default state when reading a standard, unquoted field. */
    IN_FIELD,
    /**
     * The state when the parser is inside a quoted field (e.g., "a, b"). In this state, field and
     * line separators are treated as regular characters.
     */
    IN_ESCAPED_FIELD,
    /**
     * A transient state after an escape marker (a quote) is found within a quoted field. This state
     * is used to determine if the quote is closing the field or if it is an escaped quote within
     * the field itself (e.g., a "" sequence).
     */
    AFTER_ESCAPE
  }

  /**
   * Constructs a CSVReader.
   *
   * <p>Upon construction, it immediately attempts to read the first record from the reader to
   * populate the {@code nextRecord} buffer. This "primes" the reader so that the first call to
   * {@link #hasNext()} is accurate.
   *
   * @param format The {@link CSVFormat} that defines the CSV dialect.
   * @param reader The underlying {@link Reader} from which to read CSV data.
   */
  public CSVReader(CSVFormat format, Reader reader) {
    this.format = format;
    this.reader = reader;
    // Prime the reader by loading the first record into the lookahead buffer.
    this.nextRecord = readNextRecord();
  }

  /**
   * Checks if there is another record available to read.
   *
   * @return {@code true} if {@code getNext()} can be called, {@code false} otherwise.
   */
  public boolean hasNext() {
    return this.nextRecord != null;
  }

  // --- Private constants for error messages ---
  private static final String LINE_TOO_SHORT_MESSAGE =
      "Line was too short: expected %s fields but found %s";
  private static final String LINE_TOO_LONG_MESSAGE = "Line was too long: expected %s fields";
  private static final String IMPROPER_ESCAPE_MESSAGE =
      "EOF reached unexpectedly while in an escaped field";
  private static final String REACHED_EOF_MESSAGE = "Already reached end of file while reading";
  private static final String UNEXPECTED_CHAR_AFTER_ESCAPE =
      "Unexpected character after a closing escape marker";
  private static final String UNEXPECTED_ESCAPE_IN_FIELD =
      "Unexpected escape marker in an unquoted field";

  /**
   * Returns the next CSV record as an array of strings.
   *
   * @return The next record.
   * @throws CSVIOException if the end of the file has already been reached.
   */
  public String[] getNext() {
    if (!hasNext()) {
      throw new CSVIOException(REACHED_EOF_MESSAGE);
    }
    // The current record is the one we buffered previously.
    String[] currentRecord = this.nextRecord;
    // Read the *next* record into the buffer for the *next* call to
    // hasNext()/getNext().
    this.nextRecord = readNextRecord();
    return currentRecord;
  }

  /**
   * The core parsing logic. Reads from the underlying reader character by character and constructs
   * the next record based on the CSV state machine.
   *
   * @return The next parsed record as a String array, or null if the end of the file is reached.
   * @throws CSVIOException if the CSV data is malformed.
   */
  private String[] readNextRecord() {
    if (this.eof) {
      return null;
    }

    List<String> fields = new ArrayList<>();
    StringBuilder currentField = new StringBuilder();
    State state = State.IN_FIELD;

    while (true) {
      int charCode;
      try {
        // Read one character from the input stream.
        charCode = this.reader.read();
      } catch (IOException e) {
        throw new CSVIOException("Failed to read from the underlying reader: " + e.getMessage());
      }

      // --- End Of File (EOF) Handling ---
      if (charCode == -1) {
        this.eof = true;
        // If EOF is reached while inside an unterminated quoted field, the CSV is
        // malformed.
        if (state == State.IN_ESCAPED_FIELD) {
          throw new CSVIOException(IMPROPER_ESCAPE_MESSAGE);
        }
        // If the file is empty or just ends with clean newlines, we are done.
        if (fields.isEmpty() && currentField.length() == 0) {
          return null;
        }
        // This handles the case where the file does not end with a newline character.
        // The last field and record are processed here.
        fields.add(currentField.toString());
        validateRecord(fields);
        return fields.toArray(new String[0]);
      }

      char c = (char) charCode;

      // --- State Machine Logic ---
      switch (state) {
        case IN_FIELD: // Currently parsing a simple, unquoted field.
          if (c == format.FIELD_SEPARATOR) {
            // End of the current field.
            fields.add(currentField.toString());
            currentField.setLength(0);
          } else if (c == format.LINE_SEPARATOR) {
            // End of the current record.
            fields.add(currentField.toString());
            validateRecord(fields);
            return fields.toArray(new String[0]);
          } else if (c == format.ESCAPE_MARKER) {
            // An escape marker is only valid at the very beginning of an unquoted field.
            if (currentField.length() == 0) {
              state = State.IN_ESCAPED_FIELD;
            } else {
              throw new CSVIOException(UNEXPECTED_ESCAPE_IN_FIELD);
            }
          } else {
            // A regular character; append it to the current field.
            currentField.append(c);
          }
          break;

        case IN_ESCAPED_FIELD: // Currently parsing inside a quoted field.
          if (c == format.ESCAPE_MARKER) {
            // This could be the end of the field, or an escaped quote. Move to
            // AFTER_ESCAPE to decide.
            state = State.AFTER_ESCAPE;
          } else {
            // Any other character, including separators, is part of the field value.
            currentField.append(c);
          }
          break;

        case AFTER_ESCAPE: // Just saw a quote inside a quoted field.
          if (c == format.ESCAPE_MARKER) { // This is a doubled quote (e.g., "").
            // Treat it as a literal quote and append it to the field.
            currentField.append(c);
            state = State.IN_ESCAPED_FIELD; // Return to the escaped field state.
          } else if (c == format.FIELD_SEPARATOR) {
            // This marks the end of the quoted field and the start of the next field.
            fields.add(currentField.toString());
            currentField.setLength(0);
            state = State.IN_FIELD;
          } else if (c == format.LINE_SEPARATOR) {
            // This marks the end of the quoted field and the end of the record.
            fields.add(currentField.toString());
            validateRecord(fields);
            return fields.toArray(new String[0]);
          } else {
            // According to CSV standards (RFC 4180), after a closing quote of a
            // field, the next character must be a delimiter. Any other character
            // indicates a malformed record (e.g., "Da"ta").
            throw new CSVIOException(UNEXPECTED_CHAR_AFTER_ESCAPE);
          }
          break;
      }
    }
  }

  /**
   * Validates that a parsed record has the correct number of columns as defined by the CSVFormat.
   *
   * @param fields The list of fields parsed for a single record.
   * @throws CSVIOException if the number of fields is not equal to the expected column count.
   */
  private void validateRecord(List<String> fields) {
    if (fields.size() > format.COLUMN_COUNT) {
      throw new CSVIOException(LINE_TOO_LONG_MESSAGE.formatted(format.COLUMN_COUNT));
    }
    if (fields.size() < format.COLUMN_COUNT) {
      throw new CSVIOException(
          LINE_TOO_SHORT_MESSAGE.formatted(format.COLUMN_COUNT, fields.size()));
    }
  }

  /** Custom exception for errors that occur during CSV reading or parsing. */
  public static class CSVIOException extends PersistentDataException {
    public CSVIOException(String message) {
      super(message);
    }
  }
}
