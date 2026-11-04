import java.util.List;
/*
 * The central class in the system.
 */
public class WorkOrder{
    public static final double SHOP_LABOR_RATE = 120.00;

    private String workOrderID;
    private Customer customer;
    private Vehicle vehicle;
    private List<Service> servicesRequested;
    private Mechanic assignedMechanic;
    private String status;
    private double actualLaborHours;
    private Invoice invoice; 

    public WorkOrder(String workOrderID, Customer customer, Vehicle vehicle, List<Service> services){
        this.workOrderID = workOrderID;
        this.customer = customer;
        this.vehicle = vehicle;
        this.servicesRequested = services;
        this.status = WorkStatus.PENDING;
        

        double totalHours = 0;
        for (Service s : services){
            totalHours += s.getStandardLaborHours();
        }
        this.actualLaborHours = totalHours;
    }
    
    /**
     * Calculates the total cost of all parts required for this WO's services.
     */
    public double calculatePartsCost(Inventory inventory){
        double totalPartsCost = 0;
        for (Service s : servicesRequested){
            // Get the list of PartRequirements for this service
            for (PartRequirement req : s.getRequiredParts()){
                String partNumber = req.getPartNumber();
                int quantity = req.getQuantity();
                Part part = inventory.getPart(partNumber);
                if (part != null){
                    totalPartsCost += part.getPrice() * quantity;
                } else{
                    System.out.println("Warning: Part " + partNumber + " price not found!");
                }
            }
        }
        return totalPartsCost;
    }
    
    /**
     * Calculates the total labor cost for this WO.
     */
    public double calculateLaborCost(){
        return this.actualLaborHours * SHOP_LABOR_RATE;
    }


    public void updateStatus(String status){
        this.status = status;
    }

    public void logLabor(double hours){
        this.actualLaborHours += hours;
    }
    
    public void assignMechanic(Mechanic m){
        this.assignedMechanic = m;
        this.status = WorkStatus.IN_PROGRESS;
    }

    //Getters 
    public String getWorkOrderID(){ 
        return workOrderID; 
    }
    public Customer getCustomer(){ 
        return customer; 
    }
    public Vehicle getVehicle(){ 
        return vehicle; 
    }
    public String getStatus(){ 
        return status; 
    }
    public double getActualLaborHours(){ 
        return actualLaborHours; 
    }
    public List<Service> getServicesRequested(){ 
        return servicesRequested; 
    }
    public Mechanic getAssignedMechanic(){ 
        return assignedMechanic; 
    }
    public Invoice getInvoice(){ 
        return invoice; 
    }
    public void setInvoice(Invoice invoice){ 
        this.invoice = invoice; 
    }
    
    @Override
    public String toString(){
        return "WorkOrder(ID=" + workOrderID + ", Vehicle=" + vehicle.getModel() + 
               ", Services=" + servicesRequested.get(0).getDescription() + 
               ", Status=" + status + 
               ", Mechanic=" + (assignedMechanic != null ? assignedMechanic.getUsername() : "N/A") + ")";
    }
}

