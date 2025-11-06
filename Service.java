import java.util.List;

/**
 * Represents a standard service the shop can perform.
 * e.g., "Oil Change", "Brake Replacement"
 * SINGLE RESPONSIBILITY: Defines a single service.
 */
public class Service{
    private String serviceID;
    private String description;
    private double standardLaborHours;
    // CHANGED: Replaced Map<String, Integer> with List<PartRequirement>
    private List<PartRequirement> requiredParts;
    private String compatiblePowertrain; // e.g., COMBUSTION, ELECTRIC, or null for all

    // CONSTRUCTOR UPDATED
    public Service(String serviceID, String description, double standardLaborHours, List<PartRequirement> requiredParts, String compatiblePowertrain){
        this.serviceID = serviceID;
        this.description = description;
        this.standardLaborHours = standardLaborHours;
        this.requiredParts = requiredParts; // Changed
        this.compatiblePowertrain = compatiblePowertrain;
    }
    
    /**
     * Checks if this service can be performed on the given vehicle.
     */
    public boolean isCompatibleWith(Vehicle vehicle){
        if (this.compatiblePowertrain == null){
            return true; // Compatible with all
        }
        return this.compatiblePowertrain.equals(vehicle.getPowertrain()); // Use .equals() for strings
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
}

