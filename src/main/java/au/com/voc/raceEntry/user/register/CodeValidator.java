//demo only
package au.com.voc.raceEntry.user.register;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CodeValidator implements ConstraintValidator<CodeMatch, String> {

    public final String CODE = "boats22";

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.equals(CODE);
    }

}
