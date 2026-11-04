import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MechanicDashboardPanel extends JPanel implements ActionListener{

    private Mechanic mechanic;
    private AutoShop shop;
    private JTextArea outputArea;

    private JButton viewJobsButton;
    private JButton updateStatusButton;
    private JButton logHoursButton;
    private JButton logoutButton;

    public MechanicDashboardPanel(Mechanic mechanic, AutoShop shop){
        this.mechanic = mechanic;
        this.shop = shop;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Menu
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(4, 1, 5, 5));

        viewJobsButton = new JButton("1. View My Assigned Jobs");
        updateStatusButton = new JButton("2. Update Job Status");
        logHoursButton = new JButton("3. Log Hours");
        logoutButton = new JButton("9. Logout");

        menuPanel.add(viewJobsButton);
        menuPanel.add(updateStatusButton);
        menuPanel.add(logHoursButton);
        menuPanel.add(logoutButton);

        add(menuPanel, BorderLayout.WEST);

        outputArea = new JTextArea("Welcome, Mechanic " + mechanic.getUsername() + "!\n");
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        viewJobsButton.addActionListener(this);
        updateStatusButton.addActionListener(this);
        logHoursButton.addActionListener(this);
        logoutButton.addActionListener(this);
        
        handleViewAssignedJobs();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        Object source = e.getSource();
        if (source == viewJobsButton){
            handleViewAssignedJobs();
        } else if (source == updateStatusButton){
            handleUpdateJobStatus();
        } else if (source == logHoursButton){
            handleLogHours();
        } else if (source == logoutButton){
            handleLogout();
        }
    }


    private void handleViewAssignedJobs(){
        outputArea.setText(" " + mechanic.getUsername() + "'s Assigned Jobs \n");
        
        List<WorkOrder> assignedWorkOrders = mechanic.getAssignedWorkOrders();
        if (assignedWorkOrders.isEmpty()){
            outputArea.append("No jobs assigned.");
        }
        for (WorkOrder wo : assignedWorkOrders){
            outputArea.append(wo.toString() + "\n");
        }
    }

    private WorkOrder findMechanicsWorkOrder(String woID){
        WorkOrder wo = shop.findWorkOrder(woID);
        if (wo != null && wo.getAssignedMechanic() == mechanic){
            return wo;
        }
        return null;
    }

    private void handleUpdateJobStatus(){
        outputArea.append("\n Perform Service\n");
        String woID = JOptionPane.showInputDialog(this, "Enter Work Order ID:");
        if (woID == null || woID.trim().isEmpty()){
            return;
        }
        
        WorkOrder wo = shop.findWorkOrder(woID.trim());
        if (wo == null){
            JOptionPane.showMessageDialog(this, "Error: Work Order not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (wo.getAssignedMechanic() == null || !wo.getAssignedMechanic().equals(mechanic)){
            JOptionPane.showMessageDialog(this, "Error: You are not assigned to this Work Order.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String serviceID = JOptionPane.showInputDialog(this, "Enter Service ID you performed:");
        if (serviceID == null || serviceID.trim().isEmpty()){
            return;
        }
        
        Service serviceToPerform = null;
        for (Service s : wo.getServicesRequested()){
            if (s.getServiceID().equalsIgnoreCase(serviceID.trim())){
                serviceToPerform = s;
                break;
            }
        }
        
        if (serviceToPerform == null){
            JOptionPane.showMessageDialog(this, "Error: Service not found in this Work Order.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (serviceToPerform.isCompleted()){
             JOptionPane.showMessageDialog(this, "This service is already marked as complete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String partNumber = JOptionPane.showInputDialog(this, "Enter Part Number used for '" + serviceToPerform.getDescription() + "':");
        if (partNumber == null || partNumber.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Part number is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        partNumber = partNumber.trim();

        int quantityUsed = 0;
        try{
            String qtyStr = JOptionPane.showInputDialog(this, "Enter Quantity of " + partNumber + " used:");
            if (qtyStr == null){
                return;
            }
            quantityUsed = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = mechanic.performJob(wo, serviceToPerform, partNumber, quantityUsed);
        if (success){
            outputArea.append("Successfully marked service '" + serviceToPerform.getDescription() + "' as complete on " + wo.getWorkOrderID() + ".\n");
            mechanic.completeWorkOrder(wo);

            if (wo.getStatus().equals(WorkStatus.DONE)) {
                outputArea.append("All services are complete. Work Order " + wo.getWorkOrderID() + " is now marked as DONE.\n");
                JOptionPane.showMessageDialog(this, 
                    "Service updated! All services for " + wo.getWorkOrderID() + " are now complete.", 
                    "Work Order Done", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Service updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

        } else{
            outputArea.append("Failed to update service '" + serviceToPerform.getDescription() + "' on " + wo.getWorkOrderID() + ".\n");
            JOptionPane.showMessageDialog(this, "Failed to update service. Check output area for details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLogHours(){
        String woID = JOptionPane.showInputDialog(this, "Enter Work Order ID:");
        if (woID == null) return;
        WorkOrder wo = findMechanicsWorkOrder(woID);
        
        if (wo == null){
            JOptionPane.showMessageDialog(this, "Error: Work Order not found or not assigned to you.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double hours = 0;
        try{
            String hoursStr = JOptionPane.showInputDialog(this, "Enter hours to log (e.g., 1.5):");
            if (hoursStr == null) return;
            hours = Double.parseDouble(hoursStr);
            if (hours <= 0){
                JOptionPane.showMessageDialog(this, "Hours must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        mechanic.logHours(wo, hours);
        outputArea.append(hours + " hours logged to WorkOrder " + wo.getWorkOrderID() + ". Total hours: " + wo.getActualLaborHours() + "\n");
    }

    private void handleLogout(){
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.dispose();
        new LoginFrame(shop);
    }
}