package com.example.contactapp.dao;

import com.example.contactapp.model.Contact;

import java.util.List;

/**
 * This interface defines the contract for data access operations related to contacts.
 * It follows the Data Access Object (DAO) pattern, which separates the data persistence logic
 * from the business logic of the application.
 */
public interface ContactDao {

    /**
     * Retrieves a list of all contacts from the data source.
     *
     * @return A list of all contacts.
     */
    List<Contact> getAllContacts();

    /**
     * Retrieves a single contact from the data source based on their email address.
     *
     * @param email The email address of the contact to retrieve.
     * @return The contact with the specified email, or null if not found.
     */
    Contact getContact(String email);

    /**
     * Adds a new contact to the data source.
     *
     * @param contact The contact to add.
     */
    void addContact(Contact contact);

    /**
     * Updates an existing contact in the data source.
     *
     * @param contact The contact to update.
     */
    void updateContact(Contact contact);

    /**
     * Deletes a contact from the data source based on their email address.
     *
     * @param email The email address of the contact to delete.
     */
    void deleteContact(String email);
}