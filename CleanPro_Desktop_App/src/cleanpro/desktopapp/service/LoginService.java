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

public class LoginService {
    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private static final int ID_PLACEHOLDER = 0;

    // LOGIN

    public User login(String username, String password)
            throws ValidationException {
        if (ValidationUtil.isNullOrBlank(username)
                || ValidationUtil.isNullOrBlank(password)) {
            throw new ValidationException(
                    "Username and password are required."
            );
        }

        User user =
                userDAO.getByUsername(
                        username.trim()
                );

        if (user == null) {
           throw new ValidationException(
                    "Invalid username or password."
            );
        }

        if (!user.isActive()) {
            throw new ValidationException(
                    "This account has been deactivated."
            );
        }

        if (!PasswordUtil.verifyPassword(
                password,
                user.getPasswordHash())) {
            throw new ValidationException(
                    "Invalid username or password."
            );
        }

        userDAO.updateLastLogin(
                user.getUserId(),
                LocalDateTime.now()
        );

        return user;
    }

    // REGISTER USER

    public User register(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String username,
            String password,
            int roleId
            )

            throws DuplicateEntryException, ValidationException {
             validateRegistration(
                firstName,
                lastName,
                email,
                phoneNumber,
                username,
                password,
                roleId
        );

        if (userDAO.usernameExists(username)) {
            throw new DuplicateEntryException(
                    "Username already exists."
            );
        }

        if (userDAO.emailExists(email)) {
            throw new DuplicateEntryException(
                    "Email already exists."
            );
        }

        String passwordHash =
                PasswordUtil.hashPassword(password);

        User newUser =
                new User(
                        ID_PLACEHOLDER,
                        firstName,
                        lastName,
                        email,
                        phoneNumber,
                        username,
                        passwordHash,
                        roleId,
                        true,
                        null
                );

        userDAO.insert(newUser);
        return newUser;
    }

    // REGISTRATION VALIDATION

    private void validateRegistration(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String username,
            String password,
            int roleId
            )

            throws ValidationException {

        if (ValidationUtil.isNullOrBlank(firstName)
                || ValidationUtil.isNullOrBlank(lastName)) {
            throw new ValidationException(
                    "First name and last name are required."
            );
        }

        if (!firstName.matches("[a-zA-Z ]+")
                || !lastName.matches("[a-zA-Z ]+")) {

            throw new ValidationException(
                    "Names can only contain letters."
            );
        }

        if (ValidationUtil.isNullOrBlank(username)) {
            throw new ValidationException(
                    "Username is required."
            );
        }

        if (!username.matches("[a-zA-Z0-9_]{4,20}")) {
            throw new ValidationException(
                    "Username must be 4-20 characters and contain only letters, numbers, and underscores."
            );
        }

        if (ValidationUtil.isNullOrBlank(password)) {
            throw new ValidationException(
                    "Password is required."
            );
        }

        if (password.length() < 8) {
            throw new ValidationException(
                    "Password must be at least 8 characters long."
            );
        }

        if (!password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")
                || !password.matches(".*[0-9].*")) {
            throw new ValidationException(
                    "Password must contain uppercase, lowercase, and a number."
           );
        }

        if (ValidationUtil.isNullOrBlank(email)
                || !ValidationUtil.isValidEmail(email)) {
            throw new ValidationException(
                    "Email address is not valid."
            );
        }

        if (!ValidationUtil.isNullOrBlank(phoneNumber)
                && !phoneNumber.matches("\\d{10,15}")) {
            throw new ValidationException(
                    "Phone number must contain 10 to 15 digits."
            );
        }

        if (roleId <= 0
                || roleDAO.getById(roleId) == null) {
            throw new ValidationException(
                    "Selected role does not exist."
            );
        }
    }

    // GET ROLES

    public List<Role> getAllRoles() {
        return roleDAO.getAll();
    }
}