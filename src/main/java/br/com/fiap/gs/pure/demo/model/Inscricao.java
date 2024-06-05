package br.com.fiap.gs.pure.demo.model;

import br.com.fiap.gs.pure.demo.enums.StatusInscricao;
import java.time.LocalDate;

public class Inscricao {
    private Long id;
    private LocalDate dataInscricao;
    private StatusInscricao statusInscricao;
    private Usuario usuario;
    private Evento evento;
}
