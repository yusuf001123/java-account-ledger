public class FileManager {
}
import java.io.*;
        import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileManager {
    private static final String FILE_NAME = "transactions.csv";

    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return transactions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip header or empty lines
                if (line.trim().isEmpty() || line.startsWith("date|")) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    LocalDate date = LocalDate.parse(parts[0].trim());
                    LocalTime time = LocalTime.parse(parts[1].trim());
                    String description = parts[2].trim();
                    String vendor = parts[3].trim();
                    double amount = Double.parseDouble(parts[4].trim());

                    transactions.add(new Transaction(date, time, description, vendor, amount));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions file: " + e.getMessage());
        }

        Collections.sort(transactions); // Ensure newest first
        return transactions;
    }

    public static void saveTransaction(Transaction transaction) {
        File file = new File(FILE_NAME);
        boolean fileExists = file.exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (!fileExists) {
                writer.write("date|time|description|vendor|amount");
                writer.newLine();
            }
            writer.write(transaction.toCsvString());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }
}