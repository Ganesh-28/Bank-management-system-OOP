import java.util.Objects;

/** Bank teller who performs assisted banking operations. */
public class Teller {
    private final String tellerID;
    private final String name;
    private final String branchID;

    public Teller(String tellerID, String name, String branchID) {
        this.tellerID = requireText(tellerID, "Teller ID cannot be empty.");
        this.name = requireText(name, "Name cannot be empty.");
        this.branchID = requireText(branchID, "Branch ID cannot be empty.");
    }

    public String getTellerID() { return tellerID; }
    public String getName() { return name; }
    public String getBranchID() { return branchID; }

    public void assistDeposit(Customer customer, Account account, double amount) {
        validateCustomerAccount(customer, account);
        account.deposit(amount);
        logTransaction(customer, account, amount, "Deposit");
    }

    public void assistWithdrawal(Customer customer, Account account, double amount) {
        validateCustomerAccount(customer, account);
        account.withdraw(amount);
        logTransaction(customer, account, amount, "Withdrawal");
    }

    public void assistTransfer(Customer customer, Account source, Account target, double amount) {
        validateCustomerAccount(customer, source);
        Objects.requireNonNull(target, "Target account cannot be null.");
        source.transfer(target, amount);
        logTransaction(customer, source, amount, "Transfer");
    }

    public void provideAccountDetails(Customer customer) {
        System.out.println(Objects.requireNonNull(customer, "Customer cannot be null."));
    }

    private void validateCustomerAccount(Customer customer, Account account) {
        Objects.requireNonNull(customer, "Customer cannot be null.");
        Objects.requireNonNull(account, "Account cannot be null.");
        if (!customer.getCustomerID().equals(account.getCustomer().getCustomerID())) {
            throw new IllegalArgumentException("Account does not belong to the specified customer.");
        }
    }

    private void logTransaction(Customer customer, Account account, double amount, String type) {
        TransactionLogger.log(new Transaction(customer.getCustomerID(), account.getAccountNumber(), type, amount));
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }

    @Override
    public String toString() {
        return "Teller{" + "tellerID='" + tellerID + '\''
                + ", name='" + name + '\'' + ", branchID='" + branchID + '\'' + '}';
    }
}
