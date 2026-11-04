import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Mechanic.
 * Extends the User class.
 */
public class Mechanic extends User{
    private List<String> skills;
    private List<WorkOrder> assignedWorkOrders;

    public Mechanic(String username, String password, String contactInfo){
        super(username, password, Role.MECHANIC, contactInfo);
        this.skills = new ArrayList<>();
        this.assignedWorkOrders = new ArrayList<>();
    }


    public void viewAssignedJobs(){
        System.out.println("\n " + getUsername() + "'s Assigned Jobs ");
        if (assignedWorkOrders.isEmpty()){
            System.out.println("No jobs assigned.");
            return;
        }
        for (WorkOrder wo : assignedWorkOrders){
            System.out.println(wo);
        }
    }
    
    /**
     * @param wo
     * @param status
     */
    public void updateWorkOrderStatus(WorkOrder wo, String status){
        if (assignedWorkOrders.contains(wo)){
            wo.updateStatus(status);
            System.out.println("WorkOrder " + wo.getWorkOrderID() + " status updated to " + status);
        } else{
            System.out.println("Error: Cannot update status of a job not assigned to you.");
        }
    }
    
    public void logHours(WorkOrder wo, double hours){
        if (assignedWorkOrders.contains(wo)){
            wo.logLabor(hours);
            System.out.println(hours + " hours logged to WorkOrder " + wo.getWorkOrderID() + ". Total hours: " + wo.getActualLaborHours());
        } else{
            System.out.println("Error: Cannot log hours for a job not assigned to you.");
        }
    }
    
    public void addSkill(String skill){
        this.skills.add(skill);
    }
    
    public void assignWorkOrder(WorkOrder wo){
        this.assignedWorkOrders.add(wo);
    }

    public List<String> getSkills(){
        return skills;
    }
    public List<WorkOrder> getAssignedWorkOrders(){
        return this.assignedWorkOrders;
    }

    public boolean performJob(WorkOrder wo, Service service, String partNumber, int quantityUsed) {
        if (wo.getAssignedMechanic() == null || !wo.getAssignedMechanic().equals(this)) {
            System.out.println("Mechanic " + this.getUsername() + " is not assigned to " + wo.getWorkOrderID());
            return false;
        }

        if (!wo.getServicesRequested().contains(service)) {
            System.out.println("Service " + service.getDescription() + " is not part of " + wo.getWorkOrderID());
            return false;
        }

        PartRequirement requirement = null;
        for (PartRequirement req : service.getRequiredParts()) {
            if (req.getPartNumber().equals(partNumber)) {
                requirement = req;
                break;
            }
        }

        if (requirement == null) {
            System.out.println("Error: Part " + partNumber + " is not a required part for service " + service.getDescription());
            return false;
        }
        
        if (requirement.getQuantity() != quantityUsed) {
            System.out.println("Error: Quantity mismatch. Service requires " + requirement.getQuantity() + " of " + partNumber + ", but " + quantityUsed + " were entered.");
            return false;
        }

        System.out.println("Successfully performed service: " + service.getDescription());
        service.markAsCompleted();
        return true;
    }

    /**
     * Checks if all services in a work order are done
     */
    public void completeWorkOrder(WorkOrder wo) {
        if (wo.getAssignedMechanic() == null || !wo.getAssignedMechanic().equals(this)) {
            System.out.println("Cannot complete: Mechanic " + this.getUsername() + " is not assigned to " + wo.getWorkOrderID());
            return;
        }

        if (wo.getStatus().equals(WorkStatus.DONE) || wo.getStatus().equals(WorkStatus.CLOSED)) {
            System.out.println("Work Order " + wo.getWorkOrderID() + " is already DONE or CLOSED.");
            return;
        }

        boolean allServicesDone = true;
        for (Service s : wo.getServicesRequested()) {
            if (!s.isCompleted()) {
                allServicesDone = false;
                System.out.println("Cannot complete " + wo.getWorkOrderID() + ": Service '" + s.getDescription() + "' is not finished.");
                break;
            }
        }
        if (allServicesDone) {
            wo.updateStatus(WorkStatus.DONE);
            System.out.println("All services complete. " + wo.getWorkOrderID() + " status updated to DONE.");
        }
    }

}