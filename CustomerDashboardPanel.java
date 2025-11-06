import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CustomerDashboardPanel extends JPanel implements ActionListener{

    // Model data
    private Customer customer;
    private AutoShop shop;

    // These were already class fields, which is perfect for this pattern
    private JButton viewVehiclesButton;
    private JButton viewInvoicesButton;
    private JButton makePaymentButton;
    private JButton logoutButton;
    private JTextArea outputArea; 

    public CustomerDashboardPanel(Customer customer, AutoShop shop){
        this.customer = customer;
        this.shop = shop;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Create Menu of buttons
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(5, 1, 5, 5));

        viewVehiclesButton = new JButton("1. View My Vehicles");
        viewInvoicesButton = new JButton("2. View My Invoices");
        makePaymentButton = new JButton("3. Make Payment");
        logoutButton = new JButton("9. Logout");
        
        menuPanel.add(viewVehiclesButton);
        menuPanel.add(viewInvoicesButton);
        menuPanel.add(makePaymentButton);
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
        logoutButton.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        // Get the object that triggered the event
        Object source = e.getSource();
        
        // Check which button it was
        if (source == viewVehiclesButton){
            handleViewVehicles();
        } else if (source == viewInvoicesButton){
            handleViewInvoices();
        } else if (source == makePaymentButton){
            handleMakePayment();
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
    
    private void handleLogout(){
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.dispose();
        new LoginFrame(shop);
    }
}