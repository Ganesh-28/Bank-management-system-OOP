
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Central registry and service layer for the bank.
 * Manages customers, accounts, tellers, ATMs and transactions.
 */
public class Bank {

    private final String name;

    private final List<Customer> customers = new ArrayList<>();
    private final List<Teller> tellers = new ArrayList<>();
    private final List<Account> accounts = new ArrayList<>();
    private final List<ATM> atms = new ArrayList<>();

    public Bank(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Bank name cannot be empty.");
        }

        this.name = name.trim();
    }

    // ---------------- CUSTOMER MANAGEMENT ----------------

    public void addCustomer(Customer customer) {
        Objects.requireNonNull(customer, "Customer cannot be null.");

        if (findCustomer(customer.getCustomerID()) != null) {
            throw new IllegalArgumentException(
                    "Customer ID already exists: " + customer.getCustomerID()
            );
        }

        customers.add(customer);
    }

    public void removeCustomer(Customer customer) {
        Objects.requireNonNull(customer, "Customer cannot be null.");

        if (!customer.viewAccounts().isEmpty()) {
            throw new IllegalArgumentException(
                    "Remove the customer's accounts before removing the customer."
            );
        }

        customers.remove(customer);
    }

    /**
     * Returns null when no customer matches.
     */
    public Customer getCustomerDetails(String customerID) {
        if (customerID == null || customerID.isBlank()) {
            return null;
        }

        return findCustomer(customerID.trim());
    }

    private Customer findCustomer(String id) {
        return customers.stream()
                .filter(customer -> customer.getCustomerID().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Customer> getCustomers() {
        return List.copyOf(customers);
    }

    // ---------------- TELLER MANAGEMENT ----------------

    public void addTeller(Teller teller) {
        Objects.requireNonNull(teller, "Teller cannot be null.");

        if (tellers.stream().anyMatch(
                existing -> existing.getTellerID().equals(teller.getTellerID())
        )) {
            throw new IllegalArgumentException("Teller ID already exists.");
        }

        tellers.add(teller);
    }

    public void removeTeller(Teller teller) {
        Objects.requireNonNull(teller);
        tellers.remove(teller);
    }

    public List<Teller> getTellers() {
        return List.copyOf(tellers);
    }

    // ---------------- ACCOUNT MANAGEMENT ----------------

    public void addAccount(Account account) {
        Objects.requireNonNull(account, "Account cannot be null.");

        if (accounts.stream().anyMatch(
                existing -> existing.getAccountNumber()
                        .equals(account.getAccountNumber())
        )) {
            throw new IllegalArgumentException("Account number already exists.");
        }

        if (findCustomer(account.getCustomer().getCustomerID()) == null) {
            throw new IllegalArgumentException(
                    "Account owner must be registered with this bank."
            );
        }

        accounts.add(account);
        account.getCustomer().addAccount(account);
    }

    public void removeAccount(Account account) {
        Objects.requireNonNull(account, "Account cannot be null.");

        accounts.remove(account);
        account.getCustomer().removeAccount(account);
    }

    public List<Account> getAccounts() {
        return List.copyOf(accounts);
    }

    // ---------------- ATM MANAGEMENT ----------------

    public void addATM(ATM atm) {
        Objects.requireNonNull(atm, "ATM cannot be null.");
        atms.add(atm);
    }

    public void removeATM(ATM atm) {
        Objects.requireNonNull(atm, "ATM cannot be null.");
        atms.remove(atm);
    }

    public List<ATM> getATMs() {
        return List.copyOf(atms);
    }

    // ---------------- DEPOSIT ----------------

    public void deposit(Customer customer, Account account, double amount) {
        validateOwnership(customer, account);

        account.deposit(amount);

        TransactionLogger.log(
                new Transaction(
                        customer.getCustomerID(),
                        account.getAccountNumber(),
                        "Deposit",
                        amount
                )
        );
    }

    // ---------------- WITHDRAWAL ----------------

    public void withdraw(Customer customer, Account account, double amount) {
        validateOwnership(customer, account);

        account.withdraw(amount);

        TransactionLogger.log(
                new Transaction(
                        customer.getCustomerID(),
                        account.getAccountNumber(),
                        "Withdrawal",
                        amount
                )
        );
    }

    // ---------------- MONEY TRANSFER ----------------

    public void transfer(
            Customer customer,
            Account source,
            Account target,
            double amount
    ) {

        // Validate the sender's ownership.
        validateOwnership(customer, source);

        // Validate the destination account.
        if (target == null || !accounts.contains(target)) {
            throw new IllegalArgumentException(
                    "Target account is not registered."
            );
        }

        // Prevent transferring money to the same account.
        if (source.equals(target)) {
            throw new IllegalArgumentException(
                    "Source and target accounts cannot be the same."
            );
        }

        // Perform the actual balance transfer.
        source.transfer(target, amount);

        // Record the outgoing transaction for the sender.
        TransactionLogger.log(
                new Transaction(
                        source.getCustomer().getCustomerID(),
                        source.getAccountNumber(),
                        "Transfer (Sent)",
                        amount
                )
        );

        // Record the incoming transaction for the receiver.
        TransactionLogger.log(
                new Transaction(
                        target.getCustomer().getCustomerID(),
                        target.getAccountNumber(),
                        "Transfer (Received)",
                        amount
                )
        );
    }

    // ---------------- VALIDATION ----------------

    private void validateOwnership(Customer customer, Account account) {

        if (customer == null || account == null) {
            throw new IllegalArgumentException(
                    "Customer and account are required."
            );
        }

        if (!accounts.contains(account)) {
            throw new IllegalArgumentException(
                    "Account is not registered with this bank."
            );
        }

        if (!account.getCustomer().getCustomerID()
                .equals(customer.getCustomerID())) {

            throw new IllegalArgumentException(
                    "Account does not belong to the specified customer."
            );
        }
    }

    // ---------------- TRANSACTION HISTORY ----------------

    public List<Transaction> getTransactionHistory(Account account) {
        Objects.requireNonNull(account, "Account cannot be null.");

        return TransactionLogger.getTransactionsForAccount(
                account.getAccountNumber()
        );
    }

    // ---------------- BANK DETAILS ----------------

    @Override
    public String toString() {
        return "Bank Name: " + name + System.lineSeparator()
                + "Customers: " + customers.size() + System.lineSeparator()
                + "Tellers: " + tellers.size() + System.lineSeparator()
                + "Accounts: " + accounts.size() + System.lineSeparator()
                + "ATMs: " + atms.size();
    }
}
