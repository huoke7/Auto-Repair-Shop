/**
 * Auto Retair shop program
 * 2025 Nov. 14
 * Author Eric Huo, Eric Chen
 * The program is use for a car repair shop. Provide manager, mechanic and customer to log in.
 */


import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;

/**
 * Main class
 */
public class AutoShop{

    private Inventory inventory = new Inventory();
    private List<Service> serviceCatalog = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<WorkOrder> allWorkOrders = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();
    
    private static final String USERS_FILE = "data/users.csv"; 
    private static final String PARTS_FILE = "data/parts.csv";
    private static final String SERVICES_FILE = "data/services.csv";
    private static final String VEHICLES_FILE = "data/vehicles.csv";

    /**
     * username/password
     * @return User object on success, or null on failure.
     */
    public User attemptLogin(String username, String password){
        User user = findUser(username);
        
        if (user == null){
            return null;
        }
        
        if (user.checkPassword(password)){
            return user;
        } else{
            return null;
        }
    }

    /**
     * This method is called by LoginFrame after a successful login.
     */
    public void showUserDashboard(User loggedInUser){
        JFrame mainFrame = new JFrame("Auto Shop Management System");
        mainFrame.setSize(800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        
        JPanel userPanel = null;
        if (loggedInUser instanceof Customer){
            userPanel = new CustomerDashboardPanel((Customer) loggedInUser, this);
            mainFrame.setTitle("Customer Dashboard - " + loggedInUser.getUsername());
            
        } else if (loggedInUser instanceof Manager){
            userPanel = new ManagerDashboardPanel((Manager) loggedInUser, this);
            mainFrame.setTitle("Manager Dashboard - " + loggedInUser.getUsername());
            
        } else if (loggedInUser instanceof Mechanic){
             userPanel = new MechanicDashboardPanel((Mechanic) loggedInUser, this);
            mainFrame.setTitle("Mechanic Dashboard - " + loggedInUser.getUsername());
        }

        if (userPanel != null){
            mainFrame.add(userPanel);
            mainFrame.setVisible(true);
        } else{
            JOptionPane.showMessageDialog(null, 
                "Login successful for " + loggedInUser.getUsername() + 
                "\nDashboard for role " + loggedInUser.getRole() + " is not yet built.");
            new LoginFrame(this);
        }
    }

    
    public void loadDataFromFiles(){
        System.out.println("[Loading data from files...]");
        try{
            loadUsers(USERS_FILE);
            loadParts(PARTS_FILE);
            loadServices(SERVICES_FILE);
            loadVehicles(VEHICLES_FILE);
            System.out.println("[Data loading complete.]");
        } catch (IOException e){
            System.err.println("Error reading data files: " + e.getMessage());
        }
    }

    /**
     * This method is used to load users information
     * @param filename
     * @throws IOException
     */

    private void loadUsers(String filename) throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while ((line = br.readLine()) != null){
                String[] data = line.split(",");
                String role = data[0];
                String username = data[1];
                String password = data[2];
                String contact = data[3];

                switch (role){
                    case "CUSTOMER":
                        users.add(new Customer(username, password, contact));
                        break;
                    case "MECHANIC":
                        users.add(new Mechanic(username, password, contact));
                        break;
                    case "MANAGER":
                        users.add(new Manager(username, password, contact));
                        break;
                }
            }
            System.out.println("Loaded " + users.size() + " users.");
        }
    }

    /**
     * This method is used to load parts information
     * @param filename
     * @throws IOException
     */
    
    private void loadParts(String filename) throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while ((line = br.readLine()) != null){
                try{
                    String[] data = line.split(",");
                    String partNum = data[0];
                    String desc = data[1];
                    int stock = Integer.parseInt(data[2]);
                    double price = Double.parseDouble(data[3]);
                    inventory.addPart(new Part(partNum, desc, stock, price));
                } catch (NumberFormatException e){
                    System.err.println("Skipping bad part data: " + line);
                }
            }
        }
    }

    /**
     * This method is used to load services information
     * @param filename
     * @throws IOException
     */

    private void loadServices(String filename) throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while ((line = br.readLine()) != null){
                try{
                    String[] data = line.split(",");
                    String serviceID = data[0];
                    String desc = data[1];
                    double hours = Double.parseDouble(data[2]);
                    String powertrain;
                    if (data[3].equals("null")){
                        powertrain = null;
                    } else{
                        powertrain = data[3];
                    }
                    
                    List<PartRequirement> requirements = new ArrayList<>();
                    if (data.length > 4 && !data[4].isEmpty()){
                        String[] reqParts = data[4].split(";");
                        for (String req : reqParts){
                            String[] partInfo = req.split(":");
                            requirements.add(new PartRequirement(partInfo[0], Integer.parseInt(partInfo[1])));
                        }
                    }
                    serviceCatalog.add(new Service(serviceID, desc, hours, requirements, powertrain));
                } catch (Exception e){
                    System.out.println("Skipping bad service data: " + line);
                }
            }
            System.out.println("Loaded " + serviceCatalog.size() + " services.");
        }
    }

    /**
     * This method is used to load vehicles information
     * @param filename
     * @throws IOException
     */

    private void loadVehicles(String filename) throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while ((line = br.readLine()) != null){
                try{
                    String[] data = line.split(",");
                    String type = data[0];
                    String vin = data[1];
                    String make = data[2];
                    String model = data[3];
                    int year = Integer.parseInt(data[4]);
                    String powertrain = data[5];
                    String ownerUsername = data[6];
                    String extra;
                    if (data.length > 7){
                        extra = data[7]; 
                    } else{
                        extra = ""; 
                    }
                    User owner = findUser(ownerUsername);
                    if (owner instanceof Customer){
                        Customer customer = (Customer) owner;
                        
                        Vehicle newVehicle = null; 

                        switch (type){
                            case "Car":
                                newVehicle = new Car(vin, make, model, year, powertrain, customer, extra);
                                break;
                            case "Truck":
                                newVehicle = new Truck(vin, make, model, year, powertrain, customer, Double.parseDouble(extra), 0);
                                break;
                            case "Motorbike":
                                newVehicle = new Motorbike(vin, make, model, year, powertrain, customer, Integer.parseInt(extra));
                                break;
                        }

                        if (newVehicle != null) {
                            this.vehicles.add(newVehicle); // Add to the shop's main list
                        }
                        
                    } else{
                        System.err.println("Could not find customer " + ownerUsername + " for vehicle " + vin);
                    }
                } catch (Exception e){
                    System.err.println("Skipping bad vehicle data: " + line + " (" + e.getMessage() + ")");
                }
            }
        }
    }

    public void addVehicle(Vehicle vehicle) {
        if (this.vehicles == null) {
            this.vehicles = new ArrayList<>();
        }
        this.vehicles.add(vehicle);
    }
    
    /**
     * Saves the current list of all vehicles back to the vehicles.csv file.
     * This will overwrite the existing file.
     */
    public void saveVehiclesToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(VEHICLES_FILE))) {

            for (Vehicle v : this.vehicles) {
                String type = "";
                String extra = "";
                String vin = v.getVin();
                String make = v.getMake();
                String model = v.getModel();
                int year = v.getYear();
                String powertrain = v.getPowertrain();
                String ownerUsername = v.getOwner().getUsername();

                if (v instanceof Car) {
                    type = "Car";
                    extra = ((Car) v).getBodyType();
                } else if (v instanceof Truck) {
                    type = "Truck";
                    extra = String.valueOf(((Truck) v).getTowingCapacity());
                } else if (v instanceof Motorbike) {
                    type = "Motorbike";
                    extra = String.valueOf(((Motorbike) v).getEngineDisplacement());
                }

                String csvLine = String.join(",", 
                    type, vin, make, model, String.valueOf(year), 
                    powertrain, ownerUsername, extra
                );
                

                out.println(csvLine);
            }
            System.out.println("Successfully saved " + this.vehicles.size() + " vehicles to " + VEHICLES_FILE);

        } catch (IOException e) {
            System.err.println("Error saving vehicles to file: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "CRITICAL ERROR: Could not save vehicle data to file.\n" + e.getMessage(), 
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public User findUser(String username){
        for (User u : users){
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }
    
    /**
     * Creates a new customer account
     * @param username The new customer's username.
     * @param password The new customer's password.
     * @param contactInfo The new customer's contact info (email).
     * @return true if successful, false if user already exists.
     * @throws IOException if there is an error writing to the file.
     */

    public boolean createCustomerAccount(String username, String password, String contactInfo) throws IOException {
        if (findUser(username) != null) {
            System.out.println("Error: User " + username + " already exists.");
            return false;
        }

        String csvLine = String.join(",", "CUSTOMER", username, password, contactInfo);
        try (FileWriter fw = new FileWriter(USERS_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            
            out.println(csvLine);
            
        } catch (IOException e) {
            System.err.println("Error appending user to file: " + e.getMessage());
            throw e;
        }

        Customer newCustomer = new Customer(username, password, contactInfo);
        this.users.add(newCustomer);

        System.out.println("Successfully created new customer: " + username);
        return true;
    }

    public Service findService(String serviceID){
        for (Service s : serviceCatalog){
            if (s.getServiceID().equals(serviceID)) return s;
        }
        return null;
    }
    
    public Customer findCustomer(String username){
        User u = findUser(username);
        if (u instanceof Customer){
            return (Customer) u;
        }
        return null;
    }

    public Mechanic findMechanic(String username){
        User u = findUser(username);
        if (u instanceof Mechanic){
            return (Mechanic) u;
        }
        return null;
    }

    public Vehicle findVehicle(Customer c, String vin){
        for (Vehicle v : c.getVehicles()){
            if (v.getVin().equalsIgnoreCase(vin)){
                return v;
            }
        }
        return null;
    }

    public WorkOrder findWorkOrder(String woID){
        for (WorkOrder wo : allWorkOrders){
            if (wo.getWorkOrderID().equalsIgnoreCase(woID)){
                return wo;
            }
        }
        return null;
    }

    public Inventory getInventory(){
        return inventory;
    }

    public List<WorkOrder> getAllWorkOrders(){
        return allWorkOrders;
    }
    
    public List<Service> getServiceCatalog(){
        return serviceCatalog;
    }

    public void startApp(){
        AutoShop shop = new AutoShop();
        shop.loadDataFromFiles();
        new LoginFrame(shop); 
    }


    ////// void main
    public static void main(String[] args){
    System.out.println("Auto Shop Management System Starting");
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                AutoShop shop = new AutoShop();
                shop.loadDataFromFiles();
                new LoginFrame(shop); 
            }
        });
    }

}