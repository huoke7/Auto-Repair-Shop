import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class
 */
public abstract class Vehicle{
    private String vin;
    private String make;
    private String model;
    private int year;
    private String powertrain;
    private Customer owner;
    private List<WorkOrder> serviceHistory;

    public Vehicle(String vin, String make, String model, int year, String powertrain, Customer owner){
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.year = year;
        this.powertrain = powertrain;
        this.owner = owner;
        this.serviceHistory = new ArrayList<>();
        owner.addVehicle(this);
    }
    

    public abstract String getVehicleDetails();
    
    public void addServiceHistory(WorkOrder wo){
        this.serviceHistory.add(wo);
    }
    
    public String getVin(){
        return vin; 
    }
    public String getMake(){
        return make; 
    }
    public String getModel(){ 
        return model; 
    }
    public int getYear(){ 
        return year; 
    }
    public String getPowertrain(){ 
        return powertrain;
    }
    public Customer getOwner(){ 
        return owner; }
    public List<WorkOrder> getServiceHistory(){ 
        return serviceHistory; 
    }
}

