/**
 * Motorbike calss, extend form vehicle
 */
public class Motorbike extends Vehicle{
    private int engineDisplacement;

    public Motorbike(String vin, String make, String model, int year, String powertrain, Customer owner, int engineDisplacement){
        super(vin, make, model, year, powertrain, owner);
        this.engineDisplacement = engineDisplacement;
    }

    @Override
    public String getVehicleDetails(){
        return getYear() + " " + getMake() + " " + getModel() + " (" + engineDisplacement + "cc, " + getPowertrain() + ")";
    }

    /**
     * Getter for engineDisplacement, needed for saving to CSV.
     * @return The engine displacement in cc.
     */
    public int getEngineDisplacement() {
        return engineDisplacement;
    }
}