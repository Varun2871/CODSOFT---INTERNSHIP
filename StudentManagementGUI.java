import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class StudentManagementGUI extends JFrame {


static class Student {

String name;

int rollNumber;

int marks;

String grade;



Student(String name, int rollNumber, int marks) {

this.name = name.trim(); 

this.rollNumber = rollNumber;

this.marks = Math.max(0, Math.min(100, marks));

this.grade = calculateGrade(this.marks);

}



static String calculateGrade(int marks) {

if (marks >= 90) return "A";

else if (marks >= 80) return "B";

else if (marks >= 70) return "C";

else if (marks >= 60) return "D";

else return "F";

}

@Override

public String toString() {

return name + "," + rollNumber + "," + marks + "," + grade;

}

public String prettyString() {

return "Name: " + name + " | Roll No: " + rollNumber + " | Marks: " + marks + " | Grade: " + grade;

}

}

private ArrayList<Student> students = new ArrayList<>();

private final String FILENAME = "students.txt";



private JTextArea displayArea;

private JPanel buttonPanel;

private JLabel statusBar; 

private Color primaryColor = new Color(103, 58, 183); 

private Color secondaryColor = new Color(255, 255, 255); 

private Color accentColor = new Color(0, 150, 136); 

private Color lightGrayColor = new Color(240, 240, 240);

private Color borderColor = new Color(200, 200, 200); 

private Font mainFont = new Font("Segoe UI", Font.PLAIN, 14); 

private Font boldFont = mainFont.deriveFont(Font.BOLD);

private Font titleFont = new Font("Segoe UI", Font.BOLD, 18); 



public StudentManagementGUI() {



setTitle("Student Management System Pro");

setSize(750, 650); 

setMinimumSize(new Dimension(600, 500)); 

setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 

setLayout(new BorderLayout(10, 10)); 

getContentPane().setBackground(secondaryColor);

try {

for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {

if ("Nimbus".equals(info.getName())) {

UIManager.setLookAndFeel(info.getClassName());

break;

}

}

} catch (Exception e) {

System.err.println("Nimbus L&F not found, using default.");

}




JLabel titleLabel = new JLabel("Student Management Dashboard", SwingConstants.CENTER);

titleLabel.setFont(titleFont);

titleLabel.setForeground(primaryColor);

titleLabel.setBorder(new EmptyBorder(15, 10, 10, 10));

add(titleLabel, BorderLayout.NORTH);

displayArea = new JTextArea();

displayArea.setEditable(false);

displayArea.setBackground(lightGrayColor);

displayArea.setFont(mainFont);

displayArea.setMargin(new Insets(10, 10, 10, 10)); 
Border lineBorder = BorderFactory.createLineBorder(borderColor);

Border emptyBorder = new EmptyBorder(10, 10, 10, 10); 

displayArea.setBorder(new CompoundBorder(lineBorder, emptyBorder));

JScrollPane scroll = new JScrollPane(displayArea);

scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

add(scroll, BorderLayout.CENTER);

buttonPanel = new JPanel(new GridLayout(2, 3, 15, 15)); 

buttonPanel.setBackground(secondaryColor);

buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); 


JButton addBtn = createStyledButton("Add Student");

JButton viewBtn = createStyledButton("View All Students");

JButton searchBtn = createStyledButton("Search Student");

JButton editBtn = createStyledButton("Edit Student");

JButton deleteBtn = createStyledButton("Delete Student");

JButton exportBtn = createStyledButton("Export to CSV");



buttonPanel.add(addBtn);

buttonPanel.add(viewBtn);

buttonPanel.add(searchBtn);

buttonPanel.add(editBtn);

buttonPanel.add(deleteBtn);

buttonPanel.add(exportBtn);

JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

bottomPanel.setBackground(secondaryColor);

bottomPanel.setBorder(new EmptyBorder(0, 10, 10, 10)); 



JButton exitBtn = createStyledButton("Exit Application");

exitBtn.setBackground(new Color(211, 47, 47)); 




statusBar = new JLabel("Welcome! Load student data or add new students.");

statusBar.setFont(mainFont.deriveFont(Font.ITALIC));

statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));

statusBar.setForeground(Color.DARK_GRAY);



JPanel southContent = new JPanel(new BorderLayout(10,10));

southContent.setBackground(secondaryColor);

southContent.add(buttonPanel, BorderLayout.CENTER);

