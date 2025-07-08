# Quantum Bookstore: A Java-Based Vending System

## Overview

This project is a simulation of an online bookstore management system, developed in Java. It demonstrates core object-oriented programming (OOP) principles, focusing on creating a system that is both robust and easily extensible. The design allows for different types of products (Paper Books, EBooks, and non-saleable Showcase Books) to be managed through a unified interface, showcasing the power of polymorphism.

This was developed as a solution for the Fawry N² Dev Slope \#10 Challenge.

## Core Features

  * **Diverse Product Types**: The system is built to handle multiple types of books:
      * **Paper Books**: Physical books with finite stock levels that are "shipped" upon purchase.
      * **EBooks**: Digital books with a specific file type that are "emailed" upon purchase.
      * **Showcase Books**: Demo books that are part of the inventory but are not for sale.
  * **Inventory Management**:
      * Add new books with details like ISBN, title, author, publication year, and price.
      * Remove and retrieve outdated books from the inventory based on a specified age threshold.
  * **Transaction Processing**:
      * A `buyBook` function that handles the logic for purchasing a book.
      * Automatically reduces stock for paper books and throws an error if not available.
      * Delegates actions to mock `ShippingService` and `MailService` based on the book type.
  * **Extensible Design**: The system is designed to be easily extended. Adding a new product type (e.g., `Audiobook`) can be done by simply creating a new class that inherits from the abstract `Book` class, without needing to modify the core `QuantumBookstore` logic.

## Design Philosophy

The primary goal was to build a system that adheres to the **Open/Closed Principle**. The `QuantumBookstore` is "open for extension" (you can add new book types) but "closed for modification" (you don't need to change its existing code to support them).

This is achieved through **polymorphism**. The `QuantumBookstore` interacts with a generic `Book` object. Each specific book class (`PaperBook`, `EBook`) provides its own implementation of the `handlePurchase` method, ensuring the correct logic is executed for each type.

## How to Run

The entire project is contained within a single file for simplicity.

1.  **Save the Code**: Save the code as `QuantumBookstoreFullTest.java`.
2.  **Compile**: Open a terminal or command prompt, navigate to the directory where you saved the file, and compile it using the Java compiler:
    ```bash
    javac QuantumBookstoreFullTest.java
    ```
3.  **Run**: Execute the compiled code to run the test suite:
    ```bash
    java QuantumBookstoreFullTest
    ```
    You will see the output of the test scenarios, including adding books, processing purchases, and removing outdated stock.

## Code Structure

  * **`QuantumBookstoreFullTest.java`**: The main entry point of the application. It contains the `main` method which acts as a test driver, simulating various operations on the bookstore.
  * **`QuantumBookstore`**: The central class that manages the inventory (`Map<String, Book>`) and orchestrates the main business logic (adding, buying, removing books).
  * **`Book` (abstract class)**: The base class for all products. It defines the common attributes and behaviors for any book in the store.
  * **`PaperBook`, `EBook`, `ShowcaseBook`**: Concrete classes that extend `Book` and provide specific implementations for their unique characteristics.
  * **`ShippingService` & `MailService` (interfaces)**: Define the contracts for external services. This decoupling means the bookstore doesn't need to know the details of *how* shipping or emailing works, only that it can be done.
  * **`MockShippingService` & `MockMailService`**: Mock implementations of the service interfaces used for testing and demonstration purposes.
  * 
![Screenshot 2025-07-09 012516](https://github.com/user-attachments/assets/14fa3299-d41b-4f66-b77e-1d154ce708aa)
![Screenshot 2025-07-09 012538](https://github.com/user-attachments/assets/73e56661-614d-4fb0-9fde-ccd34f6c96f1)
![Screenshot 2025-07-09 012614](https://github.com/user-attachments/assets/9ddcf7d5-f483-4d67-a499-29057d5bc1be)

