package br.com.fiap.gs.pure.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "{usuario.nome.notblank}")
    @Size(min = 3, message =  "{usuario.nome.size}")
    private String nome;

    @NotBlank(message = "{usuario.email.notblank}")
    @Email(message = "{usuario.email.formato}")
    private String email;

    @NotBlank(message = "{usuario.senha.not.blank}")
    @Size(min = 6, max = 6, message = "{usuario.senha.size}")
    private String senha;

    @PositiveOrZero(message = "{usuario.pontos.positiveorzero}")
    private int pontos;

//    public EntityModel<Usuario> toEntityModel() {
//        return EntityModel.of(
//                this,
//                linkTo(methodOn(UsuarioController.class).get(id)).withSelfRel(),
//                linkTo(methodOn(UsuarioController.class).destroy(id)).withRel("delete"),
//                linkTo(methodOn(UsuarioController.class).index()).withRel("contents")
//        );
//    }
}
