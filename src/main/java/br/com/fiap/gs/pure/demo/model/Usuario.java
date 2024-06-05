package br.com.fiap.gs.pure.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
    @NotBlank
    private String nome;
    @NotBlank
    private String email;
    @NotBlank
    private String senha;
    @Positive
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
