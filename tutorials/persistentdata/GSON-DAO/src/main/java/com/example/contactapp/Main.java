package com.example.contactapp;

import com.example.contactapp.dao.ContactDao;
import com.example.contactapp.dao.ContactDaoImpl;
import com.example.contactapp.model.Contact;

import java.util.List;
import java.util.Scanner;

/**
 * This is the main class for the Contact Management System.
 * It provides a command-line interface (CLI) for users to interact with the application.
 */
public class Main {
    // The path to the JSON file that will be used as the data source
    private static final String FILE_PATH = "data/contacts.json";
    // An instance of the ContactDao to interact with the data source
    private static final ContactDao contactDao = new ContactDaoImpl(FILE_PATH);
    // A Scanner to read user input from the console
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * The main method that runs the application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Start an infinite loop to keep the application running until the user decides to exit
        while (true) {
            // Print the main menu
            System.out.println("\nContact Management System");
            System.out.println("1. List all contacts");
            System.out.println("2. Add a new contact");
            System.out.println("3. View a single contact");
            System.out.println("4. Update an existing contact");
            System.out.println("5. Delete a contact");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            // Read the user's choice
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            // Use a switch statement to perform the appropriate action based on the user's choice
            switch (choice) {
                case 1:
                    listAllContacts();
                    break;
                case 2:
                    addContact();
                    break;
                case 3:
                    viewContact();
                    break;
                case 4:
                    updateContact();
                    break;
                case 5:
                    deleteContact();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    return; // Exit the application
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Lists all the contacts in the data source.
     */
    private static void listAllContacts() {
        List<Contact> contacts = contactDao.getAllContacts();
        if (contacts.isEmpty()) {
            System.out.println("No contacts found.");
        } else {
            contacts.forEach(System.out::println);
        }
    }

    /**
     * Prompts the user for contact details and adds a new contact to the data source.
     */
    private static void addContact() {
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();

        Contact contact = new Contact(firstName, lastName, email, phoneNumber);
        contactDao.addContact(contact);
        System.out.println("Contact added successfully.");
    }

    /**
     * Prompts the user for an email and displays the details of the corresponding contact.
     */
    private static void viewContact() {
        System.out.print("Enter email of the contact to view: ");
        String email = scanner.nextLine();
        Contact contact = contactDao.getContact(email);
        if (contact != null) {
            System.out.println(contact);
        } else {
            System.out.println("Contact not found.");
        }
    }

    /**
     * Prompts the user for an email and new details, then updates the corresponding contact.
     */
    private static void updateContact() {
        System.out.print("Enter email of the contact to update: ");
        String email = scanner.nextLine();
        Contact existingContact = contactDao.getContact(email);

        if (existingContact != null) {
            System.out.print("Enter new first name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter new last name: ");
            String lastName = scanner.nextLine();
            System.out.print("Enter new phone number: ");
            String phoneNumber = scanner.nextLine();

            Contact updatedContact = new Contact(firstName, lastName, email, phoneNumber);
            contactDao.updateContact(updatedContact);
            System.out.println("Contact updated successfully.");
        } else {
            System.out.println("Contact not found.");
        }
    }

    /**
     * Prompts the user for an email and deletes the corresponding contact.
     */
    private static void deleteContact() {
        System.out.print("Enter email of the contact to delete: ");
        String email = scanner.nextLine();
        contactDao.deleteContact(email);
        System.out.println("Contact deleted successfully.");
    }
}