package informatica.unical.spadafora.casanostra.model;

import java.util.regex.Pattern;

public class Validator {

    // ===================== Costanti per le regex =====================

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^+-]).{8,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{4,20}$");

    // ===================== Validazioni =====================

    public static boolean validateUsername(String username) {
        return USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean validatePassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean mancanoMaiuscole(String password) {
        return password.chars().noneMatch(Character::isUpperCase);
    }

    public static boolean mancanoMinuscole(String password) {
        return password.chars().noneMatch(Character::isLowerCase);
    }

    public static boolean mancanoNumeri(String password) {
        return password.chars().noneMatch(Character::isDigit);
    }

    public static boolean mancanoCaratteriSpeciali(String password) {
        return password.chars().noneMatch(ch -> "@$!%*?&#^+-".indexOf(ch) >= 0);
    }

    public static boolean lunghezzaNonValida(String username) {
        return username.length() < 4 || username.length() > 20;
    }

    public static boolean caratteriNonAmmessi(String username) {
        return !username.chars().allMatch(ch -> Character.isLetterOrDigit(ch) || ch == '-' || ch == '_');
    }
}