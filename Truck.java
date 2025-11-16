/**
 * Truck class, extend form vehicle
 */
public class Truck extends Vehicle{
    private double towingCapacity;
    private double cargoCapacity;

    public Truck(String vin, String make, String model, int year, String powertrain, Customer owner, double towingCapacity, double cargoCapacity){
        super(vin, make, model, year, powertrain, owner);
        this.towingCapacity = towingCapacity;
        this.cargoCapacity = cargoCapacity;
    }

    @Override
    public String getVehicleDetails(){
        return getYear() + " " + getMake() + " " + getModel() + " (Towing: " + towingCapacity + " lbs, " + getPowertrain() + ")";
    }

    /**
     * Getter for towingCapacity, needed for saving to CSV.
     * @return The towing capacity of the truck.
     */
    public double getTowingCapacity() {
        return towingCapacity;
    }
}