southContent.add(exitBtn, BorderLayout.SOUTH);



bottomPanel.add(southContent, BorderLayout.CENTER);

bottomPanel.add(statusBar, BorderLayout.SOUTH);



add(bottomPanel, BorderLayout.SOUTH);
loadFromFile();

if (students.isEmpty()) {

displayArea.setText("No student records found in " + FILENAME + ".\nUse 'Add Student' to begin.");

} else {

displayStudents(); 

updateStatusBar("Loaded " + students.size() + " students from " + FILENAME);

}

addBtn.addActionListener(e -> addStudentWithPopup());

viewBtn.addActionListener(e -> displayStudents());

searchBtn.addActionListener(e -> searchStudentWithPopup());

editBtn.addActionListener(e -> editStudentWithPopup());

deleteBtn.addActionListener(e -> deleteStudentWithConfirmation());

exportBtn.addActionListener(e -> exportToCSVWithFileChooser());

exitBtn.addActionListener(e -> exitApplication());


addWindowListener(new WindowAdapter() {

@Override

public void windowClosing(WindowEvent e) {

exitApplication(); 

}

});


setLocationRelativeTo(null); 
setVisible(true);

}




private JButton createStyledButton(String text) {

JButton button = new JButton(text);

button.setFont(boldFont);

button.setForeground(secondaryColor);

button.setBackground(primaryColor);

button.setFocusPainted(false);

button.setBorder(new EmptyBorder(12, 20, 12, 20));

button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

button.setHorizontalAlignment(SwingConstants.CENTER); 





button.addMouseListener(new MouseAdapter() {

private Color originalBgColor = button.getBackground();

private Color originalFgColor = button.getForeground();



@Override

public void mouseEntered(MouseEvent e) {

button.setBackground(accentColor);

button.setForeground(secondaryColor); 

}



@Override

public void mouseExited(MouseEvent e) {

button.setBackground(originalBgColor);

button.setForeground(originalFgColor);

}

});

return button;

}

private void updateStatusBar(String message) {

statusBar.setText(message);

}


private JPanel createStudentInputPanel(String nameValue, String rollValue, String marksValue, boolean rollEditable) {

JPanel panel = new JPanel(new GridBagLayout());

GridBagConstraints gbc = new GridBagConstraints();

gbc.insets = new Insets(5, 5, 5, 5); 

gbc.anchor = GridBagConstraints.WEST; 



JTextField nameField = new JTextField(nameValue, 15); 

JTextField rollField = new JTextField(rollValue, 15);

JTextField marksField = new JTextField(marksValue, 15);



rollField.setEditable(rollEditable); 



gbc.gridx = 0; gbc.gridy = 0;

panel.add(new JLabel("Name:"), gbc);

gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

panel.add(nameField, gbc);





gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;

panel.add(new JLabel("Roll Number:"), gbc);

gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

panel.add(rollField, gbc);





gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;

panel.add(new JLabel("Marks (0-100):"), gbc);

gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

panel.add(marksField, gbc);


nameField.setName("nameField");

rollField.setName("rollField");

marksField.setName("marksField");

return panel;

}

private JTextField getTextFieldByName(JPanel panel, String name) {

for(Component comp : panel.getComponents()) {

if(comp instanceof JTextField && name.equals(comp.getName())) {

return (JTextField) comp;

}

}

return null; 
}





void addStudentWithPopup() {

JPanel inputPanel = createStudentInputPanel("", "", "", true); 


int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add New Student",

JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);



