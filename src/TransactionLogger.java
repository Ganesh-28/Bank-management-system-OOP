import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/** Append-only CSV transaction log. */
public final class TransactionLogger {
    private static final Path FILE = Path.of("transactions.csv");

    private TransactionLogger() {}

    public static synchronized void log(Transaction transaction) {
        if (transaction == null) throw new IllegalArgumentException("Transaction cannot be null.");
        try {
            Files.writeString(FILE, transaction.toCSV() + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write transaction log: " + e.getMessage(), e);
        }
    }

    public static synchronized List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        if (!Files.exists(FILE)) return transactions;
        try (var reader = Files.newBufferedReader(FILE)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;
                try {
                    transactions.add(Transaction.fromCSV(line));
                } catch (RuntimeException e) {
                    System.err.println("Skipping invalid transaction at line " + lineNumber
                            + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read transaction log: " + e.getMessage(), e);
        }
        return List.copyOf(transactions);
    }

    public static synchronized List<Transaction> getTransactionsForAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }
        return getTransactions().stream()
                .filter(tx -> tx.getAccountNumber().equals(accountNumber))
                .toList();
    }
}
