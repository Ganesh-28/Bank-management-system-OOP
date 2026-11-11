import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Loads and saves customers and accounts using UTF-8 CSV files. */
public final class DataManager {
    private static final Path CUSTOMER_FILE = Path.of("customers.csv");
    private static final Path ACCOUNT_FILE = Path.of("accounts.csv");

    private DataManager() {}

    public static Map<String, Customer> loadCustomers() {
        Map<String, Customer> customers = new HashMap<>();
        if (!Files.exists(CUSTOMER_FILE)) return customers;
        try (var reader = Files.newBufferedReader(CUSTOMER_FILE)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;
                try {
                    Customer customer = Customer.fromCSV(line);
                    if (customers.putIfAbsent(customer.getCustomerID(), customer) != null) {
                        System.err.println("Skipping duplicate customer ID at line " + lineNumber);
                    }
                } catch (RuntimeException e) {
                    System.err.println("Skipping invalid customer at line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read customers.csv: " + e.getMessage(), e);
        }
        return customers;
    }

    public static void loadAccounts(Map<String, Customer> customers, Bank bank) {
        if (customers == null || bank == null) throw new IllegalArgumentException("Customers and bank are required.");
        if (!Files.exists(ACCOUNT_FILE)) return;
        try (var reader = Files.newBufferedReader(ACCOUNT_FILE)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;
                try {
                    List<String> values = CsvSupport.decode(line);
                    if (values.size() < 4) throw new IllegalArgumentException("Too few account fields.");
                    Customer customer = customers.get(values.get(3));
                    if (customer == null) {
                        System.err.println("Skipping account with unknown customer ID at line " + lineNumber);
                        continue;
                    }
                    Account account = Account.fromCSV(line, customer);
                    customer.addAccount(account);
                    bank.addAccount(account);
                } catch (RuntimeException e) {
                    System.err.println("Skipping invalid account at line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read accounts.csv: " + e.getMessage(), e);
        }
    }

    public static void saveCustomers(Collection<Customer> customers) {
        if (customers == null) throw new IllegalArgumentException("Customers cannot be null.");
        writeAtomically(CUSTOMER_FILE, customers.stream().map(Customer::toCSV).toList());
    }

    public static void saveAccounts(Collection<Account> accounts) {
        if (accounts == null) throw new IllegalArgumentException("Accounts cannot be null.");
        writeAtomically(ACCOUNT_FILE, accounts.stream().map(Account::toCSV).toList());
    }

    private static void writeAtomically(Path destination, List<String> lines) {
        Path temporary = destination.resolveSibling(destination.getFileName() + ".tmp");
        try {
            Files.write(temporary, lines, java.nio.charset.StandardCharsets.UTF_8);
            try {
                Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save " + destination + ": " + e.getMessage(), e);
        }
    }
}
