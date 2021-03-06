package app_.smartreceipt.digitaldocs.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checking if password is valid and according to the rules we set out
 */

public class PasswordValidator {

    private Pattern pattern;
    private Matcher matcher;

    private static final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*\\W).{8,40})";

    public PasswordValidator() {
        pattern = Pattern.compile(PASSWORD_PATTERN);
    }

    public boolean validate(final String password) {
        matcher = pattern.matcher(password);
        return matcher.find();
    }
}