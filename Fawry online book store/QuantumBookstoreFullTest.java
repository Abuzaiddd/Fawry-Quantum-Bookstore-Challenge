import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class QuantumBookstoreFullTest {

    public static void main(String[] args) {
        log("=====================================================");
        log("Initializing Quantum Bookstore Test Suite");
        log("=====================================================\n");

        // Instantiate the main components
        MockShippingService shippingService = new MockShippingService();
        MockMailService mailService = new MockMailService();
        QuantumBookstore store = new QuantumBookstore(shippingService, mailService);

        // --- Run Test Scenarios ---
        testBookAddition(store);
        testBookBuying(store);
        testBookRemoval(store);

        log("\n=====================================================");
        log("Quantum Bookstore Test Suite Completed");
        log("=====================================================");
    }

    /**
     * Tests the functionality of adding different types of books to the inventory.
     */
    private static void testBookAddition(QuantumBookstore store) {
        logHeader("TEST 1: ADDING BOOKS TO INVENTORY");
        store.addBook(new PaperBook("978-0321765723", "The C++ Programming Language", "Bjarne Stroustrup", 2013, 69.99, 10));
        store.addBook(new PaperBook("978-0132350884", "Clean Code", "Robert C. Martin", 2008, 45.50, 5));
        store.addBook(new EBook("978-0134494166", "Effective Java", "Joshua Bloch", 2018, 35.00, "PDF"));
        store.addBook(new EBook("978-1492032649", "Designing Data-Intensive Applications", "Martin Kleppmann", 2017, 55.99, "EPUB"));
        store.addBook(new ShowcaseBook("DEMO-001", "Quantum Physics for Dummies", "Steven Holzner", 2013, 22.99));
        
        store.printInventory();
    }

    /**
     * Tests buying scenarios like success, failure (out of stock),
     * and trying to buy non-saleable items.
     */
    private static void testBookBuying(QuantumBookstore store) {
        logHeader("TEST 2: BUYING BOOKS");

        // Scenario 2.1: Successfully buy a paper book
        log("--> Attempting to buy 2 copies of 'Clean Code'...");
        try {
            double amountPaid = store.buyBook("978-0132350884", 2, "Abuzaid@gmail.com", "123 Gleem, Alexandria");
            log("Purchase successful! Amount paid: $" + String.format("%.2f", amountPaid));
        } catch (Exception e) {
            log("ERROR: " + e.getMessage());
        }
        store.printInventory(); // Show updated stock of books

        // Scenario 2.2: Successfully buy an eBook
        log("--> Attempting to buy 1 copy of 'Effective Java'...");
        try {
            double amountPaid = store.buyBook("978-0134494166", 1, "Marwan@yahoo.dev", null); // Address not needed for eBook because they are not delivered
            log("Purchase successful! Amount paid: $" + String.format("%.2f", amountPaid));
        } catch (Exception e) {
            log("ERROR: " + e.getMessage());
        }
        store.printInventory(); // eBook "stock" is infinite, so no change expected

        // Scenario 2.3: Attempt to buy more paper books than in stock
        log("--> Attempting to buy 10 copies of 'Clean Code' (only 3 left)...");
        try {
            store.buyBook("978-0132350884", 10, "Abuzaid@example.com", "123 Gleem, Alexandria");
        } catch (Exception e) {
            log("CAUGHT EXPECTED ERROR: " + e.getMessage());
        }

        // Scenario 2.4: Attempt to buy a showcase book
        log("--> Attempting to buy 'Quantum Physics for Dummies' (a showcase book)...");
        try {
            store.buyBook("DEMO-001", 1, "curious.shopper@email.com", "456 Sheikh Zayed, Giza");
        } catch (Exception e) {
            log("CAUGHT EXPECTED ERROR: " + e.getMessage());
        }
        
        // Scenario 2.5: Attempt to buy a book that doesn't exist
        log("--> Attempting to buy a book with a non-existent ISBN...");
        try {
            store.buyBook("000-0000000000", 1, "ghost@shopper.com", "789 Nowhere St, Alexandria");
        } catch (Exception e) {
            log("CAUGHT EXPECTED ERROR: " + e.getMessage());
        }
    }

    /**
     * Tests the removal of outdated books from the inventory.
     */
    private static void testBookRemoval(QuantumBookstore store) {
        logHeader("TEST 3: REMOVING OUTDATED BOOKS");
        log("--> Adding a very old book from 1995 for removal test...");
        store.addBook(new PaperBook("978-0201633610", "Design Patterns", "Erich Gamma", 1995, 54.99, 3));
        store.printInventory();

        log("--> Removing all books older than 20 years...");
        int currentYear = Year.now().getValue();
        int yearsOldThreshold = currentYear - 2005; // Books published before 2005
        
        List<Book> removedBooks = store.removeOutdatedBooks(yearsOldThreshold);
        log("Books removed: " + removedBooks.size());
        for (Book book : removedBooks) {
            log("  - Removed: '" + book.getTitle() + "' published in " + book.getYearPublished());
        }
        
        store.printInventory();
    }

    // --- Helper Logging Methods ---
    private static void log(String message) {
        System.out.println("Quantum book store: " + message);
    }

    private static void logHeader(String header) {
        log("\n-----------------------------------------------------");
        log(header);
        log("-----------------------------------------------------");
    }
}


