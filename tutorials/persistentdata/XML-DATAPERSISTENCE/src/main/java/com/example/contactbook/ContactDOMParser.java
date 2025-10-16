package com.example.contactbook;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for parsing and manipulating the contacts.xml file using the DOM parser.
 * It provides methods to list all contacts and add a new contact.
 */
public class ContactDOMParser {

    private static final String FILE_PATH = "contacts.xml";

    /**
     * Reads the contacts.xml file and returns a list of Contact objects.
     *
     * @return A list of all contacts in the XML file.
     */
    public List<Contact> listContacts() {
        List<Contact> contacts = new ArrayList<>();
        File xmlFile = new File(FILE_PATH);

        // If the file doesn't exist, return an empty list.
        if (!xmlFile.exists()) {
            return contacts;
        }

        try {
            // Boilerplate code for setting up the DOM parser.
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Get a list of all <contact> nodes.
            NodeList nList = doc.getElementsByTagName("contact");

            // Iterate through the list of nodes.
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    // Extract the contact information from the elements.
                    int id = Integer.parseInt(eElement.getAttribute("id"));
                    String name = eElement.getElementsByTagName("name").item(0).getTextContent();
                    String email = eElement.getElementsByTagName("email").item(0).getTextContent();
                    String phone = eElement.getElementsByTagName("phone").item(0).getTextContent();

                    // Create a new Contact object and add it to the list.
                    contacts.add(new Contact(id, name, email, phone));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacts;
    }

    /**
     * Adds a new contact to the contacts.xml file.
     *
     * @param contact The contact to add.
     */
    public void addContact(Contact contact) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            Element rootElement;

            File xmlFile = new File(FILE_PATH);
            if (xmlFile.exists()) {
                // If the file exists, parse it.
                doc = dBuilder.parse(xmlFile);
                rootElement = doc.getDocumentElement();
            } else {
                // If the file doesn't exist, create a new document and root element.
                doc = dBuilder.newDocument();
                rootElement = doc.createElement("contacts");
                doc.appendChild(rootElement);
            }

            // Create the <contact> element.
            Element contactElement = doc.createElement("contact");
            contactElement.setAttribute("id", String.valueOf(contact.getId()));

            // Create the <name> element.
            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode(contact.getName()));
            contactElement.appendChild(name);

            // Create the <email> element.
            Element email = doc.createElement("email");
            email.appendChild(doc.createTextNode(contact.getEmail()));
            contactElement.appendChild(email);

            // Create the <phone> element.
            Element phone = doc.createElement("phone");
            phone.appendChild(doc.createTextNode(contact.getPhone()));
            contactElement.appendChild(phone);

            // Add the new contact to the root element.
            rootElement.appendChild(contactElement);

            // Write the updated document back to the file.
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(FILE_PATH));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}