/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.password;

import org.passay.CharacterCharacteristicsRule;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.RuleResult;

public class PassayPasswordValidator {

    public static final int MIN_LENGTH = 12;
    public static final int MAX_LENGTH = 64;
    public static final int MIN_LOWER_CASE = 1;
    public static final int MIN_UPPER_CASE = 1;
    public static final int MIN_DIGIT = 1;
    public static final int MIN_SPECAL = 1;

    public static RuleResult validate(String value) {
        org.passay.PasswordValidator validator = new org.passay.PasswordValidator(
                // https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html#implement-proper-password-strength-controls
                new LengthRule(MIN_LENGTH, MAX_LENGTH),
                new CharacterCharacteristicsRule(3,
                        new CharacterRule(EnglishCharacterData.LowerCase, MIN_LOWER_CASE),
                        new CharacterRule(EnglishCharacterData.UpperCase, MIN_UPPER_CASE),
                        new CharacterRule(EnglishCharacterData.Digit, MIN_DIGIT),
                        new CharacterRule(EnglishCharacterData.Special, MIN_SPECAL))
        );
        RuleResult result = validator.validate(new PasswordData(value));
        return result;
    }
}
