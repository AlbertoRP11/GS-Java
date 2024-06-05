package br.com.fiap.gs.pure.demo.repository;

import br.com.fiap.gs.pure.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
