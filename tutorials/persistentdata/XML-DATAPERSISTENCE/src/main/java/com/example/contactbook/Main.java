package com.example.contactbook;

import java.util.List;
import java.util.Scanner;

/**
 * The main class of the Contact Book application.
 * This class provides a command-line interface for the user to interact with the contact book.
 */
public class Main {

    private static ContactDOMParser domParser = new ContactDOMParser();
    private static Scanner scanner = new Scanner(System.in);

    /**
     * The main method of the application.
     * It displays a menu to the user and performs actions based on the user's choice.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        while (true) {
            System.out.println("\nContact Book Menu:");
            System.out.println("1. Add Contact");
            System.out.println("2. List Contacts");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addContact();
                    break;
                case 2:
                    listContacts();
                    break;
                case 3:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Prompts the user for contact information and adds a new contact to the contact book.
     */
    private static void addContact() {
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();

        // Create a new Contact object and add it to the XML file.
        Contact contact = new Contact(id, name, email, phone);
        domParser.addContact(contact);
        System.out.println("Contact added successfully.");
    }

    /**
     * Lists all the contacts in the contact book.
     */
    private static void listContacts() {
        List<Contact> contacts = domParser.listContacts();
        if (contacts.isEmpty()) {
            System.out.println("No contacts found.");
        } else {
            System.out.println("\n--- Contacts ---");
            for (Contact contact : contacts) {
                System.out.println(contact);
            }
            System.out.println("----------------");
        }
    }
}