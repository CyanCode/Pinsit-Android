package me.walkerc.pinsit;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by CChristie on 12/5/2017.
 */

public class Validator {
    public final static int PASSWORD_LENGTH_MIN = 6;
    public final static int NAME_LENGTH_MIN = 2;

    private Context context = null;

    public Validator(Context context) {
        this.context = context;
    }

    /**
     * Sends a verification email to the currently logged in user.
     */
    public static void sendEmailVerification() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.isEmailVerified()) {
            user.sendEmailVerification();
        }
    }

    /**
     * Checks whether the passed registration credentials are valid and, if not,
     * displays a Toast message to the screen with the issue.
     * Checks length of user's name and password, and whether both passwords match.
     * @param name User's name
     * @param password Password
     * @param confirmPassword Confirmed password
     * @return true if valid registration credentials, false otherwise.
     */
    public boolean validateRegistration(String name, String password, String confirmPassword) {
        return isPasswordLengthValid(password) &&
                isNameLengthValid(name) && doPasswordsMatch(password, confirmPassword);
    }

    public boolean isPasswordLengthValid(String password) {
        if (password.length() < PASSWORD_LENGTH_MIN) {
            //TODO Toast
            return false;
        }

        return true;
    }

    public boolean isNameLengthValid(String name) {
        if (name.length() < NAME_LENGTH_MIN) {
            //TODO Toast
            return false;
        }

        return true;
    }

    public boolean doPasswordsMatch(String pass1, String pass2) {
        if (!pass1.equals(pass2)) {
            //TODO Toast
            return false;
        }

        return true;
    }
}
