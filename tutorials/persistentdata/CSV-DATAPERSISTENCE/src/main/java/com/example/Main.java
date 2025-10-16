package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class to demonstrate CSV data persistence.
 */
public class Main {

    public static void main(String[] args) {
        // Create a CsvDataManager to handle CSV operations.
        CsvDataManager dataManager = new CsvDataManager("students.csv");

        // Create a list of students.
        List<Student> students = new ArrayList<>();
        students.add(new Student(1, "John Doe", 95));
        students.add(new Student(2, "Jane Smith", 88));
        students.add(new Student(3, "Peter Jones", 76));

        // Write the list of students to the CSV file.
        System.out.println("Writing students to students.csv...");
        dataManager.writeStudents(students);
        System.out.println("Done.");

        // Read the students back from the CSV file.
        System.out.println("\nReading students from students.csv...");
        List<Student> loadedStudents = dataManager.readStudents();

        // Print the loaded students to the console.
        if (loadedStudents != null) {
            for (Student student : loadedStudents) {
                System.out.println(student);
            }
        }
        System.out.println("Done.");
    }
}
