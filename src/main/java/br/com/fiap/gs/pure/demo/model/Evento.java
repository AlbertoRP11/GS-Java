package br.com.fiap.gs.pure.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Evento {

    private Long id;
    private String nomeEvento;
    private LocalDate dataEvento;
    private String descricao;
    private LocalDateTime horarioInicio;
    private LocalDateTime horarioTermino;
    private String praia;
    private String cidade;
    private int pontos;
    private Usuario organizador;

}
