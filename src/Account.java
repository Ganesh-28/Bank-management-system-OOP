import java.util.Objects;
import java.util.UUID;

/**
 * Base class for bank accounts.
 *
 * Monetary values remain {@code double} to retain compatibility with the
 * original project's public API. For a production banking system, use
 * BigDecimal and a persistent database instead.
 */
public abstract class Account {
    protected String accountNumber;
    protected double balance;
    protected final String accountType;
    protected final Customer customer;

    protected Account(String accountType, Customer customer) {
        this.accountType = requireText(accountType, "Account type cannot be empty.");
        this.customer = Objects.requireNonNull(customer, "Customer cannot be null.");
        this.accountNumber = UUID.randomUUID().toString();
        this.balance = 0.0;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public double getBalance() { return balance; }
    public Customer getCustomer() { return customer; }

    public boolean deposit(double amount) {
        validatePositiveAmount(amount, "Deposit amount must be greater than zero.");
        balance += amount;
        return true;
    }

    public boolean withdraw(double amount) {
        validatePositiveAmount(amount, "Withdrawal amount must be greater than zero.");
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        balance -= amount;
        return true;
    }

    /**
     * Transfers funds atomically with respect to validation: all validations
     * are performed before either balance is changed.
     */
    public boolean transfer(Account targetAccount, double amount) {
        Objects.requireNonNull(targetAccount, "Target account cannot be null.");
        validatePositiveAmount(amount, "Transfer amount must be greater than zero.");
        if (targetAccount == this) {
            throw new IllegalArgumentException("Source and target accounts must be different.");
        }
        validateWithdrawal(amount);
        targetAccount.validateDeposit(amount);
        balance -= amount;
        targetAccount.balance += amount;
        return true;
    }

    protected void validateWithdrawal(double amount) {
        validatePositiveAmount(amount, "Withdrawal amount must be greater than zero.");
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
    }

    protected void validateDeposit(double amount) {
        validatePositiveAmount(amount, "Deposit amount must be greater than zero.");
        if (!Double.isFinite(balance + amount)) {
            throw new IllegalArgumentException("Resulting balance is outside the supported range.");
        }
    }

    protected static void validatePositiveAmount(double amount, String message) {
        if (!Double.isFinite(amount) || amount <= 0.0) {
            throw new IllegalArgumentException(message);
        }
    }

    protected static void validateNonNegative(double amount, String message) {
        if (!Double.isFinite(amount) || amount < 0.0) {
            throw new IllegalArgumentException(message);
        }
    }

    protected static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    protected void setAccountNumber(String accountNumber) {
        this.accountNumber = requireText(accountNumber, "Account number cannot be empty.");
    }

    protected void setBalance(double balance) {
        if (!Double.isFinite(balance)) {
            throw new IllegalArgumentException("Balance must be a finite number.");
        }
        this.balance = balance;
    }

    public abstract void specificFunctionality();

    public String toCSV() {
        return CsvSupport.encode(accountNumber, Double.toString(balance), accountType,
                customer.getCustomerID());
    }

    public static Account fromCSV(String csv, Customer customer) {
        var values = CsvSupport.decode(csv);
        if (values.size() < 4) {
            throw new IllegalArgumentException("Invalid CSV data for Account.");
        }
        return switch (values.get(2)) {
            case "Savings" -> SavingsAccount.fromCSV(csv, customer);
            case "Current" -> CurrentAccount.fromCSV(csv, customer);
            default -> throw new IllegalArgumentException("Unknown account type: " + values.get(2));
        };
    }

    @Override
    public String toString() {
        return "Account Number: " + accountNumber + System.lineSeparator()
                + "Account Type: " + accountType + System.lineSeparator()
                + "Balance: " + String.format(java.util.Locale.ROOT, "%.2f", balance)
                + System.lineSeparator() + "Customer: " + customer.getName();
    }
}
