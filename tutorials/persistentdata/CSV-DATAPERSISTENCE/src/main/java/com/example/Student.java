package com.example;

/**
 * Represents a student with an ID, name, and grade.
 * This class is a simple Plain Old Java Object (POJO) that serves as our data model.
 */
public class Student {

    private int id;
    private String name;
    private int grade;

    /**
     * Default constructor.
     */
    public Student() {
    }

    /**
     * Constructs a new Student with the specified ID, name, and grade.
     *
     * @param id    the student's ID
     * @param name  the student's name
     * @param grade the student's grade
     */
    public Student(int id, String name, int grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    /**
     * Returns the student's ID.
     *
     * @return the student's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the student's ID.
     *
     * @param id the student's ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the student's name.
     *
     * @return the student's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the student's name.
     *
     * @param name the student's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the student's grade.
     *
     * @return the student's grade
     */
    public int getGrade() {
        return grade;
    }

    /**
     * Sets the student's grade.
     *
     * @param grade the student's grade
     */
    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", grade=" + grade +
                '}';
    }
}
