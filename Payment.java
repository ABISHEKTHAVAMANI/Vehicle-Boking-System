private JPanel createPaymentTab() {
    JPanel panel = new JPanel(new BorderLayout());

    totalPaymentLabel = new JLabel("Total Payment: $0");
    panel.add(totalPaymentLabel, BorderLayout.NORTH);

    JButton payButton = new JButton("Pay Now");
    payButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            double totalCost = bookingSystem.calculateTotalCost();
            totalPaymentLabel.setText("Total Payment: $" + totalCost);

            // Google Pay link
            String googlePayLink = "https://pay.google.com";
            try {
                // Open the Google Pay link in the default browser
                Desktop.getDesktop().browse(new URI(googlePayLink));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error opening Google Pay link: " + ex.getMessage());
            }
        }
    });

    panel.add(payButton, BorderLayout.CENTER);

    return panel;
}

private JPanel createVehicleRegistrationTab() {
    JPanel panel = new JPanel();

    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JTextField makeField = new JTextField();
    JTextField modelField = new JTextField();
    JTextField yearField = new JTextField();
    JTextField dailyRateField = new JTextField();
    JTextField typeField = new JTextField();
    JTextField registrationField = new JTextField();
    JTextField seatsField = new JTextField();
    JTextField acField = new JTextField();
    JTextField mileageField = new JTextField();

    panel.add(new JLabel("Make"));
    panel.add(makeField);
    panel.add(new JLabel("Model"));
    panel.add(modelField);
    panel.add(new JLabel("Year"));
    panel.add(yearField);
    panel.add(new JLabel("Daily Rate"));
    panel.add(dailyRateField);
    panel.add(new JLabel("Type"));
    panel.add(typeField);
    panel.add(new JLabel("Registration"));
    panel.add(registrationField);
    panel.add(new JLabel("Total Seats"));
    panel.add(seatsField);
    panel.add(new JLabel("A/C or Non A/C"));
    panel.add(acField);
    panel.add(new JLabel("Mileage"));
    panel.add(mileageField);

    JButton saveButton = new JButton("Save Vehicle");
    saveButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            try {
                String make = makeField.getText();
                String model = modelField.getText();
                int year = Integer.parseInt(yearField.getText());
                double dailyRate = Double.parseDouble(dailyRateField.getText());
                String type = typeField.getText();
                String registration = registrationField.getText();
                int seats = Integer.parseInt(seatsField.getText());
                String ac = acField.getText();
                double mileage = Double.parseDouble(mileageField.getText());

                Vehicle vehicle = new Vehicle(make, model, year, dailyRate, type, registration, seats, ac, mileage);
                bookingSystem.addVehicleToInventory(vehicle);
                bookingSystem.saveInventory();
                JOptionPane.showMessageDialog(null, "Vehicle added successfully!");
                tabbedPane.setSelectedIndex(0);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please fill out all fields correctly.");
            }
        }
    });

    panel.add(saveButton);

    return panel;
}

private void refreshVehicleList() {
    vehicleListModel.clear();
    for (Vehicle vehicle : bookingSystem.getAvailableVehicles()) {
        vehicleListModel.addElement(vehicle);
    }
}

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        VehicleBooking frame = new VehicleBooking();
        frame.pack();
        frame.setVisible(true);
    });
}
}