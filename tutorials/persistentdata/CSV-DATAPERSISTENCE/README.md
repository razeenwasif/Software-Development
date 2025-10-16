# CSV Data Persistence in Java

This lesson teaches you how to persist data in CSV (Comma-Separated Values) format using Java. We will use the OpenCSV library to simplify the process of reading and writing CSV files.

## Lesson Plan

1.  **Introduction to Data Persistence:**
    *   What is data persistence?
    *   Why is it important?
    *   Different ways to persist data (files, databases, etc.).

2.  **CSV Format:**
    *   What is a CSV file?
    *   Structure of a CSV file (header and data rows).
    *   Advantages and disadvantages of using CSV for data persistence.

3.  **Setting up the Project:**
    *   Creating a Maven project.
    *   Adding the OpenCSV dependency to the `pom.xml` file.

4.  **Creating the Data Model:**
    *   Creating a simple Java class (`Student.java`) to represent our data.

5.  **Writing Data to a CSV File:**
    *   Creating a `CsvDataManager.java` class to handle CSV operations.
    *   Implementing a method to write a list of objects to a CSV file.
    *   Using `StatefulBeanToCsv` from OpenCSV to map Java beans to CSV records.

6.  **Reading Data from a CSV File:**
    *   Implementing a method to read data from a CSV file.
    *   Using `CsvToBean` from OpenCSV to map CSV records to Java beans.

7.  **Putting it all Together:**
    *   Creating a `Main.java` class to demonstrate the usage of the `CsvDataManager`.
    *   Creating a list of `Student` objects.
    *   Writing the list to a CSV file.
    *   Reading the data back from the CSV file.
    *   Printing the data to the console.

## Running the Code

1.  Make sure you have Maven installed.
2.  Compile the project: `mvn compile`
3.  Run the application: `mvn exec:java -Dexec.mainClass="com.example.Main"`
