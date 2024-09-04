# Banking System

A simple banking system implemented in Java, allowing users to perform various banking operations such as creating accounts, depositing and withdrawing money, transferring funds, and viewing account details.

## Features

*   Create new bank accounts
*   Deposit and withdraw money from accounts
*   Transfer funds between accounts
*   View account details, including balance and transaction history
*   Delete accounts

## Requirements

*   Java 8 or later
*   MySQL database (configured in the `Banking` class)

## Usage

1.  Compile and run the program: `javac Banking.java` and `java Banking`
2.  Follow the prompts to perform banking operations.

## Code Structure

The code is organized into three main classes:

*   `Banking`: contains the main method and sets up the database connection.
*   `BankManagement`: provides methods for performing banking operations, such as creating accounts and depositing money.
*   `TransferAmount`: handles fund transfers between accounts.

## Database Schema

The database schema consists of two tables:

*   `account`: stores account information, including customer ID, name, account number, account type, and balance.
*   `transaction`: stores transaction history, including transaction amount, type, and order number.

## License

This project is for personal use only.

## Acknowledgments

*   MySQL Connector/J for database connectivity
*   Java 8 for the programming language


