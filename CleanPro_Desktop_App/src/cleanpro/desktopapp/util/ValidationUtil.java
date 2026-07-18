package cleanpro.desktopapp.util;

public class ValidationUtil {

    private ValidationUtil() {
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

    public static boolean isNonNegative(int value) {
        return value >= 0;
    }
}
