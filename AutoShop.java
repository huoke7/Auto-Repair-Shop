import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 * Main class
 */
public class AutoShop{

    private Inventory inventory = new Inventory();
    private List<Service> serviceCatalog = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<WorkOrder> allWorkOrders = new ArrayList<>();
    
    private static final String USERS_FILE = "users.csv"; 
    private static final String PARTS_FILE = "parts.csv";
    private static final String SERVICES_FILE = "services.csv";
    private static final String VEHICLES_FILE = "vehicles.csv";

    public static void main(String[] args){
        System.out.println("Auto Shop Management System Starting");
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                AutoShop shop = new AutoShop();
                shop.loadDataFromFiles();
                new LoginFrame(shop); 
            }
        });
        
        System.out.println("Main thread is finishing. GUI is now running.");
    }



    /**
     * username/password one time.
     *
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
     * It's responsible for opening the correct dashboard (Customer, Manager, or Mechanic).
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
            inventory.printStock();
        }
    }

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
                        switch (type){
                            case "Car":
                                new Car(vin, make, model, year, powertrain, customer, extra);
                                break;
                            case "Truck":
                                new Truck(vin, make, model, year, powertrain, customer, Double.parseDouble(extra), 0);
                                break;
                            case "Motorbike":
                                new Motorbike(vin, make, model, year, powertrain, customer, Integer.parseInt(extra));
                                break;
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



    public User findUser(String username){
        for (User u : users){
            if (u.getUsername().equals(username)) return u;
        }
        return null;
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
        return allWorkOrders;}
    public List<Service> getServiceCatalog(){
        return serviceCatalog;
    }

}