// =================================================================================
// 1. CORE BOOKSTORE AND SERVICE IMPLEMENTATIONS
// =================================================================================

/**
 * The main class for the bookstore. It manages the inventory and processes transactions.
 */
class QuantumBookstore {
    private final Map<String, Book> inventory;
    private final ShippingService shippingService;
    private final MailService mailService;

    public QuantumBookstore(ShippingService shippingService, MailService mailService) {
        this.inventory = new HashMap<>();
        this.shippingService = shippingService;
        this.mailService = mailService;
    }

    /**
     * Adds a new book to the store's inventory.
     * @parameter book The book to add.
     */
    public void addBook(Book book) {
        inventory.put(book.getIsbn(), book);
        System.out.println("Quantum book store: Added '" + book.getTitle() + "' to inventory.");
    }

    /**
     * Processes the purchase of a single book.
     * @parameter isbn The ISBN of the book to buy.
     * @parameter quantity The number of copies to buy.
     * @parameter email The customer's email (for eBooks).
     * @parameter address The customer's shipping address (for PaperBooks).
     * @return The total amount paid.
     * @throws IllegalStateException if the book is not available or not for sale.
     */
    public double buyBook(String isbn, int quantity, String email, String address) {
        Book book = inventory.get(isbn);

        if (book == null) {
            throw new IllegalStateException("Book with ISBN " + isbn + " not found in inventory.");
        }

        if (!book.isForSale()) {
            throw new IllegalStateException("Book '" + book.getTitle() + "' is a showcase item and not for sale.");
        }

        // Polymorphism in action: The bookstore doesn't need to know the book type.
        // It just tells the book to handle its own purchase process.
        PurchaseContext context = new PurchaseContext(email, address, shippingService, mailService);
        book.handlePurchase(quantity, context);

        return book.getPrice() * quantity;
    }

    /**
     * Finds and removes books published before a certain year.
     * @parameter yearsOld The age threshold. Books older than this will be removed.
     * @return A list of the books that were removed.
     */
    public List<Book> removeOutdatedBooks(int yearsOld) {
        int currentYear = Year.now().getValue();
        int cutoffYear = currentYear - yearsOld;

        List<Book> booksToRemove = inventory.values().stream()
                .filter(book -> book.getYearPublished() < cutoffYear)
                .collect(Collectors.toList());

        List<Book> removedBooks = new ArrayList<>();
        for (Book book : booksToRemove) {
            inventory.remove(book.getIsbn());
            removedBooks.add(book);
        }
        
        return removedBooks;
    }

    /**
     * Prints a formatted list of the current inventory.
     */
    public void printInventory() {
        System.out.println("\n--- Current Inventory ---");
        if (inventory.isEmpty()) {
            System.out.println("Inventory is empty.");
        } else {
            inventory.values().forEach(System.out::println);
        }
        System.out.println("-------------------------\n");
    }
}

/**
 * A simple context object to pass purchase-related information cleanly.
 */
class PurchaseContext {
    final String email;
    final String address;
    final ShippingService shippingService;
    final MailService mailService;

    public PurchaseContext(String email, String address, ShippingService shippingService, MailService mailService) {
        this.email = email;
        this.address = address;
        this.shippingService = shippingService;
        this.mailService = mailService;
    }
}


