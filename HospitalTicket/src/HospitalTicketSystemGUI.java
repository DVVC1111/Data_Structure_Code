import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
public class HospitalTicketSystemGUI {
    private static final String RED_TICKET_FILE = "redTicketPatients.json";
    private static final String BLUE_TICKET_FILE = "blueTicketPatients.json";
    private int redPatientOrder = 1;
    private static int currentID = 0;

    private JFrame frame;
    private JTextField nameField, conditionField, ticketTypeField, patientIDField;
    private JTextArea textArea;
    private JRadioButton redTicketRadioButton;
    private JRadioButton blueTicketRadioButton;
    private JPanel ticketDisplayPanel;


    public HospitalTicketSystemGUI() {
        initializeUI();
    }
    private void initializeUI() {
        frame = new JFrame("Hospital Ticket System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 700); // Adjust size if necessary
        frame.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Condition:"));
        conditionField = new JTextField();
        inputPanel.add(conditionField);

        inputPanel.add(new JLabel("Ticket Type:"));

        blueTicketRadioButton = new JRadioButton("Blue", true); // Blue selected by default
        redTicketRadioButton = new JRadioButton("Red");
        
       

        // Group the radio buttons so only one can be selected at a time
        ButtonGroup group = new ButtonGroup();
        group.add(blueTicketRadioButton);
        group.add(redTicketRadioButton);
       
        inputPanel.add(blueTicketRadioButton);
        inputPanel.add(redTicketRadioButton);
        

        inputPanel.add(new JLabel("Patient ID (for completion):"));
        patientIDField = new JTextField();
        inputPanel.add(patientIDField);

        JButton addButton = new JButton("Add Patient");
        addButton.setMargin(new Insets(5, 15, 5, 15)); // Add margin to the button
        addButton.addActionListener(new AddPatientActionListener());
        inputPanel.add(addButton);

        JButton completeButton = new JButton("Complete Blue Patient");
        completeButton.setMargin(new Insets(5, 15, 5, 15));
        completeButton.addActionListener(new CompletePatientActionListener());
        inputPanel.add(completeButton);
        
        JButton completeRedButton = new JButton("Complete Red Patient");
        completeRedButton.setMargin(new Insets(5, 15, 5, 15));
        completeRedButton.addActionListener(new CompleteRedPatientActionListener());
        inputPanel.add(completeRedButton);

        JButton showButton = new JButton("Show Patients");
        showButton.setMargin(new Insets(5, 15, 5, 15));
        showButton.addActionListener(new ShowPatientsActionListener());
        inputPanel.add(showButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);



        ticketDisplayPanel = new JPanel();
        ticketDisplayPanel.setPreferredSize(new Dimension(400, 100)); // Set the size of the ticket display
        frame.add(ticketDisplayPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
    
    
    


    class AddPatientActionListener implements ActionListener {
    	@Override
        public void actionPerformed(ActionEvent e) {
            if (redTicketRadioButton.isSelected()) {
                addRedTicketPatient();
            } else {
            	String name = nameField.getText();
                String condition = conditionField.getText();
                String ticketType = redTicketRadioButton.isSelected() ? "Red" : "Blue";
                
                int patientID = getNextPatientID();
                JSONObject patient = new JSONObject();
                
                
                patient.put("id", patientID);
                patient.put("name", name);
                patient.put("condition", condition);
                patient.put("ticketType", ticketType);
        
                String fileName = ticketType.equalsIgnoreCase("Red") ? RED_TICKET_FILE : BLUE_TICKET_FILE;
                savePatientToFile(patient, fileName);
        
                textArea.setText("Patient added: ID " + patientID + ", Name: " + name + ", Condition: " + condition);
               

                updateTicketDisplay(patientID, ticketType);
                clearInputFields();
            }
        }
//    	private int getMaxRedPatientOrder() {
//    	    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("redTicketPatients.dat"))) {
//    	        int maxOrder = 0;
//
//    	        while (true) {
//    	            try {
//    	                Patient redTicketPatient = (Patient) ois.readObject();
//    	                maxOrder = Math.max(maxOrder, redTicketPatient.getOrder());
//    	            } catch (EOFException e) {
//    	                // End of file reached
//    	                break;
//    	            }
//    	        }
//
//    	        return maxOrder;
//    	    } catch (IOException | ClassNotFoundException e) {
//    	        System.err.println("Error: Unable to read red ticket patients from file.");
//    	        e.printStackTrace();
//    	        return 0;
//    	    }
//    	}
    	private void addRedTicketPatient() {
    	    String name = nameField.getText();
    	    String condition = conditionField.getText();

    	    // Use the Patient class to represent the patient
    	    int patientID = getNextPatientID();
    	    Patient patient = new Patient(patientID, name, condition, "Red", 0);

    	    try {
    	        // Load existing list from the .dat file
    	        List<Patient> redTicketPatientList = loadRedTicketPatientsFromFile();

    	        // Prompt user to choose seriousness level
    	        String[] options = {"Urgent Conditions", "Life-Threatening", "Emergency"};
    	        int selectedOption = JOptionPane.showOptionDialog(frame,
    	                "Choose the seriousness level for the red ticket patient:",
    	                "Seriousness Level",
    	                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
    	                null, options, options[0]);

    	        // Set the seriousness attribute based on user's choice
    	        int seriousness = selectedOption + 1;
    	        patient.setSeriousness(seriousness);

    	        // Add the new patient to the list
    	        redTicketPatientList.add(patient);

    	        // Save the updated list of patients to the .dat file
    	        saveRedTicketPatientsToFile(redTicketPatientList);

    	        textArea.setText("Red Ticket Patient added: " + patient);

    	        updateTicketDisplay(patientID, "Red");
    	        clearInputFields();
    	    } catch (IOException | ClassNotFoundException e) {
    	        System.err.println("Error: Unable to add red ticket patient.");
    	        e.printStackTrace();
    	    }
    	}

    	private List<Patient> loadRedTicketPatientsFromFile() throws IOException, ClassNotFoundException {
    	    File file = new File("redTicketPatients.dat");

    	    if (file.exists()) {
    	        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
    	            List<Patient> redTicketPatientList = new ArrayList<>();

    	            // Read all existing patients from the .dat file
    	            while (true) {
    	                try {
    	                    Patient redTicketPatient = (Patient) ois.readObject();
    	                    redTicketPatientList.add(redTicketPatient);
    	                } catch (EOFException e) {
    	                    // End of file reached
    	                    break;
    	                }
    	            }

    	            return redTicketPatientList;
    	        }
    	    } else {
    	        // If the file does not exist, return an empty list
    	        return new ArrayList<>();
    	    }
    	}

    	private void saveRedTicketPatientsToFile(List<Patient> redTicketPatientList) {
    	    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("redTicketPatients.dat"))) {
    	        // Write each patient object to the .dat file
    	        for (Patient patient : redTicketPatientList) {
    	            oos.writeObject(patient);
    	        }
    	        oos.flush();
    	    } catch (IOException e) {
    	        System.err.println("Error: Unable to save red ticket patients to file.");
    	        e.printStackTrace();
    	    }
    	}

    }
    private class CompleteRedPatientActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	boolean result = removeRedPatientFromFile();
        	if (result) {
                textArea.setText("The highest priority red ticket patient currently has been removed.");
            } else {
                textArea.setText("There are no red ticket patient currently.");
            }
        }
    }
    

    // Modify the CompletePatientActionListener class
    class CompletePatientActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int id = Integer.parseInt(patientIDField.getText());

                boolean result = removePatientFromFile(id, BLUE_TICKET_FILE);
                
                if (result) {
                    textArea.setText("Blue Ticket Patient with ID " + id + " has been completed.");
                } else {
                    textArea.setText("No blue ticket patient found with ID " + id + ".");
                }

            } catch (NumberFormatException ex) {
                textArea.setText("Invalid patient ID format.");
            }
            clearInputFields();
        }
    }

    

    class ShowPatientsActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Red Ticket Patients:\n");
            sb.append(showRedTicketPatientsFromFile());
            sb.append("\nBlue Ticket Patients:\n");
            sb.append(showPatientsFromFile(BLUE_TICKET_FILE, false));
            textArea.setText(sb.toString());
        }
        private String showRedTicketPatientsFromFile() {
            StringBuilder sb = new StringBuilder();

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("redTicketPatients.dat"))) {
                // Read patient objects until the end of the file is reached
                while (true) {
                    try {
                        Patient redTicketPatient = (Patient) ois.readObject();
                        sb.append("ID ").append(redTicketPatient.getId())
                                .append(", Name: ").append(redTicketPatient.getName())
                                .append(", Condition: ").append(redTicketPatient.getCondition())
                                .append(", Ticket Type: ").append(redTicketPatient.getTicketType())
                                .append(", Seriousness: ").append(redTicketPatient.getSeriousness())
                                .append("\n");
                    } catch (EOFException e) {
                        // End of file reached
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                sb.append("Error: Unable to load red ticket patients from file.\n");
                e.printStackTrace();
            }

            return sb.toString();
        }




        private String showPatientsFromFile(String fileName, boolean includeOrder) {
            JSONParser parser = new JSONParser();
            StringBuilder sb = new StringBuilder();

            try (FileReader reader = new FileReader(fileName)) {
                Object obj = parser.parse(reader);
                if (obj instanceof JSONArray) {
                    JSONArray patientsArray = (JSONArray) obj;
                    for (Object patientObj : patientsArray) {
                        JSONObject patient = (JSONObject) patientObj;
                        int id = ((Long) patient.get("id")).intValue();
                        String name = (String) patient.get("name");
                        String condition = (String) patient.get("condition");
                        String ticketType = (String) patient.get("ticketType");
                        int order = includeOrder && ticketType.equalsIgnoreCase("Red") ? ((Long) patient.get("order")).intValue() : -1;

                        sb.append("ID ").append(id)
                          .append(", Name: ").append(name)
                          .append(", Condition: ").append(condition)
                          .append(", Ticket Type: ").append(ticketType);

                        if (order != -1) {
                            sb.append(", Order: ").append(order);
                        }
                        
                        // Display the seriousness attribute if it exists
                        if (patient.containsKey("seriousness")) {
                            int seriousness = ((Long) patient.get("seriousness")).intValue();
                            sb.append(", Seriousness: ").append(seriousness);
                        }

                        sb.append("\n");
                    }
                }
            } catch (IOException | ParseException e) {
                sb.append("Error: Unable to load patients from file.\n");
            }

            return sb.toString();
        }
    }


    // Existing methods (savePatientToFile, removePatientFromFile, showPatientsFromFile) go here

    @SuppressWarnings("unchecked")
    private static void savePatientToFile(JSONObject patient, String fileName) {
        JSONParser parser = new JSONParser();
        JSONArray patientsArray = new JSONArray();

        try (FileReader reader = new FileReader(fileName)) {
            Object obj = parser.parse(reader);
            if (obj instanceof JSONArray) {
                patientsArray = (JSONArray) obj;
            }
        } catch (IOException | ParseException e) {
            // File might not exist or is empty. Proceed to create new.
        }

        patientsArray.add(patient);

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(patientsArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.out.println("Error: Unable to save patient to file.");
            e.printStackTrace();
        }
    }

    private static boolean removePatientFromFile(int patientID, String fileName) {
        JSONParser parser = new JSONParser();
        JSONArray patientsArray = new JSONArray();
        JSONArray updatedPatientsArray = new JSONArray();
        boolean patientFound = false;
    
        try (FileReader reader = new FileReader(fileName)) {
            Object obj = parser.parse(reader);
            if (obj instanceof JSONArray) {
                patientsArray = (JSONArray) obj;
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error: Unable to load patients from file.");
            e.printStackTrace();
            return false;
        }
    
        for (Object patientObj : patientsArray) {
            JSONObject patient = (JSONObject) patientObj;
            int id = ((Long) patient.get("id")).intValue();
            if (id == patientID) {
                patientFound = true;
            } else {
                updatedPatientsArray.add(patient);
            }
        }
    
        if (patientFound) {
            try (FileWriter file = new FileWriter(fileName)) {
                file.write(updatedPatientsArray.toJSONString());
                file.flush();
            } catch (IOException e) {
                System.out.println("Error: Unable to update patients in file.");
                e.printStackTrace();
                return false;
            }
        }
        
        return patientFound;
    }
    
    private boolean removeRedPatientFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("redTicketPatients.dat"))) {
            // Create a priority queue with a custom comparator based on seriousness
            PriorityQueue<Patient> redTicketPatientQueue = new PriorityQueue<>(
                    Comparator.comparingInt(Patient::getSeriousness).reversed()
            );

            // Read patient objects until the end of the file is reached
            while (true) {
                try {
                    Patient redTicketPatient = (Patient) ois.readObject();
                    redTicketPatientQueue.add(redTicketPatient);
                } catch (EOFException e) {
                    // End of file reached
                    break;
                }
            }

            // Poll one patient from the priority queue
            if (!redTicketPatientQueue.isEmpty()) {
                Patient removedPatient = redTicketPatientQueue.poll();
                System.out.println("Removed patient from queue: " + removedPatient);
            }

            // Save the remaining patients back to the .dat file using the existing function
            saveRedTicketPatientsToFile(new ArrayList<>(redTicketPatientQueue));

            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: Unable to remove red ticket patients from file.");
            e.printStackTrace();
            return false;
        }
    }



    private void saveRedTicketPatientsToFile(List<Patient> redTicketPatientList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("redTicketPatients.dat"))) {
            // Write each patient object to the .dat file
            for (Patient patient : redTicketPatientList) {
                oos.writeObject(patient);
            }
            oos.flush();
        } catch (IOException e) {
            System.err.println("Error: Unable to save red ticket patients to file.");
            e.printStackTrace();
        }
    }



    
    private String showPatientsFromFile(String fileName) {
        JSONParser parser = new JSONParser();
        StringBuilder sb = new StringBuilder();
    
        try (FileReader reader = new FileReader(fileName)) {
            Object obj = parser.parse(reader);
            if (obj instanceof JSONArray) {
                JSONArray patientsArray = (JSONArray) obj;
                for (Object patientObj : patientsArray) {
                    JSONObject patient = (JSONObject) patientObj;
                    int id = ((Long) patient.get("id")).intValue();
                    String name = (String) patient.get("name");
                    String condition = (String) patient.get("condition");
    
                    sb.append("ID ").append(id)
                      .append(", Name: ").append(name)
                      .append(", Condition: ").append(condition).append("\n");
                }
            }
        } catch (IOException | ParseException e) {
            sb.append("Error: Unable to load patients from file.\n");
        }
    
        return sb.toString();
    }
   

    

    private void clearInputFields() {
        nameField.setText("");
        conditionField.setText("");
        ticketTypeField.setText("");
        patientIDField.setText("");
    }
    
    private void updateTicketDisplay(int patientID, String ticketType) {
        // Remove the existing ticket display panel, if any
        if (ticketDisplayPanel != null) {
            frame.remove(ticketDisplayPanel);
        }
    
        // Create a new ticket display panel
        ticketDisplayPanel = new JPanel();
        ticketDisplayPanel.setLayout(new BorderLayout());
        ticketDisplayPanel.setPreferredSize(new Dimension(400, 100)); // Set the size of the ticket display
        ticketDisplayPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    
        Color ticketColor = ticketType.equalsIgnoreCase("Red") ? Color.RED : Color.BLUE;
        ticketDisplayPanel.setBackground(ticketColor);
    
        JLabel ticketLabel = new JLabel("Ticket ID: " + patientID, SwingConstants.CENTER);
        ticketLabel.setForeground(Color.WHITE);
        ticketLabel.setFont(new Font("Arial", Font.BOLD, 20));
        ticketDisplayPanel.add(ticketLabel);
    
        // Add the new panel to the frame
        frame.add(ticketDisplayPanel, BorderLayout.SOUTH);
    
        // Refresh the frame
        frame.revalidate();
        frame.repaint();
    }
    
    
    


    private static int getNextPatientID() {
        int maxID = currentID;
        maxID = Math.max(maxID, getMaxIDFromObj("redTicketPatients.dat"));
        maxID = Math.max(maxID, getMaxIDFromJSON(BLUE_TICKET_FILE));
        return maxID + 1;
    }
    
    private static int getMaxIDFromObj(String fileName) {
        int maxID = 0;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            while (true) {
                try {
                    Patient patient = (Patient) ois.readObject();
                    maxID = Math.max(maxID, patient.getId());
                } catch (EOFException e) {
                    // End of file reached
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Handle exceptions or return 0
            e.printStackTrace();
        }
        return maxID;
    }
    
    private static int getMaxIDFromJSON(String fileName) {
        JSONParser parser = new JSONParser();
        int maxID = 0;
        try (FileReader reader = new FileReader(fileName)) {
            Object obj = parser.parse(reader);
            if (obj instanceof JSONArray) {
                JSONArray patientsArray = (JSONArray) obj;
                for (Object patientObj : patientsArray) {
                    JSONObject patient = (JSONObject) patientObj;
                    int id = ((Long) patient.get("id")).intValue();
                    maxID = Math.max(maxID, id);
                }
            }
        } catch (IOException | ParseException e) {
            // Handle exceptions or return 0
        }
        return maxID;
    }

    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HospitalTicketSystemGUI());
    }
}
