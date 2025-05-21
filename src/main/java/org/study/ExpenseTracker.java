package org.study;

import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExpenseTracker {
    private static final String DATA_FILE = "transactions.csv";
    private static final Scanner scanner = new Scanner(System.in);
    private static List<Transaction> transactions = new ArrayList<>();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        loadTransactionsFromFile();

        boolean exit = false;
        while (!exit) {
            System.out.println("\n===== EXPENSE TRACKER =====");
            System.out.println("1. Add New Transaction");
            System.out.println("2. View Monthly Summary");
            System.out.println("3. Import Transactions from File");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    addNewTransaction();
                    break;
                case 2:
                    viewMonthlySummary();
                    break;
                case 3:
                    importTransactions();
                    break;
                case 4:
                    exit = true;
                    System.out.println("Thank you for using Expense Tracker!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addNewTransaction() {
        System.out.println("\n----- Add New Transaction -----");
        System.out.println("Select transaction type:");
        System.out.println("1. Income");
        System.out.println("2. Expense");
        System.out.print("Enter choice: ");

        int typeChoice = getIntInput();
        TransactionType type;

        if (typeChoice == 1) {
            type = TransactionType.INCOME;
        } else if (typeChoice == 2) {
            type = TransactionType.EXPENSE;
        } else {
            System.out.println("Invalid choice. Returning to main menu.");
            return;
        }

        String category = getCategory(type);
        if (category.equals("CANCEL")) return;

        System.out.print("Enter amount: $");
        double amount = getDoubleInput();

        System.out.print("Enter description (optional): ");
        String description = scanner.nextLine();

        System.out.print(String.format("Enter date (yyyy-MM-dd) or press Enter for today's date (%s): ",
                LocalDate.now().format(dateFormatter)));
        String dateInput = scanner.nextLine();

        LocalDate date;
        if (dateInput.isEmpty()) {
            date = LocalDate.now();
        } else {
            try {
                date = LocalDate.parse(dateInput, dateFormatter);
            } catch (Exception e) {
                System.out.println("Invalid date format. Using today's date.");
                date = LocalDate.now();
            }
        }

        Transaction transaction = new Transaction(type, category, amount, description, date);
        transactions.add(transaction);
        saveTransactionsToFile();

        System.out.println("Transaction added successfully!");
    }

    private static String getCategory(TransactionType type) {
        List<String> categories;
        String prompt;

        if (type == TransactionType.INCOME) {
            categories = Arrays.asList("Salary", "Business", "Investment", "Gift", "Other");
            prompt = "Select income category:";
        } else {
            categories = Arrays.asList("Food", "Rent", "Utilities", "Transportation", "Entertainment",
                    "Shopping", "Travel", "Healthcare", "Education", "Other");
            prompt = "Select expense category:";
        }

        System.out.println("\n" + prompt);
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        System.out.println((categories.size() + 1) + ". Custom category");
        System.out.println("0. Cancel");

        System.out.print("Enter choice: ");
        int categoryChoice = getIntInput();

        if (categoryChoice == 0) {
            return "CANCEL";
        } else if (categoryChoice > 0 && categoryChoice <= categories.size()) {
            return categories.get(categoryChoice - 1);
        } else if (categoryChoice == categories.size() + 1) {
            System.out.print("Enter custom category: ");
            return scanner.nextLine();
        } else {
            System.out.println("Invalid choice. Using 'Other'.");
            return "Other";
        }
    }

    private static void viewMonthlySummary() {
        System.out.println("\n----- Monthly Summary -----");

        if (transactions.isEmpty()) {
            System.out.println("No transactions available.");
            return;
        }

        System.out.print("Enter year (YYYY): ");
        int year = getIntInput();

        System.out.println("Enter month (1-12): ");
        int month = getIntInput();

        if (month < 1 || month > 12) {
            System.out.println("Invalid month. Returning to main menu.");
            return;
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Transaction> monthlyTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (!transaction.getDate().isBefore(startDate) && !transaction.getDate().isAfter(endDate)) {
                monthlyTransactions.add(transaction);
            }
        }

        if (monthlyTransactions.isEmpty()) {
            System.out.println("No transactions found for " + Month.of(month) + " " + year);
            return;
        }

        double totalIncome = 0;
        double totalExpense = 0;
        Map<String, Double> incomeByCategory = new HashMap<>();
        Map<String, Double> expenseByCategory = new HashMap<>();

        for (Transaction t : monthlyTransactions) {
            if (t.getType() == TransactionType.INCOME) {
                totalIncome += t.getAmount();
                incomeByCategory.put(t.getCategory(),
                        incomeByCategory.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            } else {
                totalExpense += t.getAmount();
                expenseByCategory.put(t.getCategory(),
                        expenseByCategory.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            }
        }

        // Display summary
        System.out.println("\nSummary for " + Month.of(month) + " " + year);
        System.out.println("--------------------------------");
        System.out.printf("Total Income:  $%.2f\n", totalIncome);
        System.out.printf("Total Expense: $%.2f\n", totalExpense);
        System.out.printf("Net Balance:   $%.2f\n", totalIncome - totalExpense);

        // Display income by category
        System.out.println("\nIncome by Category:");
        for (Map.Entry<String, Double> entry : incomeByCategory.entrySet()) {
            System.out.printf("  %-15s $%.2f\n", entry.getKey() + ":", entry.getValue());
        }

        // Display expense by category
        System.out.println("\nExpense by Category:");
        for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
            System.out.printf("  %-15s $%.2f\n", entry.getKey() + ":", entry.getValue());
        }

        // Display all transactions for the month
        System.out.println("\nAll Transactions:");
        System.out.println("--------------------------------");
        System.out.printf("%-12s %-10s %-15s %-10s %s\n", "Date", "Type", "Category", "Amount", "Description");

        for (Transaction t : monthlyTransactions) {
            System.out.printf("%-12s %-10s %-15s $%-9.2f %s\n",
                    t.getDate().format(dateFormatter),
                    t.getType(),
                    t.getCategory(),
                    t.getAmount(),
                    t.getDescription());
        }
    }

    private static void importTransactions() {
        System.out.println("\n----- Import Transactions -----");
        System.out.print("Enter file path: ");
        String filePath = scanner.nextLine();

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            System.out.println("File not found: " + filePath);
            return;
        }

        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Skip header line if exists
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    // Check if this is a header line
                    if (line.startsWith("TYPE,") || line.startsWith("Date,")) {
                        continue;
                    }
                }

                try {
                    // Split by comma but handle potential commas within quoted fields
                    String[] parts;
                    if (line.contains("\"")) {
                        // Use a more sophisticated approach for CSV with quoted fields
                        List<String> fields = new ArrayList<>();
                        boolean inQuotes = false;
                        StringBuilder field = new StringBuilder();

                        for (char c : line.toCharArray()) {
                            if (c == '\"') {
                                inQuotes = !inQuotes;
                            } else if (c == ',' && !inQuotes) {
                                fields.add(field.toString().trim());
                                field = new StringBuilder();
                            } else {
                                field.append(c);
                            }
                        }
                        fields.add(field.toString().trim());
                        parts = fields.toArray(new String[0]);
                    } else {
                        parts = line.split(",");
                    }

                    if (parts.length < 5) {
                        System.out.println("Invalid line format (needs 5 fields): " + line);
                        continue;
                    }

                    // Parse the transaction type
                    TransactionType type;
                    try {
                        type = TransactionType.valueOf(parts[0].trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid transaction type: " + parts[0] + ". Must be INCOME or EXPENSE.");
                        continue;
                    }

                    String category = parts[1].trim();

                    // Parse the amount
                    double amount;
                    try {
                        amount = Double.parseDouble(parts[2].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount: " + parts[2]);
                        continue;
                    }

                    String description = parts[3].trim();

                    // Parse the date
                    LocalDate date;
                    try {
                        date = LocalDate.parse(parts[4].trim(), dateFormatter);
                    } catch (Exception e) {
                        System.out.println("Invalid date format: " + parts[4] + ". Use YYYY-MM-DD format.");
                        continue;
                    }

                    Transaction transaction = new Transaction(type, category, amount, description, date);
                    transactions.add(transaction);
                    count++;

                } catch (Exception e) {
                    System.out.println("Error parsing line: " + line + " (" + e.getMessage() + ")");
                }
            }

            if (count > 0) {
                saveTransactionsToFile();
            }

            System.out.println(count + " transactions imported successfully.");

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private static void loadTransactionsFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }

        transactions.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Skip header
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length < 5) continue;

                    TransactionType type = TransactionType.valueOf(parts[0]);
                    String category = parts[1];
                    double amount = Double.parseDouble(parts[2]);
                    String description = parts[3];
                    LocalDate date = LocalDate.parse(parts[4], dateFormatter);

                    Transaction transaction = new Transaction(type, category, amount, description, date);
                    transactions.add(transaction);

                } catch (Exception e) {
                    // Skip invalid lines
                }
            }

            System.out.println(transactions.size() + " transactions loaded.");

        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }

    private static void saveTransactionsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            // Write header
            writer.println("TYPE,CATEGORY,AMOUNT,DESCRIPTION,DATE");

            // Write transactions
            for (Transaction t : transactions) {
                writer.printf("%s,%s,%.2f,%s,%s\n",
                        t.getType(),
                        t.getCategory(),
                        t.getAmount(),
                        t.getDescription().replace(",", ";"),  // Avoid CSV issues
                        t.getDate().format(dateFormatter));
            }

        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    private static int getIntInput() {
        try {
            int value = Integer.parseInt(scanner.nextLine().trim());
            return value;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double getDoubleInput() {
        try {
            double value = Double.parseDouble(scanner.nextLine().trim());
            return value;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}