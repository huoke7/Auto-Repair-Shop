/**
 * Represents a Motorbike.
 * Extends from Vehicle.
 */
public class Motorbike extends Vehicle{
    private int engineDisplacement;

    public Motorbike(String vin, String make, String model, int year, String powertrain, Customer owner, int engineDisplacement){ // Changed from Powertrain to String
        super(vin, make, model, year, powertrain, owner);
        this.engineDisplacement = engineDisplacement;
    }

    @Override
    public String getVehicleDetails(){
        return getYear() + " " + getMake() + " " + getModel() + " (" + engineDisplacement + "cc, " + getPowertrain() + ")";
    }
}

