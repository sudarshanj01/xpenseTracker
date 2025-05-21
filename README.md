# Expense Tracker

A Java console application for tracking personal finances with income and expense management capabilities.

## Features
- Add income and expenses with detailed categorization
- Categorize transactions (Food, Rent, Travel, Salary, etc.)
- View monthly summaries with income/expense breakdowns by category
- Save/load transaction data from CSV files
- Import multiple transactions from external CSV files

## Usage
Compile with `javac ExpenseTracker.java` and run with `java ExpenseTracker`. The program presents a menu to add transactions, view reports, or import data. Transaction data is automatically saved to `transactions.csv`.

## Import Format
CSV import files should follow this format: `TYPE,CATEGORY,AMOUNT,DESCRIPTION,DATE` with TYPE as INCOME/EXPENSE and DATE as YYYY-MM-DD.
