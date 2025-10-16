package com.example.contactbook;

/**
 * Represents a single contact in the contact book.
 * This class is a simple Plain Old Java Object (POJO) that holds the data for a contact.
 */
public class Contact {
    private int id;
    private String name;
    private String email;
    private String phone;

    /**
     * Constructs a new Contact object.
     *
     * @param id    The ID of the contact.
     * @param name  The name of the contact.
     * @param email The email address of the contact.
     * @param phone The phone number of the contact.
     */
    public Contact(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    /**
     * Gets the ID of the contact.
     *
     * @return The ID of the contact.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the contact.
     *
     * @param id The new ID of the contact.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the contact.
     *
     * @return The name of the contact.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the contact.
     *
     * @param name The new name of the contact.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email address of the contact.
     *
     * @return The email address of the contact.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the contact.
     *
     * @param email The new email address of the contact.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number of the contact.
     *
     * @return The phone number of the contact.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the contact.
     *
     * @param phone The new phone number of the contact.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns a string representation of the contact.
     *
     * @return A string representation of the contact.
     */
    @Override
    public String toString() {
        return "Contact{" + 
                "id=" + id +
                ", name='" + name + "'" + 
                ", email='" + email + "'" + 
                ", phone='" + phone + "'" + 
                '}';
    }
}