import java.util.List;

/**
 * Represents a standard service the shop can perform.
 */
public class Service{
    private String serviceID;
    private String description;
    private double standardLaborHours;
    private List<PartRequirement> requiredParts;
    private String compatiblePowertrain;
    private boolean isCompleted;

    public Service(String serviceID, String description, double standardLaborHours, List<PartRequirement> requiredParts, String compatiblePowertrain){
        this.serviceID = serviceID;
        this.description = description;
        this.standardLaborHours = standardLaborHours;
        this.requiredParts = requiredParts;
        this.compatiblePowertrain = compatiblePowertrain;
    }
    
    /**
     * Checks if this service can be performed on the given vehicle.
     */
    public boolean isCompatibleWith(Vehicle vehicle){
        if (this.compatiblePowertrain == null){
            return true;
        }
        return this.compatiblePowertrain.equals(vehicle.getPowertrain());
    }
    

    public String getServiceID(){
        return serviceID;
    }
    public String getDescription(){
        return description;
    }
    public double getStandardLaborHours(){
        return standardLaborHours;
    }


    public List<PartRequirement> getRequiredParts(){ 
        return requiredParts; 
    }
    
    @Override
    public String toString(){
        return description + " (Std. " + standardLaborHours + " hours)";
    }

    public void markAsCompleted(){
        this.isCompleted = true;
    }

    public boolean isCompleted(){
        return this.isCompleted;
    }






}

