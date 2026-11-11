import java.util.List;

/** Savings account with a configurable annual/simple interest rate. */
public class SavingsAccount extends Account {
    private static final double SPECIAL_TIER_THRESHOLD = 10_000.0;
    private final double interestRate;

    public SavingsAccount(Customer customer, double interestRate) {
        super("Savings", customer);
        validateNonNegative(interestRate, "Interest rate cannot be negative.");
        this.interestRate = interestRate;
    }

    public SavingsAccount(Customer customer, double balance, double interestRate) {
        this(customer, interestRate);
        setBalance(balance);
        if (balance < 0.0) throw new IllegalArgumentException("Savings balance cannot be negative.");
    }

    public double getInterestRate() { return interestRate; }

    @Override
    public void specificFunctionality() {
        applyInterest();
        if (balance >= SPECIAL_TIER_THRESHOLD) {
            System.out.println("Congratulations! You qualify for the special savings tier.");
        }
    }

    public void applyInterest() {
        validateNonNegative(interestRate, "Interest rate cannot be negative.");
        if (interestRate == 0.0 || balance == 0.0) return;
        double interest = balance * (interestRate / 100.0);
        if (!Double.isFinite(interest)) {
            throw new IllegalArgumentException("Calculated interest is outside the supported range.");
        }
        balance += interest;
    }

    @Override
    public String toCSV() {
        return CsvSupport.encode(accountNumber, Double.toString(balance), accountType,
                customer.getCustomerID(), Double.toString(interestRate));
    }

    public static SavingsAccount fromCSV(String csv, Customer customer) {
        List<String> values = CsvSupport.decode(csv);
        if (values.size() < 5) throw new IllegalArgumentException("Invalid CSV data for SavingsAccount.");
        double balance = parseFinite(values.get(1), "balance");
        double rate = parseFinite(values.get(4), "interest rate");
        SavingsAccount account = new SavingsAccount(customer, rate);
        account.setAccountNumber(values.get(0));
        account.setBalance(balance);
        if (balance < 0.0) throw new IllegalArgumentException("Savings balance cannot be negative.");
        return account;
    }

    private static double parseFinite(String value, String field) {
        double parsed = Double.parseDouble(value);
        if (!Double.isFinite(parsed)) throw new IllegalArgumentException("Invalid " + field + ".");
        return parsed;
    }

    @Override
    public String toString() {
        return super.toString() + System.lineSeparator() + "Interest Rate: " + interestRate + "%";
    }
}
