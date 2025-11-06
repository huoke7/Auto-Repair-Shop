/**
 * the class to hold a part requirement 
 */
public class PartRequirement{
    private String partNumber;
    private int quantity;

    public PartRequirement(String partNumber, int quantity){
        this.partNumber = partNumber;
        this.quantity = quantity;
    }

    public String getPartNumber(){
        return partNumber;
    }

    public int getQuantity(){
        return quantity;
    }
}
