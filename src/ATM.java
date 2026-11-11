import java.util.Objects;

/** ATM service with a cash balance and transaction logging. */
public class ATM {
    private final String atmID;
    private final String location;
    private double cashAvailable;

    public ATM(String atmID, String location, double cashAvailable) {
        this.atmID = requireText(atmID, "ATM ID cannot be empty.");
        this.location = requireText(location, "Location cannot be empty.");
        validateNonNegative(cashAvailable, "Cash available cannot be negative.");
        this.cashAvailable = cashAvailable;
    }

    public synchronized void withdraw(Account account, double amount) {
        Objects.requireNonNull(account, "Account cannot be null.");
        Account.validatePositiveAmount(amount, "Amount must be positive.");
        if (amount > cashAvailable) throw new IllegalArgumentException("ATM does not have enough cash.");
        account.withdraw(amount);
        cashAvailable -= amount;
        logTransaction(account, amount, "Withdrawal");
    }

    public synchronized void deposit(Account account, double amount) {
        Objects.requireNonNull(account, "Account cannot be null.");
        Account.validatePositiveAmount(amount, "Amount must be positive.");
        account.deposit(amount);
        cashAvailable += amount;
        logTransaction(account, amount, "Deposit");
    }

    public void transfer(Account sourceAccount, Account targetAccount, double amount) {
        Objects.requireNonNull(sourceAccount, "Source account cannot be null.");
        sourceAccount.transfer(targetAccount, amount);
        logTransaction(sourceAccount, amount, "Transfer");
    }

    public synchronized void replenishCash(double amount) {
        Account.validatePositiveAmount(amount, "Amount must be positive.");
        if (!Double.isFinite(cashAvailable + amount)) throw new IllegalArgumentException("ATM cash limit exceeded.");
        cashAvailable += amount;
    }

    public double getCashAvailable() { return cashAvailable; }
    public double checkBalance(Account account) { return Objects.requireNonNull(account).getBalance(); }

    private void logTransaction(Account account, double amount, String type) {
        TransactionLogger.log(new Transaction(account.getCustomer().getCustomerID(),
                account.getAccountNumber(), type, amount));
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }
    private static void validateNonNegative(double value, String message) {
        if (!Double.isFinite(value) || value < 0.0) throw new IllegalArgumentException(message);
    }

    @Override
    public String toString() {
        return "ATM ID: " + atmID + System.lineSeparator()
                + "Location: " + location + System.lineSeparator()
                + "Cash Available: " + cashAvailable;
    }
}
