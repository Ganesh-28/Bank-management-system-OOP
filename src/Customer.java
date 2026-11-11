import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** A bank customer and the accounts owned by that customer. */
public class Customer {
    private final String name;
    private final String address;
    private final String customerID;
    private final String phoneNumber;
    private final String CNIC;
    private final List<Account> accounts = new ArrayList<>();

    public Customer(String customerID, String name, String address,
                    String phoneNumber, String CNIC) {
        this.customerID = requireText(customerID, "Customer ID cannot be empty.");
        this.name = requireText(name, "Name cannot be empty.");
        this.address = requireText(address, "Address cannot be empty.");
        this.phoneNumber = requireText(phoneNumber, "Phone number cannot be empty.");
        this.CNIC = requireText(CNIC, "CNIC cannot be empty.");
    }

    /** Compatibility constructor; the supplied list is defensively copied. */
    public Customer(String customerID, String name, String address,
                    String phoneNumber, String CNIC, List<Account> accounts) {
        this(customerID, name, address, phoneNumber, CNIC);
        if (accounts != null) {
            accounts.forEach(this::addAccount);
        }
    }

    public String getCustomerID() { return customerID; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getCNIC() { return CNIC; }

    public void addAccount(Account account) {
        Objects.requireNonNull(account, "Account cannot be null.");
        if (!customerID.equals(account.getCustomer().getCustomerID())) {
            throw new IllegalArgumentException("Account belongs to a different customer.");
        }
        boolean alreadyAdded = accounts.stream()
                .anyMatch(existing -> existing.getAccountNumber().equals(account.getAccountNumber()));
        if (!alreadyAdded) accounts.add(account);
    }

    public void removeAccount(Account account) {
        Objects.requireNonNull(account, "Account cannot be null.");
        accounts.remove(account);
    }

    public List<Account> viewAccounts() {
        return List.copyOf(accounts);
    }

    public String toCSV() {
        return CsvSupport.encode(customerID, name, address, phoneNumber, CNIC,
                accounts.stream().map(Account::getAccountNumber)
                        .collect(java.util.stream.Collectors.joining(";")));
    }

    public static Customer fromCSV(String csv) {
        List<String> values = CsvSupport.decode(csv);
        if (values.size() < 5) {
            throw new IllegalArgumentException("Invalid CSV data for Customer.");
        }
        return new Customer(values.get(0), values.get(1), values.get(2),
                values.get(3), values.get(4));
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }

    @Override
    public String toString() {
        return "Customer ID: " + customerID + System.lineSeparator()
                + "Name: " + name + System.lineSeparator()
                + "Address: " + address + System.lineSeparator()
                + "Phone Number: " + phoneNumber + System.lineSeparator()
                + "CNIC: " + CNIC + System.lineSeparator()
                + "Accounts: " + accounts.size();
    }
}
