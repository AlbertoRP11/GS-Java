package br.com.fiap.gs.pure.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import br.com.fiap.gs.pure.demo.controller.EventoController;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.hateoas.EntityModel;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Evento {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "{evento.nome.notblank}")
    private String nomeEvento;
    @JsonFormat (pattern = "dd/MM/yyyy")
    private LocalDate dataEvento;
    @NotBlank(message = "{evento.descricao.notblank}")
    private String descricao;
    //duração do evento em forma de número
    private LocalDateTime horarioInicio;
    private LocalDateTime horarioTermino;
    @NotBlank(message = "{evento.praia.notblank}")
    @Size(min = 3, message = "{evento.praia.size}")
    private String praia;
    @NotBlank(message = "{evento.cidade.notblank}")
    @Size(min = 3, message = "{evento.cidade.size}")
    private String cidade;
    @Positive(message = "{evento.pontos.positive}")
    private int pontos;
    @ManyToOne
    private Usuario organizador;

    public EntityModel<Evento> toEntityModel(){
        return EntityModel.of(
            this,
            linkTo(methodOn(EventoController.class).get(id)).withSelfRel(),
            linkTo(methodOn(EventoController.class).index(null, null, null)).withRel("all")
        );
    }

}
