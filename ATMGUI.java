
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class ATMGUI {
    private JFrame frame;
    private JTextField amountField, pinField;
    private JTextArea displayArea;
    private JButton depositBtn, withdrawBtn, balanceBtn, historyBtn, resetBtn, loginBtn;
    private double balance = 1000.0;
    private ArrayList<String> transactionHistory = new ArrayList<>();
    private final String BALANCE_FILE = "balance.txt";
    private final String HISTORY_FILE = "transaction_history.txt";
    private boolean isLoggedIn = false;

    public ATMGUI() {
        loadBalance();
        loadTransactionHistory();
        createGUI();
    }

    private void createGUI() {
        frame = new JFrame("Simple ATM GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 450);
        frame.setLayout(new BorderLayout(10, 10));

       
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Enter PIN:"));
        pinField = new JTextField(10);
        topPanel.add(pinField);
        loginBtn = new JButton("Login");
        topPanel.add(loginBtn);
        frame.add(topPanel, BorderLayout.NORTH);

        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(4, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        amountField = new JTextField();
        centerPanel.add(new JLabel("Amount:"));
        centerPanel.add(amountField);

        depositBtn = new JButton("Deposit");
        withdrawBtn = new JButton("Withdraw");
        balanceBtn = new JButton("Check Balance");
        historyBtn = new JButton("Transaction History");
        resetBtn = new JButton("Reset Account");

        centerPanel.add(depositBtn);
        centerPanel.add(withdrawBtn);
        centerPanel.add(balanceBtn);
        centerPanel.add(historyBtn);
        centerPanel.add(resetBtn);

        frame.add(centerPanel, BorderLayout.CENTER);

        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false);
        displayArea.setBorder(BorderFactory.createTitledBorder("Display"));
        frame.add(new JScrollPane(displayArea), BorderLayout.SOUTH);

        addListeners();
        setButtonsEnabled(false);

        frame.setVisible(true);
    }

    private void addListeners() {
        loginBtn.addActionListener(e -> {
            String pin = pinField.getText().trim();
            if (pin.equals("1234")) {
                isLoggedIn = true;
                display("Login successful!");
                setButtonsEnabled(true);
            } else {
                display("Incorrect PIN. Try again.");
            }
        });

        depositBtn.addActionListener(e -> {
            if (!isLoggedIn) return;
            double amount = parseAmount();
            if (amount > 0) {
                balance += amount;
                String msg = "Deposited ₹" + amount;
                logTransaction(msg);
                display(msg);
            }
        });

        withdrawBtn.addActionListener(e -> {
            if (!isLoggedIn) return;
            double amount = parseAmount();
            if (amount > 0 && amount <= balance) {
                balance -= amount;
                String msg = "Withdrew ₹" + amount;
                logTransaction(msg);
                display(msg);
            } else {
                display("Insufficient funds or invalid amount.");
            }
        });

        balanceBtn.addActionListener(e -> {
            if (!isLoggedIn) return;
            display("Current Balance: ₹" + balance);
        });

        historyBtn.addActionListener(e -> {
            if (!isLoggedIn) return;
            display("--- Transaction History ---");
            if (transactionHistory.isEmpty()) {
                display("No transactions yet.");
            } else {
                for (String record : transactionHistory) {
                    display(record);
                }
            }
        });

        resetBtn.addActionListener(e -> {
            if (!isLoggedIn) return;
            int confirm = JOptionPane.showConfirmDialog(frame, "Reset your account?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                balance = 1000.0;
                transactionHistory.clear();
                String msg = "Account reset to ₹1000.0";
                transactionHistory.add(msg);
                saveData();
                display(msg);
            }
        });
    }

    private double parseAmount() {
        try {
            return Double.parseDouble(amountField.getText().trim());
        } catch (NumberFormatException e) {
            display("Please enter a valid number.");
            return -1;
        }
    }

    private void logTransaction(String msg) {
        transactionHistory.add(msg);
        saveData();
    }

    private void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BALANCE_FILE));
             BufferedWriter hw = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            bw.write(String.valueOf(balance));
            for (String entry : transactionHistory) {
                hw.write(entry + "\n");
            }
        } catch (IOException e) {
            display("Error saving data.");
        }
    }

    private void loadBalance() {
        File file = new File(BALANCE_FILE);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                if (line != null) {
                    balance = Double.parseDouble(line);
                }
            } catch (IOException | NumberFormatException ignored) {}
        }
    }

    private void loadTransactionHistory() {
        File file = new File(HISTORY_FILE);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    transactionHistory.add(line);
                }
            } catch (IOException ignored) {}
        }
    }

    private void display(String msg) {
        displayArea.append(msg + "\n");
    }

    private void setButtonsEnabled(boolean enabled) {
        depositBtn.setEnabled(enabled);
        withdrawBtn.setEnabled(enabled);
        balanceBtn.setEnabled(enabled);
        historyBtn.setEnabled(enabled);
        resetBtn.setEnabled(enabled);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ATMGUI::new);
    }
}
