package cleanpro.desktopapp.controller;

import cleanpro.desktopapp.model.Role;
import cleanpro.desktopapp.model.User;
import cleanpro.desktopapp.service.LoginService;
import cleanpro.desktopapp.service.exceptions.DuplicateEntryException;
import cleanpro.desktopapp.service.exceptions.ValidationException;

import java.util.List;

public class LoginController {
    private final LoginService loginService = new LoginService();

    public User login(String username, String password) throws ValidationException {
        return loginService.login(username, password);
    }

    public User register(String firstName, String lastName, String email,
                         String phoneNumber, String username, String password,
                         int roleId)
            throws DuplicateEntryException, ValidationException {
        return loginService.register(firstName, lastName, email, phoneNumber,
                username, password, roleId);
    }

    public List<Role> getAllRoles() {
        return loginService.getAllRoles();
    }
}
