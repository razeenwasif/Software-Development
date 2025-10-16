package com.example.contactapp.dao;

import com.example.contactapp.model.Contact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a concrete implementation of the ContactDao interface.
 * It uses a JSON file as the data source and Gson for serialization and deserialization.
 */
public class ContactDaoImpl implements ContactDao {
    private final String filePath;
    private final Gson gson;

    /**
     * Constructs a new ContactDaoImpl object.
     *
     * @param filePath The path to the JSON file that will be used as the data source.
     */
    public ContactDaoImpl(String filePath) {
        this.filePath = filePath;
        this.gson = new Gson();
    }

    /**
     * Retrieves a list of all contacts from the JSON file.
     *
     * @return A list of all contacts.
     */
    @Override
    public List<Contact> getAllContacts() {
        try (FileReader reader = new FileReader(filePath)) {
            // Define the type of the list we want to deserialize into
            Type listType = new TypeToken<ArrayList<Contact>>() {}.getType();
            // Deserialize the JSON from the file into a list of contacts
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            // If the file doesn't exist or there's an error reading it, return an empty list
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves a single contact from the JSON file based on their email address.
     *
     * @param email The email address of the contact to retrieve.
     * @return The contact with the specified email, or null if not found.
     */
    @Override
    public Contact getContact(String email) {
        return getAllContacts().stream()
                .filter(contact -> contact.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    /**
     * Adds a new contact to the JSON file.
     *
     * @param contact The contact to add.
     */
    @Override
    public void addContact(Contact contact) {
        List<Contact> contacts = getAllContacts();
        contacts.add(contact);
        saveContacts(contacts);
    }

    /**
     * Updates an existing contact in the JSON file.
     *
     * @param updatedContact The contact to update.
     */
    @Override
    public void updateContact(Contact updatedContact) {
        List<Contact> contacts = getAllContacts();
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getEmail().equals(updatedContact.getEmail())) {
                contacts.set(i, updatedContact);
                break;
            }
        }
        saveContacts(contacts);
    }

    /**
     * Deletes a contact from the JSON file based on their email address.
     *
     * @param email The email address of the contact to delete.
     */
    @Override
    public void deleteContact(String email) {
        List<Contact> contacts = getAllContacts();
        contacts.removeIf(contact -> contact.getEmail().equals(email));
        saveContacts(contacts);
    }

    /**
     * Saves the list of contacts to the JSON file.
     *
     * @param contacts The list of contacts to save.
     */
    private void saveContacts(List<Contact> contacts) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Serialize the list of contacts into JSON and write it to the file
            gson.toJson(contacts, writer);
        } catch (IOException e) {
            // Print the stack trace if there's an error writing to the file
            e.printStackTrace();
        }
    }
}