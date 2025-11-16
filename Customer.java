import java.util.ArrayList;
import java.util.List;

public class Customer extends User{
    private List<Vehicle> vehicles;
    private List<WorkOrder> serviceHistory;
    private List<Invoice> invoices;
    // constructor
    public Customer(String username, String password, String contactInfo){
        super(username, password, Role.CUSTOMER, contactInfo);
        this.vehicles = new ArrayList<>();
        this.serviceHistory = new ArrayList<>();
        this.invoices = new ArrayList<>();
    }
    

    public void makePayment(Invoice invoice){
        System.out.println("Attempting to pay " + String.format("$%.2f", invoice.getTotalAmount()) + " for Invoice " + invoice.getInvoiceID());

        if (!invoice.getStatus().equals(InvoiceStatus.PAID)){
            invoice.recordPayment();
            System.out.println("Payment successful. Invoice " + invoice.getInvoiceID() + " is now PAID.");
        } else{
            System.out.println("This invoice is already paid.");
        }
    }

    

    
    public void addServiceHistory(WorkOrder wo){
        this.serviceHistory.add(wo);
    }
    
    public void addInvoice(Invoice inv){
        this.invoices.add(inv);
    }

    public List<Vehicle> getVehicles(){
        return vehicles;
    }
    
    public List<Invoice> getInvoices(){
        return invoices;
    }

    public void addVehicle(Vehicle vehicle) {
        if (this.vehicles == null) {
            this.vehicles = new ArrayList<>();
        }
        this.vehicles.add(vehicle);
    }

}