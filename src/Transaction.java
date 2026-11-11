import java.util.List;

/** Immutable record of a successful banking transaction. */
public class Transaction {
    private final String customerID;
    private final String accountNumber;
    private final String type;
    private final double amount;

    public Transaction(String customerID, String accountNumber, String type, double amount) {
        this.customerID = requireText(customerID, "Customer ID cannot be empty.");
        this.accountNumber = requireText(accountNumber, "Account number cannot be empty.");
        this.type = requireText(type, "Transaction type cannot be empty.");
        validateAmount(amount, "Amount must be greater than zero.");
        this.amount = amount;
    }

    public String getCustomerID() { return customerID; }
    public String getAccountNumber() { return accountNumber; }
    public String getType() { return type; }
    public double getAmount() { return amount; }

    public void validateAmount(double amount, String errorMessage) {
        if (!Double.isFinite(amount) || amount <= 0.0) throw new IllegalArgumentException(errorMessage);
    }

    public void validateInput(String input, String errorMessage) {
        requireText(input, errorMessage);
    }

    public String toCSV() {
        return CsvSupport.encode(customerID, accountNumber, type, Double.toString(amount));
    }

    public static Transaction fromCSV(String csv) {
        List<String> parts = CsvSupport.decode(csv);
        if (parts.size() != 4) throw new IllegalArgumentException("Invalid CSV format for Transaction.");
        return new Transaction(parts.get(0), parts.get(1), parts.get(2),
                Double.parseDouble(parts.get(3)));
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.ROOT,
                "Transaction Details:%nCustomer ID: %s%nAccount Number: %s%nType: %s%nAmount: %.2f",
                customerID, accountNumber, type, amount);
    }
}
