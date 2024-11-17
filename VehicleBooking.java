import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.Desktop;
import java.net.URI;

class Vehicle implements Serializable {
    private String make;
    private String model;
    private int year;
    private boolean isAvailable;
    private double dailyRate;
    private String type;
    private String registrationNumber;
    private int totalSeats;
    private String acStatus;
    private double mileage;

    public Vehicle(String make, String model, int year, double dailyRate, String type, String registrationNumber, int totalSeats, String acStatus, double mileage) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.dailyRate = dailyRate;
        this.isAvailable = true;
        this.type = type;
        this.registrationNumber = registrationNumber;
        this.totalSeats = totalSeats;
        this.acStatus = acStatus;
        this.mileage = mileage;
    }

    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public double getDailyRate() { return dailyRate; }
    public boolean isAvailable() { return isAvailable; }
    public String getType() { return type; }
    public String getRegistrationNumber() { return registrationNumber; }
    public int getTotalSeats() { return totalSeats; }
    public String getAcStatus() { return acStatus; }
    public double getMileage() { return mileage; }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return make + " " + model + " (" + year + ") - $" + dailyRate + "/day";
    }
}

class VehicleInventory implements Serializable {
    private ArrayList<Vehicle> vehicles = new ArrayList<>();

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicles.remove(vehicle);
    }

    public ArrayList<Vehicle> getAvailableVehicles() {
        ArrayList<Vehicle> availableVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.isAvailable()) {
                availableVehicles.add(vehicle);
            }
        }
        return availableVehicles;
    }

    public ArrayList<Vehicle> getAllVehicles() {
        return vehicles;
    }

    // Save the vehicle list to a file
    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("vehicles.dat"))) {
            oos.writeObject(vehicles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the vehicle list from a file
    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("vehicles.dat"))) {
            vehicles = (ArrayList<Vehicle>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class BookingSystem {
    private VehicleInventory inventory = new VehicleInventory();
    private ArrayList<Vehicle> bookedVehicles = new ArrayList<>();
    private int rentalDays = 0;

    public void addVehicleToInventory(Vehicle vehicle) {
        inventory.addVehicle(vehicle);
    }

    public void removeVehicleFromInventory(Vehicle vehicle) {
        inventory.removeVehicle(vehicle);
    }

    public ArrayList<Vehicle> getAvailableVehicles() {
        return inventory.getAvailableVehicles();
    }

    public boolean bookVehicles(List<Vehicle> vehicles, int days) {
        boolean allAvailable = true;

        for (Vehicle vehicle : vehicles) {
            if (vehicle.isAvailable()) {
                vehicle.setAvailable(false);
                bookedVehicles.add(vehicle);
            } else {
                allAvailable = false;
            }
        }

        rentalDays = days;
        return allAvailable;
    }

    public ArrayList<Vehicle> getBookedVehicles() {
        return bookedVehicles;
    }

    public double calculateTotalCost() {
        double total = 0;
        for (Vehicle vehicle : bookedVehicles) {
            total += vehicle.getDailyRate() * rentalDays;
        }
        return total;
    }

    public void resetBooking() {
        for (Vehicle vehicle : bookedVehicles) {
            vehicle.setAvailable(true);
        }
        bookedVehicles.clear();
    }

    public ArrayList<Vehicle> getAllVehicles() {
        return inventory.getAllVehicles();
    }

    public void saveInventory() {
        inventory.saveToFile();
    }

    public void loadInventory() {
        inventory.loadFromFile();
    }
}

public class VehicleBooking extends JFrame {
    private BookingSystem bookingSystem = new BookingSystem();
    private JTextArea billingDisplayArea;
    private JTextField daysField;
    private JLabel totalCostLabel;
    private JLabel totalPaymentLabel;
    private JTabbedPane tabbedPane;
    private DefaultListModel<Vehicle> vehicleListModel;
    private JPanel vehiclePanel;

    public VehicleBooking() {
        setTitle("Vehicle Booking System");

        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(this);

        bookingSystem.loadInventory();

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(173, 216, 230));

        tabbedPane.add("Vehicles", createCombinedVehicleTab());
        tabbedPane.add("Billing", createBillingTab());
        tabbedPane.add("Payment", createPaymentTab());
        tabbedPane.add("Add New Vehicle", createVehicleRegistrationTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);

        JButton closeButton = new JButton("X");
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        closeButton.addActionListener(e -> System.exit(0));

        JButton minimizeButton = new JButton("-");
        minimizeButton.setBackground(Color.YELLOW);
        minimizeButton.setForeground(Color.WHITE);
        minimizeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        minimizeButton.addActionListener(e -> setState(Frame.ICONIFIED));

        JButton maximizeButton = new JButton("+");
        maximizeButton.setBackground(Color.GREEN);
        maximizeButton.setForeground(Color.WHITE);
        maximizeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        maximizeButton.addActionListener(e -> {
            if (getExtendedState() == Frame.MAXIMIZED_BOTH) {
                setExtendedState(Frame.NORMAL);
            } else {
                setExtendedState(Frame.MAXIMIZED_BOTH);
            }
        });

        controlPanel.add(minimizeButton);
        controlPanel.add(maximizeButton);
        controlPanel.add(closeButton);

        return controlPanel;
    }

    private JPanel createCombinedVehicleTab() {
        JPanel panel = new JPanel(new BorderLayout());

        vehicleListModel = new DefaultListModel<>();
        JList<Vehicle> vehicleList = new JList<>(vehicleListModel);
        JScrollPane vehicleListScroll = new JScrollPane(vehicleList);

        refreshVehicleList();

        JPanel vehicleButtonPanel = new JPanel(new GridLayout(0, 1, 10, 10));

        JButton addButton = new JButton("Add Vehicle");
        JButton deleteButton = new JButton("Delete Vehicle");
        JButton bookButton = new JButton("Book Vehicle");

        addButton.addActionListener(e -> {
            tabbedPane.setSelectedIndex(3);
        });

        deleteButton.addActionListener(e -> {
            Vehicle selectedVehicle = vehicleList.getSelectedValue();
            if (selectedVehicle != null) {
                bookingSystem.removeVehicleFromInventory(selectedVehicle);
                bookingSystem.saveInventory();
                refreshVehicleList();
                JOptionPane.showMessageDialog(null, "Vehicle removed from inventory.");
            } else {
                JOptionPane.showMessageDialog(null, "Please select a vehicle to delete.");
            }
        });

        bookButton.addActionListener(e -> {
            List<Vehicle> selectedVehicles = vehicleList.getSelectedValuesList();
            try {
                int rentalDays = Integer.parseInt(daysField.getText());
                if (rentalDays <= 0) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid number of rental days.");
                    return;
                }

                if (bookingSystem.bookVehicles(selectedVehicles, rentalDays)) {
                    JOptionPane.showMessageDialog(null, "Vehicles booked successfully!");
                    double totalCost = bookingSystem.calculateTotalCost();
                    billingDisplayArea.setText("Booked Vehicles:\n");
                    for (Vehicle vehicle : selectedVehicles) {
                        billingDisplayArea.append(vehicle + "\n");
                    }
                    totalCostLabel.setText("Total Cost: $" + totalCost);
                } else {
                    JOptionPane.showMessageDialog(null, "Some vehicles are not available.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number of rental days.");
            }
        });

        vehicleButtonPanel.add(addButton);
        vehicleButtonPanel.add(deleteButton);
        vehicleButtonPanel.add(bookButton);

        panel.add(vehicleButtonPanel, BorderLayout.WEST);
        panel.add(vehicleListScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBillingTab() {
        JPanel panel = new JPanel(new BorderLayout());

        billingDisplayArea = new JTextArea(10, 30);
        billingDisplayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(billingDisplayArea);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JLabel rentalDaysLabel = new JLabel("Rental Days: ");
        daysField = new JTextField(5);
        totalCostLabel = new JLabel("Total Cost: $0");

        bottomPanel.add(rentalDaysLabel);
        bottomPanel.add(daysField);
        bottomPanel.add(totalCostLabel);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }
