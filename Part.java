/**
 * Represents a single inventory Part.
 */
public class Part{
    private String partNumber;
    private String description;
    private int stockLevel;
    private double price;

    public Part(String partNumber, String description, int stockLevel, double price){
        this.partNumber = partNumber;
        this.description = description;
        this.stockLevel = stockLevel;
        this.price = price;
    }
    
    public void increaseStock(int amount){
        if (amount > 0){
            this.stockLevel += amount;
        }
    }
    
    /**
     * Decreases stock. Returns true on success, false if not enough stock.
     */
    public boolean decreaseStock(int amount){
        if (amount > 0 && this.stockLevel >= amount){
            this.stockLevel -= amount;
            return true;
        }
        return false;
    }


    public String getPartNumber(){ 
        return partNumber; 
    }
    public String getDescription(){ 
        return description; }
    public int getStockLevel(){ return stockLevel; 
    }
    public double getPrice(){ 
        return price; 
    }
}
