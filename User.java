/*
 * Abstract class.
 */
public abstract class User{
    private String username;
    private String password;
    private String role;
    private String contactInfo;

    public User(String username, String password, String role, String contactInfo){
        this.username = username;
        this.password = password;
        this.role = role;
        this.contactInfo = contactInfo;
    }


    public boolean checkPassword(String passwordAttempt){
        return this.password.equals(passwordAttempt);
    }

    public String getUsername(){
        return username;
    }
    public String getRole(){
        return role;
    }
    public String getContactInfo(){
        return contactInfo;
    }
}