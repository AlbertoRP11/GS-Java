package br.com.fiap.gs.pure.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Constraint(validatedBy = StatusInscricaoValidator.class)
@Retention(RUNTIME)
public @interface StatusInscricao {

    String message() default "Status inválido. O status da inscrição deve ser PENDENTE, INSCRITO ou CONCLUIDA.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
