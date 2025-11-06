import java.util.ArrayList;
import java.util.List;

/**
 * Manages the shop's inventory of parts.
 */
public class Inventory{
    private List<Part> parts;

    public Inventory(){
        this.parts = new ArrayList<>();
    }


    public void addPart(Part part){
        parts.add(part);
    }


    public List<Part> getParts(){
        return this.parts;
    }
    
    /**
     * Finds a part in the list by its part number.
     */
    public Part getPart(String partNumber){
        for (Part p : parts){
            if (p.getPartNumber().equals(partNumber)){
                return p;
            }
        }
        return null;
    }

    /**
     * Checks if a sufficient quantity of a part is in stock.
     */
    public boolean checkPartAvailability(String partNumber, int quantityNeeded){
        Part p = getPart(partNumber);
        if (p != null){
            return p.getStockLevel() >= quantityNeeded;
        }
        return false;
    }

    /**
     * Reserves a part, decreasing its stock.
     */
    public boolean reservePart(String partNumber, int quantityNeeded){
        Part p = getPart(partNumber);
        if (p != null && p.getStockLevel() >= quantityNeeded){
            p.decreaseStock(quantityNeeded);
            return true;
        } else{
            System.out.println("Error: Failed to reserve part " + partNumber + ". Not enough stock.");
            return false;
        }
    }

    /**
     * Restocks a part, increasing its stock.
     */
    public void restockPart(String partNumber, int quantityAdded){
        Part p = getPart(partNumber);
        if (p != null){
            p.increaseStock(quantityAdded);
        } else{
            System.out.println("Error: Cannot restock " + partNumber + ". Part does not exist in inventory.");
        }
    }
    
    /**
     * Prints the current stock of all parts.
     */
    public void printStock(){
        System.out.println("\n--- Current Inventory Stock ---");
        for (Part p : parts){
            System.out.println(p.getPartNumber() + ": " + p.getDescription() + " (Stock: " + p.getStockLevel() + ")");
        }
    }
}

