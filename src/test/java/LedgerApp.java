public class LedgerApp {
}
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class LedgerApp {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n==========================================");
            System.out.println("        ACCOUNTING LEDGER - HOME          ");
            System.out.println("==========================================");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "D" -> addTransaction(true);
                case "P" -> addTransaction(false);
                case "L" -> showLedgerMenu();
                case "X" -> {
                    System.out.println("Exiting application. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addTransaction(boolean isDeposit) {
        System.out.println(isDeposit ? "\n--- ADD DEPOSIT ---" : "\n--- MAKE PAYMENT (DEBIT) ---");

        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Enter vendor: ");
        String vendor = scanner.nextLine().trim();

        System.out.print("Enter amount: ");
        double amount;
        try {
            amount = Math.abs(Double.parseDouble(scanner.nextLine().trim()));
            if (!isDeposit) {
                amount = -amount; // Ensure payments are stored as negative values
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered. Transaction cancelled.");
            return;
        }

        Transaction transaction = new Transaction(LocalDate.now(), LocalTime.now(), description, vendor, amount);
        FileManager.saveTransaction(transaction);
        System.out.println("Transaction saved successfully!");
    }

    private static void showLedgerMenu() {
        boolean inLedger = true;

        while (inLedger) {
            System.out.println("\n==========================================");
            System.out.println("               LEDGER MENU                ");
            System.out.println("==========================================");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim().toUpperCase();
            List<Transaction> allTransactions = FileManager.loadTransactions();

            switch (choice) {
                case "A" -> displayTransactions("ALL ENTRIES", allTransactions);
                case "D" -> {
                    List<Transaction> deposits = allTransactions.stream()
                            .filter(t -> t.getAmount() > 0)
                            .collect(Collectors.toList());
                    displayTransactions("DEPOSITS", deposits);
                }
                case "P" -> {
                    List<Transaction> payments = allTransactions.stream()
                            .filter(t -> t.getAmount() < 0)
                            .collect(Collectors.toList());
                    displayTransactions("PAYMENTS", payments);
                }
                case "R" -> showReportsMenu();
                case "H" -> inLedger = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void showReportsMenu() {
        boolean inReports = true;

        while (inReports) {
            System.out.println("\n==========================================");
            System.out.println("               REPORTS MENU               ");
            System.out.println("==========================================");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search (Bonus)");
            System.out.println("0) Back");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            List<Transaction> all = FileManager.loadTransactions();
            LocalDate today = LocalDate.now();

            switch (choice) {
                case "1" -> { // Month To Date
                    LocalDate startOfMonth = today.withDayOfMonth(1);
                    List<Transaction> filtered = all.stream()
                            .filter(t -> !t.getDate().isBefore(startOfMonth) && !t.getDate().isAfter(today))
                            .collect(Collectors.toList());
                    displayTransactions("MONTH TO DATE", filtered);
                }
                case "2" -> { // Previous Month
                    LocalDate prevMonth = today.minusMonths(1);
                    List<Transaction> filtered = all.stream()
                            .filter(t -> t.getDate().getYear() == prevMonth.getYear()
                                    && t.getDate().getMonth() == prevMonth.getMonth())
                            .collect(Collectors.toList());
                    displayTransactions("PREVIOUS MONTH", filtered);
                }
                case "3" -> { // Year To Date
                    LocalDate startOfYear = today.withDayOfYear(1);
                    List<Transaction> filtered = all.stream()
                            .filter(t -> !t.getDate().isBefore(startOfYear) && !t.getDate().isAfter(today))
                            .collect(Collectors.toList());
                    displayTransactions("YEAR TO DATE", filtered);
                }
                case "4" -> { // Previous Year
                    int prevYear = today.getYear() - 1;
                    List<Transaction> filtered = all.stream()
                            .filter(t -> t.getDate().getYear() == prevYear)
                            .collect(Collectors.toList());
                    displayTransactions("PREVIOUS YEAR", filtered);
                }
                case "5" -> { // Search by Vendor
                    System.out.print("Enter vendor name: ");
                    String vendorQuery = scanner.nextLine().trim().toLowerCase();
                    List<Transaction> filtered = all.stream()
                            .filter(t -> t.getVendor().toLowerCase().contains(vendorQuery))
                            .collect(Collectors.toList());
                    displayTransactions("VENDOR SEARCH: " + vendorQuery, filtered);
                }
                case "6" -> runCustomSearch(all);
                case "0" -> inReports = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void runCustomSearch(List<Transaction> all) {
        System.out.println("\n--- CUSTOM SEARCH (Press Enter to skip any field) ---");

        System.out.print("Start Date (YYYY-MM-DD): ");
        String startInput = scanner.nextLine().trim();
        LocalDate startDate = startInput.isEmpty() ? null : LocalDate.parse(startInput);

        System.out.print("End Date (YYYY-MM-DD): ");
        String endInput = scanner.nextLine().trim();
        LocalDate endDate = endInput.isEmpty() ? null : LocalDate.parse(endInput);

        System.out.print("Description: ");
        String descInput = scanner.nextLine().trim().toLowerCase();

        System.out.print("Vendor: ");
        String vendorInput = scanner.nextLine().trim().toLowerCase();

        System.out.print("Amount: ");
        String amountInput = scanner.nextLine().trim();
        Double amount = amountInput.isEmpty() ? null : Double.parseDouble(amountInput);

        List<Transaction> results = all.stream()
                .filter(t -> startDate == null || !t.getDate().isBefore(startDate))
                .filter(t -> endDate == null || !t.getDate().isAfter(endDate))
                .filter(t -> descInput.isEmpty() || t.getDescription().toLowerCase().contains(descInput))
                .filter(t -> vendorInput.isEmpty() || t.getVendor().toLowerCase().contains(vendorInput))
                .filter(t -> amount == null || Double.compare(t.getAmount(), amount) == 0)
                .collect(Collectors.toList());

        displayTransactions("CUSTOM SEARCH RESULTS", results);
    }

    private static void displayTransactions(String title, List<Transaction> transactions) {
        System.out.println("\n--- " + title + " ---");
        if (transactions.isEmpty()) {
            return;
        }
        System.out.printf("%-12s | %-10s | %-25s | %-20s | %10s%n",
                "DATE", "TIME", "DESCRIPTION", "VENDOR", "AMOUNT");
        System.out.println("-".repeat(88));
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }
}