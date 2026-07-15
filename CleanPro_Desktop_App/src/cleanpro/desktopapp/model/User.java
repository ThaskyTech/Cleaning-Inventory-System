public class User {
    public enum Role { ADMIN, CLEANER/* Staff*/ }

    private int userID;
    private String fullName;
    private String email;
    private String username;
    private String password;
    private Role role;

    public User(int userID, String fullName, String email, String username,
                String password, Role role){
        this.userID = userID;
        this.fullName = fullName.trim();
        this.email = email.trim();
        this.username = username.trim();
        this.password = password;
        this.role = role;
    }

    //Getters and Setters

    public int getUserID(){return userID;}

    public String getFullName(){return fullName;}
    public String getFirstName(){return fullName.split("\\s+")[0];}
    public String getLastName(){return fullName.split("\\s+")[1];}
    public void setFullName(String fullName){this.fullName = fullName;}

    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    public String getUsername(){return username;}
    public void setUsername( String username){this.username = username;}

    public Role getRole(){return role;};
    public void setRole(Role role){this.role = role;}

}
