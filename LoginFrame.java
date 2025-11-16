import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame{

    private AutoShop shop; 
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame(AutoShop shop){
        this.shop = shop;

        //Window Setup
        setTitle("Auto Shop Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("")); 
        
        loginButton = new JButton("Login");
        panel.add(loginButton);

        //  Add the panel to the frame
        add(panel);

        loginButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                handleLoginAttempt();
            }
        });

        //  Make the window visible
        setVisible(true);
    }

    /**
     * Controller part of View.
     */
    private void handleLoginAttempt(){
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User loggedInUser = shop.attemptLogin(username, password);

        if (loggedInUser != null){
            this.dispose(); 
            
            shop.showUserDashboard(loggedInUser);
            
        } else{
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
