/**
 * Represents a bill for a completed WorkOrder
 */
public class Invoice{
    private String invoiceID;
    private WorkOrder workOrder; // The WO this invoice is for
    private double laborCost;
    private double partsCost;
    private double taxesAndFees;
    private double totalAmount;
    private String status; 

    public Invoice(WorkOrder workOrder, Inventory inventory, double taxRate){
        this.invoiceID = "I-" + workOrder.getWorkOrderID();
        this.workOrder = workOrder;
        this.status = InvoiceStatus.UNPAID; 
        
        // Calculate costs
        this.laborCost = calculateLaborCost();
        this.partsCost = calculatePartsCost(inventory); 
        this.taxesAndFees = (this.laborCost + this.partsCost) * taxRate;
        this.totalAmount = this.laborCost + this.partsCost + this.taxesAndFees;
    }
    
    /**
     * Helper method to calculate total labor cost from all services.
     */
    private double calculateLaborCost(){
        double totalHours = 0;
        for (Service s : this.workOrder.getServicesRequested()){
            totalHours += s.getStandardLaborHours();
        }
        double LABOR_RATE_PER_HOUR = 80.0;
        return totalHours * LABOR_RATE_PER_HOUR;
    }
    
    /**
     * Helper method to calculate total part cost from all services.
     */
    private double calculatePartsCost(Inventory inventory){
        double cost = 0;
        for (Service s : this.workOrder.getServicesRequested()){
            for (PartRequirement req : s.getRequiredParts()){
                Part p = inventory.getPart(req.getPartNumber()); 
                if (p != null){
                    cost += p.getPrice() * req.getQuantity();
                } else{
                    System.out.println("Error: Part " + req.getPartNumber() + " not found in inventory for invoicing.");
                }
            }
        }
        return cost;
    }

    //  Public Methods to update state 
    public void recordPayment(){
        this.status = InvoiceStatus.PAID; 
    }
    
    public void flagAsDeclined(){
        this.status = InvoiceStatus.DECLINED; 
    }
    
    //Getters 
    public String getInvoiceID(){ 
        return invoiceID; 
    }
    public WorkOrder getWorkOrder(){ 
        return workOrder;
    }
    public double getTotalAmount(){ 
        return totalAmount; }
    public String getStatus(){ return status; } 
    
    @Override
    public String toString(){
        return "Invoice [ID=" + invoiceID + 
               ", WorkOrder=" + workOrder.getWorkOrderID() + 
               ", Total=" + String.format("$%.2f", totalAmount) + 
               ", Status=" + status + "]";
    }
}

