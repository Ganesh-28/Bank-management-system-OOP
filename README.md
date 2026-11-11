# 🏦 Bank Management System

A console-based banking application developed using **Java 21** and Object-Oriented Programming (OOP) principles. The application provides essential banking operations, including customer management, account creation, deposits, withdrawals, fund transfers, and transaction history tracking.

The project demonstrates Core Java concepts, modular application design, exception handling, file handling, and CSV-based data persistence.

## 📌 Project Overview

The Bank Management System is designed to simulate basic banking operations through an interactive command-line interface. It allows users to manage customers and their bank accounts while maintaining transaction records and storing application data locally.

## ✨ Features

### Customer Management
- Add new customers.
- View customer details.
- Display all registered customers.
- Remove customers.
- Prevent duplicate customer IDs.

### Account Management
- Create Savings and Current accounts.
- View account information.
- Display all registered accounts.
- Remove existing accounts.
- Associate accounts with registered customers.

### Banking Operations
- Deposit money into accounts.
- Withdraw money with balance validation.
- Transfer funds between accounts.
- Validate account ownership.
- Prevent insufficient-balance withdrawals.

### Transaction Management
- Record deposits and withdrawals.
- Maintain transaction history.
- Record outgoing and incoming fund transfers.
- Retrieve transactions using account numbers.

### Data Persistence
- Store customer information using CSV files.
- Store account information using CSV files.
- Maintain transaction records.
- Load previously saved data when the application restarts.

### Input Validation and Error Handling
- Validate customer and account information.
- Handle invalid account numbers.
- Prevent duplicate customer registration.
- Handle insufficient balance.
- Use exception handling for invalid operations.

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core application development |
| OOP | Modular and reusable application design |
| Java Collections | Managing customers, accounts and transactions |
| File Handling | Reading and writing application data |
| CSV | Local data persistence |
| Exception Handling | Error handling and validation |
| JUnit 5 | Unit testing |

## 🏗️ Project Architecture

The application follows a modular, class-based architecture.

```text
Bank-management-system-OOP/
│
├── src/
│   ├── Account.java
│   ├── SavingsAccount.java
│   ├── CurrentAccount.java
│   ├── Customer.java
│   ├── Bank.java
│   ├── ATM.java
│   ├── Teller.java
│   ├── Transaction.java
│   ├── TransactionLogger.java
│   ├── DataManager.java
│   ├── CsvSupport.java
│   └── InputHandler.java
│
├── test/
│   └── JUnit test classes
│
├── lib/
│   └── JUnit library
│
├── .gitignore
├── README.md
├── LICENSE
├── compile.bat
└── compile.ps1
```

### Core Components

| Class | Responsibility |
|---|---|
| `Account` | Base class for bank accounts and common banking operations |
| `SavingsAccount` | Implements savings account functionality |
| `CurrentAccount` | Implements current account functionality |
| `Customer` | Manages customer information and associated accounts |
| `Bank` | Central management of customers, accounts, tellers and ATMs |
| `ATM` | Handles ATM-related banking operations |
| `Teller` | Represents bank teller operations |
| `Transaction` | Represents individual financial transactions |
| `TransactionLogger` | Records and retrieves transaction history |
| `DataManager` | Loads and saves application data |
| `CsvSupport` | Handles CSV encoding and decoding |
| `InputHandler` | Provides the interactive command-line interface |

## 🖥️ Application Screenshots

Screenshots of the application can be added to the `screenshots/` directory.

Suggested screenshots:

1. Main Menu – Application interface.
2. Customer Management – Adding and viewing customers.
3. Account Management – Creating savings and current accounts.
4. Banking Operations – Deposit, withdrawal and transfer.
5. Transaction History – Displaying account transactions.
6. Account Overview – Displaying account balances.

Example Markdown for adding screenshots:

```markdown
### Main Menu

![Main Menu](screenshots/main-menu.png)

### Account Management

![Account Management](screenshots/account-management.png)

### Transaction History

![Transaction History](screenshots/transaction-history.png)
```

> Add your actual application screenshots using these filenames before publishing the screenshots section.

## ⚙️ Installation and Setup

### Prerequisites

Make sure the following are installed:

- Java Development Kit (JDK) 21 or later.
- Git.
- Visual Studio Code or any Java-compatible IDE.

Verify your Java installation:

```bash
java -version
javac -version
```

### 1. Clone the Repository

```bash
git clone https://github.com/Ganesh-28/Bank-management-system-OOP.git
```

### 2. Navigate to the Project Directory

```bash
cd Bank-management-system-OOP
```

### 3. Compile the Application

Create a directory for compiled Java classes:

```bash
mkdir bin
```

Compile all Java source files:

```bash
javac --release 21 -encoding UTF-8 -d bin src/*.java
```

**Windows PowerShell users:** The same compilation command works in PowerShell.

### 4. Run the Application

```bash
java -cp bin InputHandler
```

## 🚀 Application Menu

The application provides the following interactive menu:

```text
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
```

Select an option and follow the instructions displayed in the terminal.

## 🧪 Testing

The project includes JUnit test classes for testing application functionality.

Testing areas include:

- Customer management.
- Account operations.
- Banking business logic.
- Transaction handling.
- Input validation.
- Exception handling.

Run the available JUnit test suite using the included JUnit library and your preferred Java test runner.

## 🧠 Core Java Concepts Demonstrated

This project demonstrates practical implementation of:

- Classes and Objects
- Encapsulation
- Inheritance
- Polymorphism
- Abstraction
- Constructors and Method Overriding
- Java Collections Framework
- Exception Handling
- File I/O
- CSV Processing
- Modular Programming
- Unit Testing

## 🔮 Future Enhancements

Potential improvements for future versions:

- Integrate MySQL using JDBC.
- Develop a REST API using Spring Boot.
- Implement user authentication and authorization.
- Add a graphical user interface.
- Introduce secure password hashing.
- Generate downloadable account statements.
- Add role-based access control.
- Improve transaction consistency and audit logging.

## 👨‍💻 Author

**Avasarala Sai Ganesh**

- GitHub: [Ganesh-28](https://github.com/Ganesh-28)
- Project Repository: [Bank Management System](https://github.com/Ganesh-28/Bank-management-system-OOP)

## 📄 License

This project is distributed under the license included in this repository. Please refer to the `LICENSE` file for details.

---

⭐ If you find this project useful, consider giving the repository a star!