package com.example.contactapp.model;

/**
 * Represents a contact with a first name, last name, email, and phone number.
 * This class is a simple Plain Old Java Object (POJO) that serves as the model for our application.
 */
public class Contact {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    /**
     * Constructs a new Contact object.
     *
     * @param firstName   The first name of the contact.
     * @param lastName    The last name of the contact.
     * @param email       The email address of the contact (used as a unique identifier).
     * @param phoneNumber The phone number of the contact.
     */
    public Contact(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the first name of the contact.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the contact.
     *
     * @param firstName The new first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the contact.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the contact.
     *
     * @param lastName The new last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the email address of the contact.
     *
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the contact.
     *
     * @param email The new email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number of the contact.
     *
     * @return The phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the contact.
     *
     * @param phoneNumber The new phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns a string representation of the contact.
     *
     * @return A string containing the contact's details.
     */
    @Override
    public String toString() {
        return "Contact{" +
                "firstName='" + firstName + "'" +
                ", lastName='" + lastName + "'" +
                ", email='" + email + "'" +
                ", phoneNumber='" + phoneNumber + "'" +
                '}';
    }
}