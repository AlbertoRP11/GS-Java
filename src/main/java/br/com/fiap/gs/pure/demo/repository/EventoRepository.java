package br.com.fiap.gs.pure.demo.repository;

import br.com.fiap.gs.pure.demo.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    //adicionar filtro por data
}
