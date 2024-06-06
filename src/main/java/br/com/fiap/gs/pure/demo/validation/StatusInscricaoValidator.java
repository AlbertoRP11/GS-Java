package br.com.fiap.gs.pure.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StatusInscricaoValidator implements ConstraintValidator<StatusInscricao, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equals("PENDENTE") || value.equals("INSCRITO") || value.equals("CONCLUIDA");
    }

}
