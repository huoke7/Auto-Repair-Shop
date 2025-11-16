/**
 * Car calss, extend form vehicle
 * 
 */
public class Car extends Vehicle{
    private String bodyType;

    public Car(String vin, String make, String model, int year, String powertrain, Customer owner, String bodyType){
        super(vin, make, model, year, powertrain, owner);
        this.bodyType = bodyType;
    }

    @Override
    public String getVehicleDetails(){
        return getYear() + " " + getMake() + " " + getModel() + " (" + bodyType + ", " + getPowertrain() + ")";
    }

    /**
     * Getter for the bodyType, needed for saving to CSV.
     * @return The body type of the car.
     */
    public String getBodyType() {
        return bodyType;
    }
}