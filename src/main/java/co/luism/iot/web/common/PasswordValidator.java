package co.luism.iot.web.common;

import co.luism.diagnostics.webmanager.LanguageManager;
import com.vaadin.data.validator.AbstractValidator;

/**
 * Created by luis on 10.12.14.
 */
// Validator for validating the passwords
public final class PasswordValidator extends AbstractValidator<String> {

    public PasswordValidator() {
        super(LanguageManager.getInstance().getValue("WRONG_PASSWORD_TEXT"));
    }

    @Override
    protected boolean isValidValue(String value) {
        //
        // Password must be at least 4 characters long and contain at least
        // one number
        //
        if (value != null
                && (value.length() < 4 || !value.matches(".*\\d.*"))) {
            return false;
        }
        return true;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