if (result == JOptionPane.OK_OPTION) {

JTextField nameField = getTextFieldByName(inputPanel, "nameField");

JTextField rollField = getTextFieldByName(inputPanel, "rollField");

JTextField marksField = getTextFieldByName(inputPanel, "marksField");



try {

String name = nameField.getText().trim();

String rollStr = rollField.getText().trim();

String marksStr = marksField.getText().trim();



if (name.isEmpty() || rollStr.isEmpty() || marksStr.isEmpty()) {

JOptionPane.showMessageDialog(this, "All fields (Name, Roll Number, Marks) are required.", "Input Error", JOptionPane.ERROR_MESSAGE);

updateStatusBar("Add failed: Missing input.");

return;

}



int roll = Integer.parseInt(rollStr);

int marks = Integer.parseInt(marksStr);



if (marks < 0 || marks > 100) {

JOptionPane.showMessageDialog(this, "Marks must be between 0 and 100.", "Input Error", JOptionPane.ERROR_MESSAGE);

updateStatusBar("Add failed: Invalid marks range.");

return;

}




for (Student existingStudent : students) {

if (existingStudent.rollNumber == roll) {

JOptionPane.showMessageDialog(this, "Student with Roll Number " + roll + " already exists.", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);

updateStatusBar("Add failed: Duplicate roll number.");

return;

}

}



Student s = new Student(name, roll, marks);

students.add(s);

displayArea.append("\nStudent added: " + s.prettyString());

scrollToBottom(); 

updateStatusBar("Student '" + s.name + "' added successfully.");

JOptionPane.showMessageDialog(this, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

saveToFile(); 

} catch (NumberFormatException e) {

JOptionPane.showMessageDialog(this, "Invalid input: Roll Number and Marks must be valid integers.", "Input Error", JOptionPane.ERROR_MESSAGE);

updateStatusBar("Add failed: Invalid number format.");

}

} else {

updateStatusBar("Add student operation cancelled.");

}

}



void displayStudents() {

if (students.isEmpty()) {

displayArea.setText("No student records available.");

updateStatusBar("Displayed all students: None found.");

} else {

StringBuilder sb = new StringBuilder("Current Student Records (" + students.size() + "):\n------------------------------------\n");

for (Student s : students) {

sb.append(s.prettyString()).append("\n");

}

displayArea.setText(sb.toString());

displayArea.setCaretPosition(0); // Scroll to top

updateStatusBar("Displayed all " + students.size() + " students.");

}

}



void searchStudentWithPopup() {

String rollStr = JOptionPane.showInputDialog(this, "Enter Roll Number to search:", "Search Student", JOptionPane.QUESTION_MESSAGE);

if (rollStr != null) {

rollStr = rollStr.trim();

if (!rollStr.isEmpty()) {

try {

int roll = Integer.parseInt(rollStr);

boolean found = false;

for (Student s : students) {

if (s.rollNumber == roll) {

displayArea.setText("Search Result:\n" + s.prettyString());

JOptionPane.showMessageDialog(this, "Student Found:\n" + s.prettyString(), "Search Result", JOptionPane.INFORMATION_MESSAGE);

updateStatusBar("Found student with Roll No: " + roll);

found = true;

break;

}

}

if (!found) {

JOptionPane.showMessageDialog(this, "Student with Roll Number " + roll + " not found.", "Search Result", JOptionPane.WARNING_MESSAGE);

displayArea.setText("Student with Roll Number " + roll + " not found.");

updateStatusBar("Student with Roll No: " + roll + " not found.");

}

} catch (NumberFormatException e) {

JOptionPane.showMessageDialog(this, "Invalid input. Please enter a numeric Roll Number.", "Input Error", JOptionPane.ERROR_MESSAGE);

updateStatusBar("Search failed: Invalid roll number format.");

}

} else {

updateStatusBar("Search cancelled: No input provided.");

}

} else {

updateStatusBar("Search operation cancelled.");

}

}



