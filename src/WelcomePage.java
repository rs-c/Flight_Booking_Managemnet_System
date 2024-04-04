import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class WelcomePage extends JFrame {
    private static final String FLIGHTS_FILE = "flights.txt";
    private static final String BOOKINGS_FILE = "bookings.txt";

    private HashMap<String, Flight> flightsMap; // Store flights using flightNo as key
    private HashMap<String, Booking> bookingsMap; // Store bookings using passportNo as key

    public WelcomePage() {
        setTitle("Flight Booking System");
        setSize(1000, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        flightsMap = new HashMap<>();
        bookingsMap = new HashMap<>();

        loadFlightsFromFile();
        loadBookingsFromFile();

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(Color.GRAY);

        // Title Label
        JLabel titleLabel = new JLabel("Welcome to Flight Booking System", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        welcomePanel.add(titleLabel, BorderLayout.NORTH);

        // Image
        ImageIcon imageIcon = new ImageIcon("C:\\Users\\rahul\\Winter 24\\flight\\src\\Main.png");
        JLabel imageLabel = new JLabel(imageIcon);
        welcomePanel.add(imageLabel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 10, 0)); // 1 row, 3 columns
        JButton addFlightButton = new JButton("Add Flight");
        JButton makeBookingButton = new JButton("Make a Booking");
        JButton viewBookingsButton = new JButton("View Bookings");

        addFlightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAddFlightFrame();
            }
        });

        makeBookingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openMakeBookingFrame();
            }
        });

        viewBookingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openViewBookingsFrame();
            }
        });

        buttonsPanel.add(addFlightButton);
        buttonsPanel.add(makeBookingButton);
        buttonsPanel.add(viewBookingsButton);

        welcomePanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(welcomePanel);
        setVisible(true);
    }

    private void loadFlightsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FLIGHTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String flightNo = parts[0].trim();
                    String from = parts[1].trim();
                    String to = parts[2].trim();
                    double farePerSeat = Double.parseDouble(parts[3].trim());
                    flightsMap.put(flightNo, new Flight(flightNo, from, to, farePerSeat));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void loadBookingsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    String passportNo = parts[0].trim();
                    String flightNo = parts[1].trim();
                    String name = parts[2].trim();
                    boolean hasMeal = Boolean.parseBoolean(parts[3].trim());
                    boolean hasWindowSeat = Boolean.parseBoolean(parts[4].trim());
                    double totalAmount = Double.parseDouble(parts[5].trim());
                    Flight flight = flightsMap.get(flightNo);
                    if (flight != null) {
                        Booking booking = new Booking(flight, passportNo, name, hasMeal, hasWindowSeat, totalAmount);
                        bookingsMap.put(passportNo, booking);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void saveFlightsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FLIGHTS_FILE))) {
            for (Flight flight : flightsMap.values()) {
                writer.println(flight.getFlightNo() + "," + flight.getFrom() + "," + flight.getTo() + "," + flight.getFarePerSeat());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBookingsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking booking : bookingsMap.values()) {
                writer.println(booking.getPassportNo() + "," + booking.getFlight().getFlightNo() + ","
                        + booking.getName() + "," + booking.isHasMeal() + "," + booking.isHasWindowSeat()
                        + "," + booking.getTotalAmount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openAddFlightFrame() {
        JFrame addFlightFrame = new JFrame("Add Flight");
        addFlightFrame.setSize(400, 300);
        addFlightFrame.setDefaultCloseOperation(2);

        JPanel addFlightPanel = new JPanel(new GridLayout(5, 2));

        JTextField flightNoField = new JTextField();
        JTextField fromField = new JTextField();
        JTextField toField = new JTextField();
        JTextField fareField = new JTextField();

        addFlightPanel.add(new JLabel("Flight No: "));
        addFlightPanel.add(flightNoField);
        addFlightPanel.add(new JLabel("From: "));
        addFlightPanel.add(fromField);
        addFlightPanel.add(new JLabel("To: "));
        addFlightPanel.add(toField);
        addFlightPanel.add(new JLabel("Fare per Seat: "));
        addFlightPanel.add(fareField);

        JButton addButton = new JButton("Add Flight");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String flightNo = flightNoField.getText();
                String from = fromField.getText();
                String to = toField.getText();
                double farePerSeat = Double.parseDouble(fareField.getText());

                Flight flight = new Flight(flightNo, from, to, farePerSeat);
                flightsMap.put(flightNo, flight);
                saveFlightsToFile(); // Save flight to file

                JOptionPane.showMessageDialog(null, "Flight added successfully!");
                addFlightFrame.dispose(); // Close the add flight frame
            }
        });

        addFlightPanel.add(addButton);

        addFlightFrame.add(addFlightPanel);
        addFlightFrame.setVisible(true);
    }

    private void openMakeBookingFrame() {
        JFrame makeBookingFrame = new JFrame("Make a Booking");
        makeBookingFrame.setSize(400, 300);
        makeBookingFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel makeBookingPanel = new JPanel(new GridLayout(9, 2));

        JComboBox<String> flightsComboBox = new JComboBox<>(flightsMap.keySet().toArray(new String[0]));
        JTextField passportField = new JTextField();
        JTextField nameField = new JTextField();

        makeBookingPanel.add(new JLabel("Select Flight: "));
        makeBookingPanel.add(flightsComboBox);
        makeBookingPanel.add(new JLabel("Passport No: "));
        makeBookingPanel.add(passportField);
        makeBookingPanel.add(new JLabel("Name: "));
        makeBookingPanel.add(nameField);

        JCheckBox mealCheckBox = new JCheckBox("Meal (+$50)");
        JCheckBox windowSeatCheckBox = new JCheckBox("Window Seat (+$30)");

        makeBookingPanel.add(mealCheckBox);
        makeBookingPanel.add(windowSeatCheckBox);

        JLabel fromLabel = new JLabel("From:");
        JTextField fromField = new JTextField();
        fromField.setEditable(false);
        JLabel toLabel = new JLabel("To:");
        JTextField toField = new JTextField();
        toField.setEditable(false);
        JLabel fareLabel = new JLabel("Basic Fare:");
        JTextField fareField = new JTextField();
        fareField.setEditable(false);

        flightsComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedFlightNo = (String) flightsComboBox.getSelectedItem();
                if (selectedFlightNo != null) {
                    Flight selectedFlight = flightsMap.get(selectedFlightNo);
                    fromField.setText(selectedFlight.getFrom());
                    toField.setText(selectedFlight.getTo());
                    fareField.setText(String.valueOf(selectedFlight.getFarePerSeat()));
                }
            }
        });

        makeBookingPanel.add(fromLabel);
        makeBookingPanel.add(fromField);
        makeBookingPanel.add(toLabel);
        makeBookingPanel.add(toField);
        makeBookingPanel.add(fareLabel);
        makeBookingPanel.add(fareField);

        JButton bookButton = new JButton("Book");
        bookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedFlightNo = (String) flightsComboBox.getSelectedItem();
                Flight selectedFlight = flightsMap.get(selectedFlightNo);

                String passportNo = passportField.getText();
                String name = nameField.getText();

                boolean hasMeal = mealCheckBox.isSelected();
                boolean hasWindowSeat = windowSeatCheckBox.isSelected();

                double totalAmount = selectedFlight.getFarePerSeat();
                if (hasMeal) {
                    totalAmount += 50.0;
                }
                if (hasWindowSeat) {
                    totalAmount += 30.0;
                }

                Booking booking = new Booking(selectedFlight, passportNo, name, hasMeal, hasWindowSeat, totalAmount);
                bookingsMap.put(passportNo, booking);
                saveBookingsToFile(); // Save booking to file

                JOptionPane.showMessageDialog(null, "Booking successful!\nTotal Amount: $" + totalAmount);
                makeBookingFrame.dispose(); // Close the make booking frame
            }
        });

        makeBookingPanel.add(bookButton);

        makeBookingFrame.add(makeBookingPanel);
        makeBookingFrame.setVisible(true);
    }

    private void openViewBookingsFrame() {
        JFrame viewBookingsFrame = new JFrame("View Bookings");
        viewBookingsFrame.setSize(600, 400);
        viewBookingsFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel viewBookingsPanel = new JPanel(new BorderLayout());

        JTextArea bookingsTextArea = new JTextArea(20, 40);
        bookingsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(bookingsTextArea);

        JButton viewDetailsButton = new JButton("View Details");
        JTextField passportNoField = new JTextField(20);
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter Passport No: "));
        inputPanel.add(passportNoField);
        inputPanel.add(viewDetailsButton);

        viewDetailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String passportNo = passportNoField.getText();
                Booking booking = bookingsMap.get(passportNo);
                if (booking != null) {
                    bookingsTextArea.setText(booking.toString());
                } else {
                    bookingsTextArea.setText("Booking not found for Passport No: " + passportNo);
                }
            }
        });

        viewBookingsPanel.add(inputPanel, BorderLayout.NORTH);
        viewBookingsPanel.add(scrollPane, BorderLayout.CENTER);

        viewBookingsFrame.add(viewBookingsPanel);
        viewBookingsFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomePage());
    }
}

