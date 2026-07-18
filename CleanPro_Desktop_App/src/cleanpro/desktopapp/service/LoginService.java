package cleanpro.desktopapp.service;

import cleanpro.desktopapp.dao.RoleDAO;
import cleanpro.desktopapp.dao.UserDAO;
import cleanpro.desktopapp.model.Role;
import cleanpro.desktopapp.model.User;
import cleanpro.desktopapp.service.exceptions.DuplicateEntryException;
import cleanpro.desktopapp.service.exceptions.ValidationException;
import cleanpro.desktopapp.util.PasswordUtil;
import cleanpro.desktopapp.util.ValidationUtil;
import java.time.LocalDateTime;
import java.util.List;

public class LoginService{
    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private static final int INT_ID_PLACEHOLDER = 0; //NB:To be replaced once Marco is done with DAO
    
    public User login(String username, String password)throws ValidationException{
        if(ValidationUtil.isNullOrBlank(username) || ValidationUtil.isNullOrBlank(password)){
            throw new ValidationException("Username and password are required.");
        }
        
        //validate user
        User user = userDAO.getByUsername(username);
        if(user == null){throw new ValidationException("Invalid username or password.");}
        if(user.isActive()){throw new ValidationException("This account has been deactivated.");}
        if(!PasswordUtil.verifyPassword(password, user.getPasswordHash())){throw new ValidationException("Invalid username or password.");}
        
        userDAO.updateLastLogin(user.getUserId(), localDateTime.now());
        
        return user;
    }
    
    public User register(String firstName, String lastName, String email, String phoneNumber,
                          String username, String password, int roleId)
            throws DuplicateEntryException, ValidationException {
        
        //some simple upfront validation before dao use
        if (ValidationUtil.isNullOrBlank(firstName) || ValidationUtil.isNullOrBlank(lastName)) {
            throw new ValidationException("First name and last name are required.");
        }
        if (ValidationUtil.isNullOrBlank(username) || ValidationUtil.isNullOrBlank(password)) {
            throw new ValidationException("Username and password are required.");
        }
        if (password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("Email address is not valid.");
        }
        if (roleDAO.getById(roleId) == null) {
            throw new ValidationException("Selected role does not exist.");
        }
        if (userDAO.usernameExists(username)) {
            throw new DuplicateEntryException("Username already exists.");
        }
        if (userDAO.emailExists(email)) {
            throw new DuplicateEntryException("Email already exists.");
        }

        String passwordHash = PasswordUtil.hashPassword(password);
        User newUser = new User(NEXT_ID_PLACEHOLDER, firstName, lastName, email, phoneNumber,
                username, passwordHash, roleId, true, null);
        userDAO.insert(newUser);
        return newUser;
    }
    
    public List<Role> getAllRoles() {
        return roleDAO.getAll();
    }
}
