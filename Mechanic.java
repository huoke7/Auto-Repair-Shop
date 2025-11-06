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
}