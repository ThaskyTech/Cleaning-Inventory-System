
package cleanpro.desktopapp.model;



public class Role {
    private int roleId;
    private String roleName;
    private String roleDescription;
    
    public Role(int roleId, String roleName, String roleDescription) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }
    
    //Getters and Setters
    
    public int getRoleId(){return roleId;}
    
    public String getRoleName(){return roleName;}
    public void setRoleName(String roleName){ this.roleName = roleName;}
    
    public String getRoleDescription(){ return roleDescription;}
    public void setRoleDescription(String roleDescription){ this.roleDescription = roleDescription;}
    
}
