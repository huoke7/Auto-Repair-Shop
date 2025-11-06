/**
 * Represents a Truck.
 */
public class Truck extends Vehicle{
    private double towingCapacity;
    private double weight;

    public Truck(String vin, String make, String model, int year, String powertrain, Customer owner, double towingCapacity, double weight){
        super(vin, make, model, year, powertrain, owner);
        this.towingCapacity = towingCapacity;
        this.weight = weight;
    }

    public String getWeitht(String weight){
        return weight;
    }

    @Override
    public String getVehicleDetails(){
        return getYear() + " " + getMake() + " " + getModel() + " (Towing: " + towingCapacity + " lbs, " + getPowertrain() + ")";
    }
}