void editStudentWithPopup() {

String rollStr = JOptionPane.showInputDialog(this, "Enter Roll Number of the student to edit:", "Edit Student", JOptionPane.QUESTION_MESSAGE);

if (rollStr != null) {

rollStr = rollStr.trim();

if (!rollStr.isEmpty()) {

try {

int rollToEdit = Integer.parseInt(rollStr);

Student studentToEdit = null;

for (Student s : students) {

if (s.rollNumber == rollToEdit) {

studentToEdit = s;

break;

}

}



if (studentToEdit != null) {

JPanel editPanel = createStudentInputPanel(studentToEdit.name, String.valueOf(studentToEdit.rollNumber), String.valueOf(studentToEdit.marks), false); 



int result = JOptionPane.showConfirmDialog(this, editPanel, "Edit Details for Roll No: " + rollToEdit,

JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);



if (result == JOptionPane.OK_OPTION) {

JTextField nameField = getTextFieldByName(editPanel, "nameField");

JTextField marksField = getTextFieldByName(editPanel, "marksField");



String newName = nameField.getText().trim();

String newMarksStr = marksField.getText().trim();



boolean changed = false;

try {



if (!newName.isEmpty() && !newName.equals(studentToEdit.name)) {

studentToEdit.name = newName;

changed = true;

}





if (!newMarksStr.isEmpty()) {

int newMarks = Integer.parseInt(newMarksStr);

if (newMarks < 0 || newMarks > 100) {

JOptionPane.showMessageDialog(this, "Marks must be between 0 and 100.", "Input Error", JOptionPane.ERROR_MESSAGE);

updateStatusBar("Edit failed: Invalid marks range.");

return; 

}

if (newMarks != studentToEdit.marks) {

studentToEdit.marks = newMarks;

studentToEdit.grade = Student.calculateGrade(newMarks); 

changed = true;

}

}



if (changed) {

displayStudents(); 

updateStatusBar("Student " + rollToEdit + " updated successfully.");

JOptionPane.showMessageDialog(this, "Student details updated!", "Success", JOptionPane.INFORMATION_MESSAGE);

saveToFile();

} else {

updateStatusBar("No changes made to student " + rollToEdit + ".");

JOptionPane.showMessageDialog(this, "No changes were made.", "Edit Information", JOptionPane.INFORMATION_MESSAGE);

}



} catch (NumberFormatException e) {

JOptionPane.showMessageDialog(this, "Invalid input for Marks. Please enter a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);

updateStatusBar("Edit failed: Invalid marks format.");

}

} else {

updateStatusBar("Edit operation cancelled for Roll No: " + rollToEdit);

}



} else {

JOptionPane.showMessageDialog(this, "Student with Roll Number " + rollToEdit + " not found.", "Edit Error", JOptionPane.ERROR_MESSAGE);

displayArea.setText("Student with Roll Number " + rollToEdit + " not found.");

updateStatusBar("Edit failed: Student " + rollToEdit + " not found.");

}

} catch (NumberFormatException e) {

JOptionPane.showMessageDialog(this, "Invalid input. Please enter a numeric Roll Number.", "Input Error", JOptionPane.ERROR_MESSAGE);

updateStatusBar("Edit failed: Invalid roll number format.");

}

} else {

updateStatusBar("Edit cancelled: No input provided.");

}

} else {

updateStatusBar("Edit operation cancelled.");

}

}



void deleteStudentWithConfirmation() {

String rollStr = JOptionPane.showInputDialog(this, "Enter Roll Number of the student to delete:", "Delete Student", JOptionPane.WARNING_MESSAGE);

if (rollStr != null) {

rollStr = rollStr.trim();

if (!rollStr.isEmpty()) {

try {

int rollToDelete = Integer.parseInt(rollStr);

Iterator<Student> iterator = students.iterator();

boolean found = false;

while (iterator.hasNext()) {

Student student = iterator.next();

if (student.rollNumber == rollToDelete) {

found = true;

int confirmation = JOptionPane.showConfirmDialog(this,

"Are you sure you want to delete student:\n" + student.prettyString() + "?",

"Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);



if (confirmation == JOptionPane.YES_OPTION) {

String deletedInfo = student.prettyString(); 

iterator.remove();

displayStudents(); 

displayArea.append("\n---\nStudent deleted: " + deletedInfo);

scrollToBottom();

updateStatusBar("Student " + rollToDelete + " deleted successfully.");

JOptionPane.showMessageDialog(this, "Student deleted successfully!", "Deletion Success", JOptionPane.INFORMATION_MESSAGE);

saveToFile(); 

} else {

JOptionPane.showMessageDialog(this, "Deletion cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);

updateStatusBar("Deletion cancelled for Roll No: " + rollToDelete);

}

break; 

}

}



if (!found) {

JOptionPane.showMessageDialog(this, "Student with Roll Number " + rollToDelete + " not found.", "Deletion Error", JOptionPane.ERROR_MESSAGE);

displayArea.setText("Student with Roll Number " + rollToDelete + " not found.");

updateStatusBar("Delete failed: Student " + rollToDelete + " not found.");

}

} catch (NumberFormatException e) {

JOptionPane.showMessageDialog(this, "Invalid input. Please enter a numeric Roll Number.", "Input Error", JOptionPane.ERROR_MESSAGE);

updateStatusBar("Delete failed: Invalid roll number format.");

}

} else {

updateStatusBar("Delete cancelled: No input provided.");

}

} else {

updateStatusBar("Delete operation cancelled.");

}

}



void exportToCSVWithFileChooser() {

if (students.isEmpty()) {

JOptionPane.showMessageDialog(this, "No student data to export.", "Export Info", JOptionPane.INFORMATION_MESSAGE);

updateStatusBar("Export cancelled: No data available.");

return;

}



JFileChooser fileChooser = new JFileChooser();

fileChooser.setDialogTitle("Save Student Data as CSV");

fileChooser.setSelectedFile(new File("students_export.csv")); 
fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));



int userSelection = fileChooser.showSaveDialog(this);



if (userSelection == JFileChooser.APPROVE_OPTION) {

File fileToSave = fileChooser.getSelectedFile();



String filePath = fileToSave.getAbsolutePath();

if (!filePath.toLowerCase().endsWith(".csv")) {

fileToSave = new File(filePath + ".csv");

}



try (PrintWriter pw = new PrintWriter(fileToSave)) {

pw.println("Name,Roll Number,Marks,Grade"); 

for (Student s : students) {

pw.println(s.toString()); 
}

displayArea.append("\n---\nData successfully exported to: " + fileToSave.getName());

scrollToBottom();

updateStatusBar("Data exported successfully to " + fileToSave.getName());

JOptionPane.showMessageDialog(this, "Data exported to:\n" + fileToSave.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);

} catch (IOException e) {

displayArea.append("\n---\nExport failed: " + e.getMessage());

scrollToBottom();

updateStatusBar("Export failed: Error writing to file.");

JOptionPane.showMessageDialog(this, "Error occurred during export:\n" + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);

}

} else {

updateStatusBar("Export operation cancelled by user.");

}

}



void loadFromFile() {

students.clear(); 
File file = new File(FILENAME);

if (!file.exists()) {

updateStatusBar("Data file '" + FILENAME + "' not found. Starting fresh.");

return; 

}



int loadedCount = 0;

int skippedCount = 0;

try (Scanner sc = new Scanner(file)) {

// Skip header line if present (optional, depends on your file format)

// if (sc.hasNextLine()) sc.nextLine();



while (sc.hasNextLine()) {

String line = sc.nextLine().trim();

if (line.isEmpty() || line.startsWith("Name,Roll Number,Marks,Grade")) continue; // Skip empty lines or header



String[] parts = line.split(",");

if (parts.length == 4) { // Expect Name, Roll, Marks, Grade

String name = parts[0].trim();

try {

int roll = Integer.parseInt(parts[1].trim());

int marks = Integer.parseInt(parts[2].trim());

// Grade (parts[3]) is calculated anyway, so we don't strictly need to load it

students.add(new Student(name, roll, marks));

loadedCount++;

} catch (NumberFormatException e) {

System.err.println("Skipping invalid student data (bad number format): " + line);

skippedCount++;

} catch (ArrayIndexOutOfBoundsException e) {

System.err.println("Skipping invalid student data (missing fields): " + line);

skippedCount++;

}

} else {

System.err.println("Skipping invalid line format in file: " + line);

skippedCount++;

}

}

updateStatusBar("Loaded " + loadedCount + " students. Skipped " + skippedCount + " invalid lines.");

} catch (FileNotFoundException e) {

// Should not happen due to the file.exists() check, but handle defensively

updateStatusBar("Error: Could not find file '" + FILENAME + "' during load.");

System.err.println("Error loading file: " + e.getMessage());

}

}



void saveToFile() {

try (PrintWriter pw = new PrintWriter(FILENAME)) {
         
pw.println("Name,Roll Number,Marks,Grade"); // Write header

for (Student s : students) {

pw.println(s.toString()); // Save in CSV format

}

updateStatusBar("Data saved successfully to " + FILENAME);

System.out.println("Data saved to " + FILENAME); // Console feedback

} catch (IOException e) {

updateStatusBar("Error: Could not save data to " + FILENAME);

System.err.println("Error saving to file: " + e.getMessage());

JOptionPane.showMessageDialog(this, "Error occurred while saving data to " + FILENAME + ".", "Save Error", JOptionPane.ERROR_MESSAGE);

}

}

private void scrollToBottom() {

displayArea.setCaretPosition(displayArea.getDocument().getLength());

}



void exitApplication() {

saveToFile(); // Ensure data is saved before exiting

JOptionPane.showMessageDialog(this, "Exiting Student Management System.\nData saved.", "Exit", JOptionPane.INFORMATION_MESSAGE);

dispose(); 

System.exit(0); 
}
public static void main(String[] args) {
SwingUtilities.invokeLater(StudentManagementGUI::new);

}

}