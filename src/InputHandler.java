import java.util.Scanner;

/** Console entry point for the banking application. */
public final class InputHandler {
    private static final Bank bank = new Bank("Toy Alfalah Bank");
    private static final Scanner scanner = new Scanner(System.in);

    private InputHandler() {}

    public static void main(String[] args) {
        loadData();
        boolean running = true;
        while (running) {
            showMenu();
            int choice = getInputInt();
            try {
                switch (choice) {
                    case 1 -> addCustomer();
                    case 2 -> removeCustomer();
                    case 3 -> viewCustomerDetails();
                    case 4 -> addAccount();
                    case 5 -> removeAccount();
                    case 6 -> deposit();
                    case 7 -> withdraw();
                    case 8 -> transfer();
                    case 9 -> viewTransactionHistory();
                    case 10 -> bank.getCustomers().forEach(System.out::println);
                    case 11 -> bank.getAccounts().forEach(System.out::println);
                    case 12 -> bank.getTellers().forEach(System.out::println);
                    case 13 -> bank.getATMs().forEach(System.out::println);
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Operation failed: " + e.getMessage());
            } finally {
                saveData();
            }
        }
        saveData();
        System.out.println("Data saved. Goodbye.");
    }

    private static void loadData() {
        var customers = DataManager.loadCustomers();
        customers.values().forEach(bank::addCustomer);
        DataManager.loadAccounts(customers, bank);
    }

    private static void showMenu() {
        System.out.println("""

                --- Bank System Menu ---
                1. Add Customer
                2. Remove Customer
                3. View Customer Details
                4. Add Account
                5. Remove Account
                6. Deposit
                7. Withdraw
                8. Transfer
                9. View Transaction History
                10. Show All Customers
                11. Show All Accounts
                12. Show All Tellers
                13. Show All ATMs
                0. Exit
                """);
        System.out.print("Enter your choice: ");
    }

    private static void addCustomer() {
        String id = prompt("Enter Customer ID: ");
        Customer customer = new Customer(id, prompt("Enter Name: "),
                prompt("Enter Address: "), prompt("Enter Phone Number: "),
                prompt("Enter CNIC: "));
        bank.addCustomer(customer);
        System.out.println("Customer added successfully.");
    }

    private static void removeCustomer() {
        Customer customer = bank.getCustomerDetails(prompt("Enter Customer ID to remove: "));
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }
        bank.removeCustomer(customer);
        System.out.println("Customer removed successfully.");
    }

    private static void viewCustomerDetails() {
        Customer customer = bank.getCustomerDetails(prompt("Enter Customer ID to view: "));
        System.out.println(customer == null ? "Customer not found." : customer);
    }

    private static void addAccount() {
        Customer customer = bank.getCustomerDetails(prompt("Enter Customer ID for the account: "));
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }
        String type = prompt("Enter Account Type (Savings/Current): ");
        double openingBalance = getInputDouble("Enter Opening Balance: ");
        Account account;
        if ("Savings".equalsIgnoreCase(type)) {
            double rate = getInputDouble("Enter Interest Rate (%): ");
            account = new SavingsAccount(customer, openingBalance, rate);
        } else if ("Current".equalsIgnoreCase(type)) {
            double limit = getInputDouble("Enter Overdraft Limit: ");
            double fee = getInputDouble("Enter Overdraft Fee: ");
            account = new CurrentAccount(customer, openingBalance, limit, fee);
        } else {
            System.out.println("Invalid account type.");
            return;
        }
        bank.addAccount(account);
        System.out.println("Account created: " + account.getAccountNumber());
    }

    private static Account findAccount(String prompt) {
        String number = prompt(prompt);
        return bank.getAccounts().stream()
                .filter(a -> a.getAccountNumber().equals(number))
                .findFirst().orElse(null);
    }

    private static void removeAccount() {
        Account account = findAccount("Enter Account Number to remove: ");
        if (account == null) System.out.println("Account not found.");
        else {
            bank.removeAccount(account);
            System.out.println("Account removed successfully.");
        }
    }

    private static void deposit() {
        Account account = findAccount("Enter Account Number to deposit into: ");
        if (account == null) { System.out.println("Account not found."); return; }
        bank.deposit(account.getCustomer(), account, getInputDouble("Enter Amount to Deposit: "));
        System.out.println("Deposit successful.");
    }

    private static void withdraw() {
        Account account = findAccount("Enter Account Number to withdraw from: ");
        if (account == null) { System.out.println("Account not found."); return; }
        bank.withdraw(account.getCustomer(), account, getInputDouble("Enter Amount to Withdraw: "));
        System.out.println("Withdrawal successful.");
    }

    private static void transfer() {
        Account source = findAccount("Enter Source Account Number: ");
        if (source == null) { System.out.println("Source account not found."); return; }
        Account target = findAccount("Enter Target Account Number: ");
        if (target == null) { System.out.println("Target account not found."); return; }
        bank.transfer(source.getCustomer(), source, target,
                getInputDouble("Enter Amount to Transfer: "));
        System.out.println("Transfer successful.");
    }

    private static void viewTransactionHistory() {
        Account account = findAccount("Enter Account Number to view history: ");
        if (account == null) System.out.println("Account not found.");
        else bank.getTransactionHistory(account).forEach(System.out::println);
    }

    private static String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    public static String getInputString() { return scanner.nextLine(); }

    public static int getInputInt() {
        while (true) {
            String value = prompt("");
            try { return Integer.parseInt(value); }
            catch (NumberFormatException e) { System.out.print("Invalid input. Enter a whole number: "); }
        }
    }

    public static double getInputDouble() { return getInputDouble(""); }

    private static double getInputDouble(String message) {
        while (true) {
            String value = prompt(message);
            try {
                double parsed = Double.parseDouble(value);
                if (Double.isFinite(parsed)) return parsed;
            } catch (NumberFormatException ignored) { /* retry below */ }
            System.out.println("Invalid input. Enter a finite number.");
        }
    }

    public static void validateInput(Object input, String errorMessage) {
        if (input == null || input.toString().isBlank()) throw new IllegalArgumentException(errorMessage);
    }

    private static void saveData() {
        DataManager.saveCustomers(bank.getCustomers());
        DataManager.saveAccounts(bank.getAccounts());
    }
}
