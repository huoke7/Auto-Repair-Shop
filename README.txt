
Author: Eric H, Eric C 
Time: 2025 Nov 16
Verson: final


This is a Java Swing program that simulates a management system for an auto repair shop. 
The program handles different user roles, manages inventory, tracks vehicle repairs from creation to invoicing, and uses CSV files to load initial data.

First, user use their uername and password to login their account, different rolls have different menu

Manager rolls:
Create Work Orders: Generate new work orders for customers and their vehicles.
Assign Mechanics:Assign a specific mechanic to an open work order.
Generate Invoices: Finalize a completed work order and generate a billable invoice.
Restock Inventory: Add stock to existing parts in the inventory.
Create Customers: Register new customer accounts directly from the manager panel.
View Reports: Generate a simple report of revenue and current inventory levels.


Mechanic rolls:
View Assigned Jobs: See a list of all work orders assigned to them.
Update Job Status: Mark individual services within a work order as "complete" and record the parts used.
Log Hours: Log the number of hours spent on a specific work order.

Customer rolls:
View My Vehicles: See a list of all their registered vehicles.
Add New Vehicle: Add a new Car, Truck, or Motorbike to their account.
View My Invoices: See a list of all their past and present invoices and their status (Paid/Unpaid).
Make Payment: Select an unpaid invoice and pay it.


How to Run:

1. Ensure all '.java' files and '.csv' files are in the same file.
2. Compile the Java files
3. Run the AutoShop class
4. The login window will appear


Each data are storted in each CSV file.

User information:
MANAGER,a_smith,pass,admin.smith@shop.com,
MECHANIC,m_bob,pass,bob-mechanic@shop.com,
MECHANIC,m_eric,pass,eirc-mechanic@shop.com,
CUSTOMER,c_jane,pass,jane.doe@email.com,
CUSTOMER,c_jeff,pass,jeff@email.com,

Car information:
Car,VIN123,Honda,Civic,2018,COMBUSTION,c_jane,Sedan
Truck,VIN456,Ford,F-150,2020,COMBUSTION,c_jane,10000
Motorbike,VIN789,Yamaha,MT-07,2022,COMBUSTION,c_jane,689
Car,VIN111,BYD,AH892,2025,ELECTRIC,c_jeff,Sedan

Serves information:
S-01,Oil Change,1.0,COMBUSTION,P-001:1;P-002:1
S-02,Brake Replacement,2.5,null,P-003:2
S-03,Wiper Replacement,0.5,null,P-004:2

Parts information;
P-001,Oil Filter,50,15.00
P-002,Synthetic Oil (5L),30,45.00
P-003,Brake Pad Set,20,80.00
P-004,Windshield Wiper,10,25.00