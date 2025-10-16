package com.example.comp2100miniproject.persistentdata.formatted;

import com.example.comp2100miniproject.persistentdata.PersistentDataException;

import java.io.IOException;
import java.io.Writer;

public class CSVWriter implements FormattedWriter<String[]> {
    private final CSVFormat format;
    private final Writer writer;
    private boolean isFirstRecord = true;

    // Error message for when the data array has the wrong number of columns.
    private static final String COLUMN_COUNT_MISMATCH_MESSAGE = "Invalid data format: Expected %d columns, but got %d.";

    public CSVWriter(CSVFormat format, Writer writer) {
        this.format = format;
        this.writer = writer;
    }

    /**
     * In a standard CSV file, there is no dedicated header section.
     * Headers are typically written as the first data row using putNext().
     * Therefore, this method does nothing.
     */
    @Override
    public void putHeader() {
        // No operation is needed for CSV headers. The calling code should
        // use putNext() to write a header row if desired.
    }

    /**
     * Writes a single row of data to the writer, formatted according to the CSV specification.
     *
     * @param data An array of strings representing the fields for one row.
     * @throws CSVIOException if the data array's length does not match the expected column count,
     *                        or if an I/O error occurs.
     */
    @Override
    public void putNext(String[] data) {
        if (data.length != format.COLUMN_COUNT) {
            throw new CSVIOException(
                    COLUMN_COUNT_MISMATCH_MESSAGE.formatted(format.COLUMN_COUNT, data.length)
            );
        }

        try {
            // Write a line separator before the record, but only if it's not the first one.
            if (!isFirstRecord) {
                writer.write(format.LINE_SEPARATOR);
            }

            StringBuilder lineBuilder = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                String field = data[i];
                if (field == null) {
                    field = ""; // Treat nulls as empty strings.
                }

                boolean needsEscaping = field.contains(String.valueOf(format.FIELD_SEPARATOR)) ||
                        field.contains(String.valueOf(format.LINE_SEPARATOR)) ||
                        field.contains(String.valueOf(format.ESCAPE_MARKER));

                if (needsEscaping) {
                    lineBuilder.append(format.ESCAPE_MARKER);
                    // A more robust way to double the escape markers.
                    String escapedField = field.replace(
                            String.valueOf(format.ESCAPE_MARKER),
                            String.valueOf(format.ESCAPE_MARKER) + String.valueOf(format.ESCAPE_MARKER)
                    );
                    lineBuilder.append(escapedField);
                    lineBuilder.append(format.ESCAPE_MARKER);
                } else {
                    lineBuilder.append(field);
                }

                if (i < data.length - 1) {
                    lineBuilder.append(format.FIELD_SEPARATOR);
                }
            }

            writer.write(lineBuilder.toString());
            isFirstRecord = false; // Subsequent records are not the first.

        } catch (IOException e) {
            throw new CSVIOException("Failed to write to the underlying writer: " + e.getMessage());
        }
    }


    /**
     * In a standard CSV file, there is no dedicated footer section.
     * Therefore, this method does nothing.
     */
    @Override
    public void putFooter() {
        // No operation is needed for CSV footers.
    }

    /**
     * Custom exception for CSV writing errors.
     */
    public static class CSVIOException extends PersistentDataException {
        public CSVIOException(String message) {
            super(message);
        }
    }
}