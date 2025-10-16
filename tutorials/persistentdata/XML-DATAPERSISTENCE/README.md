# Java XML Data Persistence: A Project-Based Lesson

This project is a simple command-line Contact Book application in Java that
 demonstrates data persistence using XML. It's designed to teach the basics of
 using XML as a data store in a Java application.

## Concepts Covered

*   **Data Persistence:** The ability of an application to store data in a
    non-volatile storage, so it can be retrieved later.
*   **XML (eXtensible Markup Language):** A markup language that defines a set of
    rules for encoding documents in a format that is both human-readable and
    machine-readable.
*   **DOM (Document Object Model) Parser:** A programming interface for HTML and XML
    documents. It represents the page so that programs can change the document
    structure, style, and content. The DOM represents the document as nodes and
    objects. That way, programming languages can connect to the page.

## Project Structure

*   `pom.xml`: The Project Object Model (POM) file for Maven. It defines the
    project's dependencies, build process, and other configuration.
*   `src/main/java/com/example/contactbook/Contact.java`: The data model for a
    contact.
*   `src/main/java/com/example/contactbook/ContactDOMParser.java`: This class
    contains the logic for reading from and writing to the `contacts.xml` file
    using the DOM parser.
*   `src/main/java/com/example/contactbook/Main.java`: The main class of the
    application. It provides a command-line interface for the user to interact
    with the contact book.
*   `contacts.xml`: The XML file where the contact data is stored.

## How to Compile and Run

This project uses Maven. You can compile and run it using the following commands:

1.  **Compile the project:**

    ```bash
    mvn compile
    ```

2.  **Run the application:**

    ```bash
    mvn exec:java -Dexec.mainClass="com.example.contactbook.Main"
    ```

## The Lesson

### 1. The Data Model (`Contact.java`)

We start by defining a simple `Contact` class. This is a Plain Old Java Object
 (POJO) that holds the data for a single contact. It has fields for `id`, `name`,
 `email`, and `phone`.

### 2. The XML Structure (`contacts.xml`)

Our data is stored in an XML file. The structure is simple:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<contacts>
    <contact id="1">
        <name>John Doe</name>
        <email>john.doe@example.com</email>
        <phone>123-456-7890</phone>
    </contact>
</contacts>
```

The root element is `<contacts>`, and each contact is represented by a `<contact>`
 element with an `id` attribute.

### 3. Parsing and Manipulating XML (`ContactDOMParser.java`)

This is the core of the lesson. We use the DOM parser to interact with the XML
 file.

*   **Reading Data (`listContacts`):**
    1.  We use `DocumentBuilderFactory` and `DocumentBuilder` to parse the XML file
        into a `Document` object.
    2.  We get a `NodeList` of all `<contact>` elements.
    3.  We iterate through the `NodeList`, and for each `<contact>` element, we
        extract the `id`, `name`, `email`, and `phone` and create a new `Contact`
        object.

*   **Writing Data (`addContact`):**
    1.  We parse the existing XML file or create a new `Document` if the file
        doesn't exist.
    2.  We create a new `<contact>` element and its child elements (`<name>`,
        `<email>`, `<phone>`).
    3.  We append the new `<contact>` element to the root element.
    4.  We use a `Transformer` to write the updated `Document` back to the
        `contacts.xml` file.

### 4. The User Interface (`Main.java`)

The `Main` class provides a simple command-line menu to interact with the
 application. It uses a `Scanner` to get user input and calls the appropriate
 methods in `ContactDOMParser`.

## Further Exploration

This project uses the DOM parser, which loads the entire XML document into memory.
 For very large XML files, this can be inefficient. You can explore other XML
 parsing techniques in Java:

*   **SAX (Simple API for XML):** An event-based parser that reads the XML
    document sequentially. It's more memory-efficient than DOM.
*   **JAXB (Java Architecture for XML Binding):** Allows you to bind Java objects
    to XML documents. It can simplify the process of converting XML to Java
    objects and vice versa.