package br.com.fiap.gs.pure.demo.repository;

import br.com.fiap.gs.pure.demo.model.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {
}
