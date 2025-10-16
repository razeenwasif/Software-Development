package com.example.comp2100miniproject.persistentdata.formatted;

import com.example.comp2100miniproject.persistentdata.PersistentDataException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader implements FormattedReader<String[]> {
    private final CSVFormat format;
    private final Reader reader;
    private String[] nextRecord;

    private boolean eof = false;

    private enum State {
        IN_FIELD,
        IN_ESCAPED_FIELD,
        AFTER_ESCAPE
    }

    public CSVReader(CSVFormat format, Reader reader) {
        this.format = format;
        this.reader = reader;
        // Prime the reader by loading the first record.
        this.nextRecord = readNextRecord();
    }

    public boolean hasNext() {
        return this.nextRecord != null;
    }

    // Format strings for error cases.
    private static final String LINE_TOO_SHORT_MESSAGE = "Line was too short: expected %s fields but found %s";
    private static final String LINE_TOO_LONG_MESSAGE = "Line was too long: expected %s fields";
    private static final String IMPROPER_ESCAPE_MESSAGE = "EOF reached unexpectedly while in an escaped field";
    private static final String REACHED_EOF_MESSAGE = "Already reached end of file while reading";
    private static final String UNEXPECTED_CHAR_AFTER_ESCAPE = "Unexpected character after a closing escape marker";
    private static final String UNEXPECTED_ESCAPE_IN_FIELD = "Unexpected escape marker in an unquoted field";


    public String[] getNext() {
        if (!hasNext()) {
            throw new CSVIOException(REACHED_EOF_MESSAGE);
        }
        String[] currentRecord = this.nextRecord;
        this.nextRecord = readNextRecord();
        return currentRecord;
    }

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
                charCode = this.reader.read();
            } catch (IOException e) {
                throw new CSVIOException("Failed to read from the underlying reader: " + e.getMessage());
            }

            // --- End Of File Handling ---
            if (charCode == -1) {
                this.eof = true;
                if (state == State.IN_ESCAPED_FIELD) {
                    throw new CSVIOException(IMPROPER_ESCAPE_MESSAGE);
                }
                if (fields.isEmpty() && currentField.length() == 0) {
                    return null; // File is empty or ends with a clean newline.
                }
                // Process the very last record if the file doesn't end with a newline.
                fields.add(currentField.toString());
                validateRecord(fields);
                return fields.toArray(new String[0]);
            }

            char c = (char) charCode;

            switch (state) {
                case IN_FIELD:
                    if (c == format.FIELD_SEPARATOR) {
                        fields.add(currentField.toString());
                        currentField.setLength(0);
                    } else if (c == format.LINE_SEPARATOR) {
                        fields.add(currentField.toString());
                        validateRecord(fields);
                        return fields.toArray(new String[0]);
                    } else if (c == format.ESCAPE_MARKER) {
                        // A quote is only allowed at the very beginning of an unquoted field.
                        if (currentField.length() == 0) {
                            state = State.IN_ESCAPED_FIELD;
                        } else {
                            throw new CSVIOException(UNEXPECTED_ESCAPE_IN_FIELD);
                        }
                    } else {
                        currentField.append(c);
                    }
                    break;

                case IN_ESCAPED_FIELD:
                    if (c == format.ESCAPE_MARKER) {
                        state = State.AFTER_ESCAPE;
                    } else {
                        currentField.append(c);
                    }
                    break;

                case AFTER_ESCAPE:
                    if (c == format.ESCAPE_MARKER) { // Handles a doubled quote ("")
                        currentField.append(c);
                        state = State.IN_ESCAPED_FIELD;
                    } else if (c == format.FIELD_SEPARATOR) {
                        fields.add(currentField.toString());
                        currentField.setLength(0);
                        state = State.IN_FIELD;
                    } else if (c == format.LINE_SEPARATOR) {
                        fields.add(currentField.toString());
                        validateRecord(fields);
                        return fields.toArray(new String[0]);
                    } else {
                        // CRITICAL FIX: After a closing quote, any character that is not a
                        // delimiter or another quote is a syntax violation. This is the case
                        // that catches the malformed "Da"ta" record.
                        throw new CSVIOException(UNEXPECTED_CHAR_AFTER_ESCAPE);
                    }
                    break;
            }
        }
    }

    private void validateRecord(List<String> fields) {
        if (fields.size() > format.COLUMN_COUNT) {
            throw new CSVIOException(LINE_TOO_LONG_MESSAGE.formatted(format.COLUMN_COUNT));
        }
        if (fields.size() < format.COLUMN_COUNT) {
            throw new CSVIOException(LINE_TOO_SHORT_MESSAGE.formatted(format.COLUMN_COUNT, fields.size()));
        }
    }

    public static class CSVIOException extends PersistentDataException {
        public CSVIOException(String message) {
            super(message);
        }
    }
}