 
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UniversalCurrencyConverter {

    private static final Map<String, Double> exchangeRates = new HashMap<>();

    static {
        exchangeRates.put("USD", 1.0);
        exchangeRates.put("INR", 83.0);
        exchangeRates.put("EUR", 0.93);
        exchangeRates.put("GBP", 0.80);
        exchangeRates.put("JPY", 154.5);
        exchangeRates.put("AUD", 1.52);
        exchangeRates.put("CAD", 1.36);
        exchangeRates.put("CNY", 7.24);
        exchangeRates.put("AED", 3.67);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String continueChoice = "yes"; 

        System.out.println("==========================================");
        System.out.println("         UNIVERSAL CURRENCY CONVERTER     ");
        System.out.println("==========================================");
        System.out.println("Supported Currencies: " + exchangeRates.keySet());
        System.out.println();

        do {
            System.out.print("Enter source currency code (e.g., INR): ");
            String fromCurrency = scanner.next().toUpperCase();

            System.out.print("Enter target currency code (e.g., USD): ");
            String toCurrency = scanner.next().toUpperCase();

            if (!exchangeRates.containsKey(fromCurrency) || !exchangeRates.containsKey(toCurrency)) {
                System.out.println(" Error: Invalid currency code entered.\n");
                continue;
            }

            System.out.print("Enter amount in " + fromCurrency + ": ");
            double amount;
            try {
                amount = scanner.nextDouble();
                if (amount < 0) {
                    System.out.println(" Error: Amount cannot be negative.\n");
                    continue;
                }
            } catch (Exception e) {
                System.out.println(" Error: Invalid amount entered.\n");
                scanner.nextLine(); 
                continue;
            }

            double result = convertCurrency(fromCurrency, toCurrency, amount);
            System.out.printf(" %.2f %s = %.2f %s%n%n", amount, fromCurrency, result, toCurrency);

            
            ZonedDateTime indiaTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            System.out.println(" Date & Time (IST): " + indiaTime.format(formatter));

            System.out.print(" Do you want to convert another amount? (yes/no): ");
            continueChoice = scanner.next().toLowerCase();

        } while (continueChoice.equals("yes") || continueChoice.equals("y"));

        System.out.println("\n Thank you for using the Universal Currency Converter!");
        scanner.close();
    }

    private static double convertCurrency(String from, String to, double amount) {
        double amountInUSD = amount / exchangeRates.get(from);
        return amountInUSD * exchangeRates.get(to);
    }
}

