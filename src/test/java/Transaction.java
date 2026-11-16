import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Transaction {
}
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Comparable<Transaction> {
    private final LocalDate date;
    private final LocalTime time;
    private final String description;
    private final String vendor;
    private double amount;

    public Transaction(LocalDate date, LocalTime time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    // Formats the transaction for saving to transactions.csv
    public String toCsvString() {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        return String.format("%s|%s|%s|%s|%.2f",
                date.format(dateFmt), time.format(timeFmt), description, vendor, amount);
    }

    // Formats the transaction cleanly for the console display
    @Override
    public String toString() {
        return String.format("%-12s | %-10s | %-25s | %-20s | %10.2f",
                date, time.withNano(0), description, vendor, amount);
    }

    // Sorts newest entries first
    @Override
    public int compareTo(Transaction other) {
        LocalDateTime thisDateTime = LocalDateTime.of(this.date, this.time);
        LocalDateTime otherDateTime = LocalDateTime.of(other.date, other.time);
        return otherDateTime.compareTo(thisDateTime);
    }
}
