import java.util.List;

/**
 * Represents a Manager.
 * This class orchestrates many other objects (WorkOrders, Mechanics, Invoices).
 */
public class Manager extends User{

    public Manager(String username, String password, String contactInfo){
        super(username, password, Role.MANAGER, contactInfo);
    }
    
    public WorkOrder createNewWorkOrder(Customer customer, Vehicle vehicle, List<Service> services){
        String woID = "WO-" + (int)(Math.random() * 10000);
        WorkOrder wo = new WorkOrder(woID, customer, vehicle, services);
        customer.addServiceHistory(wo);
        System.out.println("Created " + wo); // This will still print to console
        return wo;
    }
    
    public void assignMechanicToJob(WorkOrder wo, Mechanic mechanic){
        wo.assignMechanic(mechanic);
        mechanic.assignWorkOrder(wo);
        System.out.println("Assigned " + mechanic.getUsername() + " to " + wo.getWorkOrderID());
    }
    
    public Invoice generateInvoice(WorkOrder wo, Inventory inventory, double taxRate){
        if (wo.getStatus().equals(WorkStatus.DONE)){
            Invoice invoice = new Invoice(wo, inventory, taxRate);
            wo.getCustomer().addInvoice(invoice);
            wo.updateStatus(WorkStatus.CLOSED);
            wo.setInvoice(invoice);
            System.out.println("Generated Invoice " + invoice.getInvoiceID() + " for " + wo.getWorkOrderID());
            System.out.println(invoice);
            return invoice;
        } else{
            System.out.println("Cannot generate invoice: WorkOrder " + wo.getWorkOrderID() + " is not 'DONE'.");
            return null;
        }
    }

    public void restockInventory(Inventory inventory, String partNumber, int quantity){
        inventory.restockPart(partNumber, quantity);
        System.out.println("Restocked " + partNumber + " by " + quantity + ". New stock: " + inventory.getPart(partNumber).getStockLevel());
    }
}