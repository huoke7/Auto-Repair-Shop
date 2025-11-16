import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CustomerDashboardPanel extends JPanel implements ActionListener{

    private Customer customer;
    private AutoShop shop;
    private JButton viewVehiclesButton;
    private JButton viewInvoicesButton;
    private JButton makePaymentButton;
    private JButton addVehicleButton;
    private JButton logoutButton;
    private JTextArea outputArea; 

    public CustomerDashboardPanel(Customer customer, AutoShop shop){
        this.customer = customer;
        this.shop = shop;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(6, 1, 5, 5));

        viewVehiclesButton = new JButton("1. View My Vehicles");
        viewInvoicesButton = new JButton("2. View My Invoices");
        makePaymentButton = new JButton("3. Make Payment");
        addVehicleButton = new JButton("4. Add New Vehicle");
        logoutButton = new JButton("9. Logout");
        
        menuPanel.add(viewVehiclesButton);
        menuPanel.add(viewInvoicesButton);
        menuPanel.add(makePaymentButton);
        menuPanel.add(addVehicleButton);
        menuPanel.add(new JLabel(""));
        menuPanel.add(logoutButton);
        
        add(menuPanel, BorderLayout.WEST);

        outputArea = new JTextArea("Welcome, " + customer.getUsername() + "!\n");
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        
        add(scrollPane, BorderLayout.CENTER);
    
        viewVehiclesButton.addActionListener(this);
        viewInvoicesButton.addActionListener(this);
        makePaymentButton.addActionListener(this);
        addVehicleButton.addActionListener(this);
        logoutButton.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Object source = e.getSource();
        
        if (source == viewVehiclesButton){
            handleViewVehicles();
        } else if (source == viewInvoicesButton){
            handleViewInvoices();
        } else if (source == makePaymentButton){
            handleMakePayment();
        } else if (source == addVehicleButton) {
            handleAddVehicle();
        } else if (source == logoutButton){
            handleLogout();
        }
    }

    private void handleViewVehicles(){
        outputArea.setText("--- My Vehicles ---\n");
        List<Vehicle> vehicles = customer.getVehicles();
        
        if (vehicles.isEmpty()){
            outputArea.append("You have no vehicles registered.");
            return;
        }
        for (Vehicle v : vehicles){
            outputArea.append(v.getVehicleDetails() + "\n");
        }
    }
    
    private void handleViewInvoices(){
        outputArea.setText("--- My Invoices ---\n");
        List<Invoice> invoices = customer.getInvoices();
        
        if (invoices.isEmpty()){
            outputArea.append("You have no outstanding invoices.");
            return;
        }
        for (Invoice inv : invoices){
            outputArea.append(inv.toString() + "\n");
        }
    }
    

    private void handleMakePayment(){
        String invID = JOptionPane.showInputDialog(this, 
            "Enter Invoice ID to pay:");
            
        if (invID == null || invID.trim().isEmpty()){
            return;
        }

        Invoice invoiceToPay = null;
        for (Invoice inv : customer.getInvoices()){
            if (inv.getInvoiceID().equalsIgnoreCase(invID.trim())){
                invoiceToPay = inv;
                break;
            }
        }
        
        if (invoiceToPay == null){
            JOptionPane.showMessageDialog(this, "Error: Invoice not found.", "Payment Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (invoiceToPay.getStatus().equals(InvoiceStatus.PAID)){
            JOptionPane.showMessageDialog(this, "This invoice is already paid.", "Payment Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        customer.makePayment(invoiceToPay); 
        
        JOptionPane.showMessageDialog(this, 
            "Payment successful for Invoice " + invoiceToPay.getInvoiceID(), 
            "Payment Success", 
            JOptionPane.INFORMATION_MESSAGE);
            
        handleViewInvoices();
    }
    
    /**
     * Handles logic for adding a new vehicle.
     * This method now supports Car, Truck, and Motorbike.
     */
    private void handleAddVehicle() {
        String[] vehicleTypes = {"Car", "Truck", "Motorbike"};
        String chosenType = (String) JOptionPane.showInputDialog(
                this,
                "Select the type of vehicle to add:",
                "Add New Vehicle - Step 1",
                JOptionPane.QUESTION_MESSAGE,
                null,
                vehicleTypes,
                vehicleTypes[0]);
        if (chosenType == null) {
            return;
        }

        JTextField vinField = new JTextField();
        JTextField makeField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField yearField = new JTextField();
        String[] powertrainOptions = { Powertrain.COMBUSTION, Powertrain.ELECTRIC };
        JComboBox<String> powertrainField = new JComboBox<>(powertrainOptions);
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.add(new JLabel("VIN:"));
        formPanel.add(vinField);
        formPanel.add(new JLabel("Make:"));
        formPanel.add(makeField);
        formPanel.add(new JLabel("Model:"));
        formPanel.add(modelField);
        formPanel.add(new JLabel("Year:"));
        formPanel.add(yearField);
        formPanel.add(new JLabel("Powertrain:"));
        formPanel.add(powertrainField);
        JTextField specificField = new JTextField();
        String specificLabel;
        
        switch (chosenType) {
            case "Car":
                specificLabel = "Body Type (e.g., Sedan):";
                break;
            case "Truck":
                specificLabel = "Towing Capacity (lbs):";
                break;
            case "Motorbike":
                specificLabel = "Engine Displacement (cc):";
                break;
            default:
                return;
        }
        formPanel.add(new JLabel(specificLabel));
        formPanel.add(specificField);

        int result = JOptionPane.showConfirmDialog(this, formPanel, "Add New " + chosenType,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String vin = vinField.getText().trim();
                String make = makeField.getText().trim();
                String model = modelField.getText().trim();
                String yearStr = yearField.getText().trim();
                String powertrain = (String) powertrainField.getSelectedItem();
                String specificValue = specificField.getText().trim();

                if (vin.isEmpty() || make.isEmpty() || model.isEmpty() || yearStr.isEmpty() || specificValue.isEmpty()) {
                    throw new Exception("All fields are required.");
                }

                int year = Integer.parseInt(yearStr);
                
                Vehicle newVehicle = null;

                switch (chosenType) {
                    case "Car":
                        newVehicle = new Car(vin, make, model, year, powertrain, this.customer, specificValue);
                        break;
                    case "Truck":
                        double towingCapacity = Double.parseDouble(specificValue);
                        newVehicle = new Truck(vin, make, model, year, powertrain, this.customer, towingCapacity, 0); 
                        break;
                    case "Motorbike":
                        int engineDisplacement = Integer.parseInt(specificValue);
                        newVehicle = new Motorbike(vin, make, model, year, powertrain, this.customer, engineDisplacement);
                        break;
                }

                if (newVehicle != null) {
                    shop.addVehicle(newVehicle); 
                    shop.saveVehiclesToFile();

                    JOptionPane.showMessageDialog(this, "Vehicle added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    handleViewVehicles();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: 'Year' and 'Capacity/CC' must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleLogout(){
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.dispose();
        new LoginFrame(shop);
    }
}