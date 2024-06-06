package br.com.fiap.gs.pure.demo.model;

import br.com.fiap.gs.pure.demo.validation.StatusInscricao;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inscricao {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInscricao;
    @NotBlank(message = "{inscricao.status.notblank}")
    @StatusInscricao(message = "{inscricao.status.opcoes}")
    private String statusInscricao;
    @ManyToOne
    private Usuario usuario;
    @ManyToOne
    private Evento evento;
}
