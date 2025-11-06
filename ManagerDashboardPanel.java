import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ManagerDashboardPanel extends JPanel implements ActionListener{

    private Manager manager;
    private AutoShop shop;
    private JTextArea outputArea;

    private JButton createWoButton;
    private JButton assignMechButton;
    private JButton genInvoiceButton;
    private JButton restockButton;
    private JButton reportButton;
    private JButton logoutButton;

    public ManagerDashboardPanel(Manager manager, AutoShop shop){
        this.manager = manager;
        this.shop = shop;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Menu
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(6, 1, 5, 5));

        // Initialize the class fields
        createWoButton = new JButton("1. Create Work Order");
        assignMechButton = new JButton("2. Assign Mechanic");
        genInvoiceButton = new JButton("3. Generate Invoice");
        restockButton = new JButton("4. Restock Inventory");
        reportButton = new JButton("5. Produce Report");
        logoutButton = new JButton("9. Logout");

        menuPanel.add(createWoButton);
        menuPanel.add(assignMechButton);
        menuPanel.add(genInvoiceButton);
        menuPanel.add(restockButton);
        menuPanel.add(reportButton);
        menuPanel.add(logoutButton);

        add(menuPanel, BorderLayout.WEST);

        outputArea = new JTextArea("Welcome, Manager " + manager.getUsername() + "!\n");
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        createWoButton.addActionListener(this);
        assignMechButton.addActionListener(this);
        genInvoiceButton.addActionListener(this);
        restockButton.addActionListener(this);
        reportButton.addActionListener(this);
        logoutButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        // Get the button that was clicked
        Object source = e.getSource();
        
        // Check which button it was and call the correct helper
        if (source == createWoButton){
            handleCreateWorkOrder();
        } else if (source == assignMechButton){
            handleAssignMechanic();
        } else if (source == genInvoiceButton){
            handleGenerateInvoice();
        } else if (source == restockButton){
            handleRestockInventory();
        } else if (source == reportButton){
            handleProduceReport();
        } else if (source == logoutButton){
            handleLogout();
        }
    }

    private void handleCreateWorkOrder(){
        String custUsername = JOptionPane.showInputDialog(this, "Enter customer username:");
        if (custUsername == null) return;
        Customer customer = shop.findCustomer(custUsername);
        if (customer == null){
            JOptionPane.showMessageDialog(this, "Error: Customer not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String vin = JOptionPane.showInputDialog(this, "Enter vehicle VIN:");
        if (vin == null) return;
        Vehicle vehicle = shop.findVehicle(customer, vin);
        if (vehicle == null){
            JOptionPane.showMessageDialog(this, "Error: Vehicle not found for this customer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Service> requestedServices = new ArrayList<>();
        while (true){
            String serviceID = JOptionPane.showInputDialog(this, "Enter Service ID to add (or 'done' to finish):");
            if (serviceID == null || "done".equalsIgnoreCase(serviceID)){
                break;
            }
            Service service = shop.findService(serviceID);
            if (service != null){
                boolean partsAvailable = true;
                for (PartRequirement req : service.getRequiredParts()){
                    if (!shop.getInventory().checkPartAvailability(req.getPartNumber(), req.getQuantity())){
                        JOptionPane.showMessageDialog(this, "Error: Part " + req.getPartNumber() + " is out of stock. Cannot add service.", "Error", JOptionPane.ERROR_MESSAGE);
                        partsAvailable = false;
                        break;
                    }
                }
                if (partsAvailable){
                    requestedServices.add(service);
                    outputArea.append("Added service: " + service.getDescription() + "\n");
                }
            } else{
                JOptionPane.showMessageDialog(this, "Error: Service ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (requestedServices.isEmpty()){
            outputArea.append("No services selected. Work order cancelled.\n");
            return;
        }

        WorkOrder wo = manager.createNewWorkOrder(customer, vehicle, requestedServices);
        for (Service s : requestedServices){
            for (PartRequirement req : s.getRequiredParts()){
                shop.getInventory().reservePart(req.getPartNumber(), req.getQuantity());
            }
        }
        shop.getAllWorkOrders().add(wo);
        
        outputArea.append("Successfully created " + wo.getWorkOrderID() + " and reserved parts.\n");
        printInventoryToGui();
    }

    private void handleAssignMechanic(){
        String woID = JOptionPane.showInputDialog(this, "Enter Work Order ID:");
        if (woID == null) return;
        WorkOrder wo = shop.findWorkOrder(woID);
        if (wo == null){
            JOptionPane.showMessageDialog(this, "Error: Work Order not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String mechUsername = JOptionPane.showInputDialog(this, "Enter Mechanic username:");
        if (mechUsername == null) return;
        Mechanic mechanic = shop.findMechanic(mechUsername);
        if (mechanic == null){
            JOptionPane.showMessageDialog(this, "Error: Mechanic not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        manager.assignMechanicToJob(wo, mechanic);
        outputArea.append("Assigned " + mechanic.getUsername() + " to " + wo.getWorkOrderID() + "\n");
    }

    private void handleGenerateInvoice(){
        String woID = JOptionPane.showInputDialog(this, "Enter Work Order ID to invoice:");
        if (woID == null) return;
        WorkOrder wo = shop.findWorkOrder(woID);
        if (wo == null){
            JOptionPane.showMessageDialog(this, "Error: Work Order not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!wo.getStatus().equals(WorkStatus.DONE)){
             JOptionPane.showMessageDialog(this, "Error: Work Order is not 'DONE'. Current status: " + wo.getStatus(), "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        double taxRate = 0.0;
        try{
            String taxStr = JOptionPane.showInputDialog(this, "Enter tax rate (e.g., 0.13 for 13%):");
            if (taxStr == null) return;
            taxRate = Double.parseDouble(taxStr);
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this, "Invalid number. Using 0.0 tax.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        
        Invoice invoice = manager.generateInvoice(wo, shop.getInventory(), taxRate); // Will print to console
        if (invoice != null){
             outputArea.append("Generated Invoice " + invoice.getInvoiceID() + " for " + wo.getWorkOrderID() + "\n");
             outputArea.append(invoice.toString() + "\n");
        } else{
             outputArea.append("Failed to generate invoice for " + wo.getWorkOrderID() + ".\n");
        }
    }

    private void handleRestockInventory(){
        String partNum = JOptionPane.showInputDialog(this, "Enter Part Number to restock:");
        if (partNum == null) return;
        if (shop.getInventory().getPart(partNum) == null){
            JOptionPane.showMessageDialog(this, "Error: Part " + partNum + " does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int quantity = 0;
        try{
            String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity to add:");
            if (qtyStr == null) return;
            quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0){
                JOptionPane.showMessageDialog(this, "Quantity must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        manager.restockInventory(shop.getInventory(), partNum, quantity);
        outputArea.append("Restocked " + partNum + " by " + quantity + ".\n");
        printInventoryToGui();
    }

    private void handleProduceReport(){
        outputArea.setText(" Manager Report \n");
        List<WorkOrder> allWorkOrders = shop.getAllWorkOrders();
        
        outputArea.append("Total Work Orders: " + allWorkOrders.size() + "\n");
        int closedOrders = 0;
        double totalRevenue = 0;
        for (WorkOrder wo : allWorkOrders){
            if (wo.getStatus().equals(WorkStatus.CLOSED) && wo.getInvoice() != null && wo.getInvoice().getStatus().equals(InvoiceStatus.PAID)){
                closedOrders++;
                totalRevenue += wo.getInvoice().getTotalAmount();
            }
        }
        outputArea.append(String.format("Total Revenue: $%.2f", totalRevenue) + "\n");
        outputArea.append("\nInventory Status:\n");
        
        printInventoryToGui();
        
        outputArea.append(" End of Report \n");
    }
    
    private void printInventoryToGui(){
        outputArea.append("\n Current Inventory Stock \n");
        List<Part> allParts = shop.getInventory().getParts();
        
        if (allParts.isEmpty()){
            outputArea.append("Inventory is empty.\n");
            return;
            }
        
        for (Part p : allParts){ 
            outputArea.append(p.getPartNumber() + ": " + p.getDescription() + " (Stock: " + p.getStockLevel() + ")\n");
            }
    }

    private void handleLogout(){
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.dispose();
        new LoginFrame(shop);
    }
}