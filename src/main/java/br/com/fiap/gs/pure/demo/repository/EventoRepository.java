package br.com.fiap.gs.pure.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.fiap.gs.pure.demo.model.Evento;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    Page<Evento> findByCidadeIgnoreCase(String cidade, Pageable pageable);

    @Query("SELECT e FROM Evento e WHERE MONTH(e.dataEvento) = :mes")
    Page<Evento> findByMes(@Param("mes") Integer mes, Pageable pageable);

    @Query("SELECT e FROM Evento e WHERE e.cidade = :cidade AND MONTH(e.dataEvento) = :mes")
    Page<Evento> findByCidadeAndMes(@Param("cidade") String cidade, @Param("mes") Integer mes, Pageable pageable);

    Evento findFirstByOrderByPontos();
}