// =================================================================================
// 2. SERVICE INTERFACES AND MOCK IMPLEMENTATIONS
// =================================================================================

interface ShippingService {
    void ship(Book book, String address);
}

interface MailService {
    void send(Book book, String email);
}

/**
 * A mock implementation for demonstration purposes.
 */
class MockShippingService implements ShippingService {
    @Override
    public void ship(Book book, String address) {
        System.out.println("Quantum book store: [ShippingService] Preparing to ship '" + book.getTitle() + "' to " + address);
    }
}

/**
 * A mock implementation for demonstration purposes. In a real system,
 * this would use a library like JavaMail to send an actual email.
 */
class MockMailService implements MailService {
    @Override
    public void send(Book book, String email) {
        System.out.println("Quantum book store: [MailService] Sending link for '" + book.getTitle() + "' to " + email);
    }
}


// =================================================================================
// 3. BOOK HIERARCHY (THE EXTENSIBLE PART)
// =================================================================================

/**
 * Abstract base class for all book types. Defines common properties and behaviors.
 * The key to extensibility is the abstract handlePurchase method.
 */
abstract class Book {
    protected final String isbn;
    protected final String title;
    protected final String author;
    protected final int yearPublished;
    protected final double price;

    public Book(String isbn, String title, String author, int yearPublished, double price) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.yearPublished = yearPublished;
        this.price = price;
    }

    // Getters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public int getYearPublished() { return yearPublished; }
    public double getPrice() { return price; }

    /**
     * Defines whether the book can be sold. Overridden by showcase books.
     */
    public boolean isForSale() {
        return true;
    }

    /**
     * Each subclass must implement its own logic for how a purchase is handled.
     * This is the core of the extensible design.
     */
    public abstract void handlePurchase(int quantity, PurchaseContext context);

    @Override
    public String toString() {
        return "  - ISBN: " + isbn + ", Title: " + title + ", Author: " + author + ", Year: " + yearPublished;
    }
}

/**
 * Represents a physical book with a limited stock.
 */
class PaperBook extends Book {
    private int stock;

    public PaperBook(String isbn, String title, String author, int yearPublished, double price, int stock) {
        super(isbn, title, author, yearPublished, price);
        this.stock = stock;
    }

    @Override
    public void handlePurchase(int quantity, PurchaseContext context) {
        if (quantity > stock) {
            throw new IllegalStateException("Not enough stock for '" + title + "'. Available: " + stock + ", Requested: " + quantity);
        }
        if (context.address == null || context.address.trim().isEmpty()) {
            throw new IllegalArgumentException("A shipping address is required for paper books.");
        }
        this.stock -= quantity;
        context.shippingService.ship(this, context.address);
    }

    @Override
    public String toString() {
        return super.toString() + " (Paper, Stock: " + stock + ")";
    }
}

/**
 * Represents a digital book with a specific file type. Stock is effectively infinite.
 */
class EBook extends Book {
    private final String fileType;

    public EBook(String isbn, String title, String author, int yearPublished, double price, String fileType) {
        super(isbn, title, author, yearPublished, price);
        this.fileType = fileType;
    }

    @Override
    public void handlePurchase(int quantity, PurchaseContext context) {
        if (context.email == null || context.email.trim().isEmpty()) {
            throw new IllegalArgumentException("An email address is required for eBooks.");
        }
        // No stock to reduce for eBooks.
        context.mailService.send(this, context.email);
    }

    @Override
    public String toString() {
        return super.toString() + " (eBook, Format: " + fileType + ")";
    }
}

/**
 * Represents a book that is only for display and cannot be sold.
 */
class ShowcaseBook extends Book {
    public ShowcaseBook(String isbn, String title, String author, int yearPublished, double price) {
        super(isbn, title, author, yearPublished, price);
    }

    @Override
    public boolean isForSale() {
        return false;
    }

    @Override
    public void handlePurchase(int quantity, PurchaseContext context) {
        // This should technically never be called because of the isForSale() check in the store,
        // but it's good practice to implement it defensively.
        throw new UnsupportedOperationException("Showcase books are not for sale.");
    }

    @Override
    public String toString() {
        return super.toString() + " (Showcase Only - Not for Sale)";
    }
}
