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

        // Initialize the class fields
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
        
        // This initial call remains the same
        handleViewAssignedJobs();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        // Get the button that was clicked
        Object source = e.getSource();

        // Check which button it was and call the correct helper
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
        
        // This code assumes you have added the getAssignedWorkOrders() getter to Mechanic.java
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
        return null; // If not found or doesn't belong to them, return null
    }

    private void handleUpdateJobStatus(){
        String woID = JOptionPane.showInputDialog(this, "Enter Work Order ID:");
        if (woID == null) return;
        WorkOrder wo = findMechanicsWorkOrder(woID);
        
        if (wo == null){
            // Updated error message to be more accurate
            JOptionPane.showMessageDialog(this, "Error: Work Order not found or not assigned to you.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String status = JOptionPane.showInputDialog(this, "Enter new status (PENDING, IN_PROGRESS, DONE):");
        if (status == null) return;
        status = status.toUpperCase();

        if (status.equals(WorkStatus.PENDING) || status.equals(WorkStatus.IN_PROGRESS) || status.equals(WorkStatus.DONE)){
            mechanic.updateWorkOrderStatus(wo, status);
            outputArea.append("WorkOrder " + wo.getWorkOrderID() + " status updated to " + status + "\n");
        } else{
            JOptionPane.showMessageDialog(this, "Error: Invalid status. Must be PENDING, IN_PROGRESS, or DONE.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLogHours(){
        String woID = JOptionPane.showInputDialog(this, "Enter Work Order ID:");
        if (woID == null) return;
        WorkOrder wo = findMechanicsWorkOrder(woID);
        
        if (wo == null){
            // Updated error message to be more accurate
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