package com.group8.cleaninginventory.util;

// Shared static helper methods for validating input such as required fields and email format.
public class ValidationUtils {

    private ValidationUtils() {
    }

    public static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        return email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isPositive(int value) {
        return value > 0;
    }
}
