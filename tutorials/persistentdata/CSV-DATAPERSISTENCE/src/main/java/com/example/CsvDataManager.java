package com.example;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Manages reading and writing data to a CSV file.
 * This class uses the OpenCSV library to handle the conversion between Java objects and CSV records.
 */
public class CsvDataManager {

    private final String filePath;

    /**
     * Constructs a new CsvDataManager with the specified file path.
     *
     * @param filePath the path to the CSV file
     */
    public CsvDataManager(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Writes a list of students to the CSV file.
     *
     * @param students the list of students to write
     */
    public void writeStudents(List<Student> students) {
        try (Writer writer = new FileWriter(filePath)) {
            // Create a StatefulBeanToCsv object to write the beans to the CSV file.
            StatefulBeanToCsv<Student> beanToCsv = new StatefulBeanToCsvBuilder<Student>(writer).build();
            // Write the list of students to the CSV file.
            beanToCsv.write(students);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a list of students from the CSV file.
     *
     * @return the list of students read from the CSV file
     */
    public List<Student> readStudents() {
        try (FileReader reader = new FileReader(filePath)) {
            // Create a CsvToBean object to read the CSV file and map the records to Student objects.
            return new CsvToBeanBuilder<Student>(reader)
                    .withType(Student.class)
                    .build()
                    .parse();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
