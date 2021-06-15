package OtherClasses;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CredentialValidation {


    /**
     * Verify that email is formatted correctly
     *  - Example: myemail@domain.com
     * @param email String
     * @param inputEmail EditText
     * @return  boolean
     * TRUE: Email is valid (matches expected format)
     * FALSE: Email is invalid.
     *
     */
    public boolean validateEmail(final String email, EditText inputEmail) {
        String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";

        // Check for empty strings or null
        if (email.length() == 0) {
            inputEmail.setError("Required field");
            return false;
        }

        // Check email format
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    /**
     * Verify that password is formatted correctly
     * @param password String
     * @return  boolean
     * TRUE: Password is at least 6 characters long, has at least one capital letter, a special character, and at least one number
     * FALSE: Password is too short, doesn't have a number, or doesn't have a capital letter
     * Matches only standard alpha-numeric characters
     * Update - 3/24/2020:
     * I think I fixed the validation but app crashes after going through
     */
    public boolean validatePassword(String password, EditText inputPassword) {
        // Regex used to enforce password requirements
        // Group labels are added for documentation purposes
        final String passwordRegex = "^(?:(?<Numbers>[0-9]{1,})|(?<lowerAlpha>[a-z]{1,})|(?<upperAlpha>[A-Z]{1,})|(?<Special>[^a-zA-Z0-9]{1,})){6,}$";

        // Flags for validation
        boolean isLength = false;
        boolean isCapital = false;
        boolean isNumber = false;
        boolean isSpecial = false;

        // Created a Pattern object
        Pattern regex = Pattern.compile(passwordRegex);

        // Created a Matcher object.
        Matcher stringMatcher = regex.matcher(password);

        // Initialize Error message string
        String errorMessage = "Missing Requirements: ";

        // Check for empty strings or null
        if (password.length() == 0) {
            inputPassword.setError("Required field");
            errorMessage = "Password Requirements:\n- At least 6 non-blank characters in length\n- At least 1 capital letter\n- At least 1 number\n- At least 1 special character";

            // Set error message
            inputPassword.setError(errorMessage);
            return false;
        }

        if (stringMatcher.find() == true) {

            // Check for length (i.e. < 6 characters long)
            if (!password.matches(passwordRegex)) {
                errorMessage = errorMessage + "\n- At least 6 characters required";
            }
            else { isLength = true; }

            // Check for a number
            if (stringMatcher.group(1) == null) {
                errorMessage = errorMessage + "\n- At least 1 number required";
            }
            else { isNumber = true; }

            // Check for a capital letter
            if (stringMatcher.group(2) == null || stringMatcher.group(3) == null) {
                errorMessage = errorMessage + "\n- At lest 1 lowercase and capital letter is required.";
            }
            else { isCapital = true; }

            // Check for a special character
            if (stringMatcher.group(4) == null) {
                errorMessage = errorMessage + "\n- At least 1 special character is required";
            }
            else { isSpecial = true; }

            // Return full error message
            if (!errorMessage.equals("Missing Requirements: ")) {
                inputPassword.setError(errorMessage);
            }
        }
        else {
            // Return general error message
            errorMessage = stringMatcher.find()  + " Password Requirements:\n- At least 6 characters in length\n- At least 1 capital and lowercase letter required\n- At least 1 number\n- At least 1 special character";

            // Set error message
            inputPassword.setError(errorMessage);
            return false;
        }

        // Return final response
        return isLength & isCapital & isNumber & isSpecial;
    }
}
