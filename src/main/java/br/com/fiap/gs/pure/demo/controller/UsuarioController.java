package br.com.fiap.gs.pure.demo.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;


import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.fiap.gs.pure.demo.model.Usuario;
import br.com.fiap.gs.pure.demo.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("user")
@Slf4j
@Tag(name = "usuarios", description = "Endpoint relacionado com os usuários do CineWave")
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping
    @Operation(summary = "Lista todos os usuários cadastrados no sistema.",
            description = "Endpoint que retorna um array de objetos do tipo usuário")
    public List<Usuario> index(){
        return usuarioRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Cadastra um usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Erro de validação do usuário"),
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso")
    })
    public Usuario create(@RequestBody @Valid Usuario usuario) {
        log.info("cadastrando Usuario: {}", usuario);
        return usuarioRepository.save(usuario);
    }

    @GetMapping("{id}")
    @Operation(summary = "Busca um usuário pelo id.",
            description = "Endpoint que retorna um usuário com base em seu id.")
    public ResponseEntity<Usuario> get(@PathVariable Long id) {
        log.info("Buscar por id: {}", id);

        return usuarioRepository
                    .findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/login/{email}")
    @Operation(summary = "Busca um usuário pelo email.",
            description = "Endpoint que retorna um usuário com base em seu email.")
    public ResponseEntity<Usuario> findByEmail(@PathVariable String email) {
        return usuarioRepository
                .findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Apaga um usuário do sistema.")
    public void destroy(@PathVariable Long id) {
        log.info("apagando Usuario {}", id);

        verificarSeExisteUsuario(id);
        usuarioRepository.deleteById(id);
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualiza os dados de um usuário no sistema com base no id.")
    public Usuario update(@PathVariable Long id, @RequestBody Usuario usuario){
        log.info("atualizando usuario id {} para {}", id, usuario);
        
        verificarSeExisteUsuario(id);

        usuario.setId(id);
        return usuarioRepository.save(usuario);

    }

    private void verificarSeExisteUsuario(Long id) {
        usuarioRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não encontrada" )
            );
    }
}
