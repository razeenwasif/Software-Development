# Lesson: Mastering Gson and the DAO Pattern in Java

This guide will walk you through building a simple Contact Management System in Java.
Through this project, you'll gain a practical understanding of JSON, the Gson
library, and the Data Access Object (DAO) pattern.

## Core Concepts

### 1. JSON (JavaScript Object Notation)

JSON is a lightweight and human-readable format for data interchange. It's widely
used in web applications and APIs to transmit data between a server and a client.

**Key Characteristics:**

*   **Human-Readable:** JSON is easy for humans to read and write.
*   **Machine-Parsable:** It's also easy for machines to parse and generate.
*   **Language-Independent:** While it originates from JavaScript, JSON is a
    language-independent data format.

**Example:**

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "123-456-7890"
}
```

### 2. Gson

Gson is a powerful Java library developed by Google that simplifies the process of
working with JSON. It allows you to convert Java objects into their JSON
representation (serialization) and vice versa (deserialization).

**Key Features:**

*   **Ease of Use:** Gson provides a simple `toJson()` and `fromJson()` methods for
    serialization and deserialization.
*   **Handling Complex Objects:** It can handle complex data structures, including
    nested objects and collections.
*   **Customization:** Gson allows for custom representations of objects.

### 3. DAO (Data Access Object) Pattern

The DAO pattern is a structural design pattern that separates the data persistence
logic of an application from its business logic. It provides an abstract interface
to a database or other persistence mechanism.

**Benefits of the DAO Pattern:**

*   **Separation of Concerns:** It decouples the business logic from the data
    access logic, making the code cleaner and easier to maintain.
*   **Flexibility:** You can easily switch the underlying data source (e.g., from a
    JSON file to a database) without changing the business logic.
*   **Centralized Data Access:** It centralizes data access code in one place,
    making it easier to manage and debug.

## Project Code with Explanations

Now, let's dive into the code. I've added Javadocs and comments to each file to
help you understand its purpose and functionality.

### `src/main/java/com/example/contactapp/model/Contact.java`

This is our model class. It's a simple POJO (Plain Old Java Object) that
represents a contact. Gson will use this class to serialize and deserialize
contact data.

### `src/main/java/com/example/contactapp/dao/ContactDao.java`

This is the DAO interface. It defines the contract for our data access
operations. By programming to this interface, we decouple our business logic from
the specific implementation of data access.

### `src/main/java/com/example/contactapp/dao/ContactDaoImpl.java`

This is the concrete implementation of our DAO interface. It uses a JSON file as
the data store and the Gson library to handle the serialization and
deserialization of contacts.

### `src/main/java/com/example/contactapp/Main.java`

This is the main entry point of our application. It contains the command-line
interface (CLI) that allows users to interact with the contact management system.

### `data/contacts.json`

This file is our database. It's a simple JSON file that stores an array of
contact objects.

## How to Compile and Run

1.  **Compile the source code:**

    Open a terminal and navigate to the root directory of the project. Then,
    execute the following command to compile the source code:

    ```bash
    javac -cp lib/gson-2.10.1.jar \
    src/main/java/com/example/contactapp/model/Contact.java \
    src/main/java/com/example/contactapp/dao/ContactDao.java \
    src/main/java/com/example/contactapp/dao/ContactDaoImpl.java \
    src/main/java/com/example/contactapp/Main.java -d out
    ```

2.  **Run the application:**

    Once the code is compiled, you can run the application with the following
    command:

    ```bash
    java -cp out:lib/gson-2.10.1.jar com.example.contactapp.Main
    ```

## Next Steps

Now that you have a basic understanding of Gson and the DAO pattern, you can try
to extend the project with the following features:

*   Add validation for user input.
*   Implement a more robust error-handling mechanism.
*   Add a search functionality to find contacts by name.
*   Use a different data source, such as a relational database (e.g., SQLite,
    H2) or a NoSQL database (e.g., MongoDB).

I hope this lesson has been helpful! Let me know if you have any questions.