class Flight {
    private String flightNo;
    private String from;
    private String to;
    private double farePerSeat;

    public Flight(String flightNo, String from, String to, double farePerSeat) {
        this.flightNo = flightNo;
        this.from = from;
        this.to = to;
        this.farePerSeat = farePerSeat;
    }

    public String getFlightNo() {
        return flightNo;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getFarePerSeat() {
        return farePerSeat;
    }

    @Override
    public String toString() {
        return "Flight No: " + flightNo + ", From: " + from + ", To: " + to + ", Fare per Seat: $" + farePerSeat;
    }
}

class Booking {
    private Flight flight;
    private String passportNo;
    private String name;
    private boolean hasMeal;
    private boolean hasWindowSeat;
    private double totalAmount;

    public Booking(Flight flight, String passportNo, String name, boolean hasMeal, boolean hasWindowSeat, double totalAmount) {
        this.flight = flight;
        this.passportNo = passportNo;
        this.name = name;
        this.hasMeal = hasMeal;
        this.hasWindowSeat = hasWindowSeat;
        this.totalAmount = totalAmount;
    }

    public Flight getFlight() {
        return flight;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public String getName() {
        return name;
    }

    public boolean isHasMeal() {
        return hasMeal;
    }

    public boolean isHasWindowSeat() {
        return hasWindowSeat;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return "Flight: " + flight.toString() + "\nPassport No: " + passportNo + "\nName: " + name
                + "\nMeal: " + (hasMeal ? "Yes" : "No") + "\nWindow Seat: " + (hasWindowSeat ? "Yes" : "No")
                + "\nTotal Amount: $" + totalAmount;
    }
}
