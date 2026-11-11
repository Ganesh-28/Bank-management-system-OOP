import java.util.List;

/** Current account with an overdraft limit and optional overdraft fee. */
public class CurrentAccount extends Account {
    private final double overdraftLimit;
    private final double overdraftFee;

    /** Original constructor signature retained for compatibility. */
    public CurrentAccount(Customer customer, double overdraftLimit, double overdraftFee) {
        super("Current", customer);
        validateNonNegative(overdraftLimit, "Overdraft limit cannot be negative.");
        validateNonNegative(overdraftFee, "Overdraft fee cannot be negative.");
        this.overdraftLimit = overdraftLimit;
        this.overdraftFee = overdraftFee;
    }

    /** Convenience constructor for opening an account with an initial balance. */
    public CurrentAccount(Customer customer, double openingBalance,
                          double overdraftLimit, double overdraftFee) {
        this(customer, overdraftLimit, overdraftFee);
        setBalance(openingBalance);
        if (openingBalance < -overdraftLimit) {
            throw new IllegalArgumentException("Opening balance exceeds overdraft limit.");
        }
    }

    public double getOverdraftLimit() { return overdraftLimit; }
    public double getOverdraftFee() { return overdraftFee; }

    @Override
    protected void validateWithdrawal(double amount) {
        validatePositiveAmount(amount, "Withdrawal amount must be greater than zero.");
        if (balance - amount < -overdraftLimit) {
            throw new IllegalArgumentException("Insufficient balance: overdraft limit exceeded.");
        }
    }

    @Override
    public boolean withdraw(double amount) {
        validateWithdrawal(amount);
        balance -= amount;
        return true;
    }

    @Override
    public void specificFunctionality() {
        if (balance < -overdraftLimit) {
            throw new IllegalArgumentException("Exceeds overdraft limit.");
        }
    }

    public void applyOverdraftFee() {
        if (balance >= 0.0 || overdraftFee == 0.0) return;
        double fee = Math.min(overdraftFee, balance + overdraftLimit);
        if (fee > 0.0) balance -= fee;
    }

    @Override
    public String toCSV() {
        return CsvSupport.encode(accountNumber, Double.toString(balance), accountType,
                customer.getCustomerID(), Double.toString(overdraftLimit),
                Double.toString(overdraftFee));
    }

    public static CurrentAccount fromCSV(String csv, Customer customer) {
        List<String> values = CsvSupport.decode(csv);
        if (values.size() < 6) throw new IllegalArgumentException("Invalid CSV data for CurrentAccount.");
        double balance = Double.parseDouble(values.get(1));
        double limit = Double.parseDouble(values.get(4));
        double fee = Double.parseDouble(values.get(5));
        if (!Double.isFinite(balance)) throw new IllegalArgumentException("Invalid balance.");
        CurrentAccount account = new CurrentAccount(customer, limit, fee);
        account.setAccountNumber(values.get(0));
        account.setBalance(balance);
        account.specificFunctionality();
        return account;
    }

    @Override
    public String toString() {
        return super.toString() + System.lineSeparator()
                + "Overdraft Limit: " + overdraftLimit + System.lineSeparator()
                + "Overdraft Fee: " + overdraftFee;
    }
}
