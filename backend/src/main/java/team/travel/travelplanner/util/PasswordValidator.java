package team.travel.travelplanner.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

    // Regular expression for a strong password: at least 8 characters long, contains at least one uppercase letter, one lowercase letter, one digit, and one special character
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean validate(String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}

