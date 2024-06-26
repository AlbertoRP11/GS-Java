package br.com.fiap.gs.pure.demo.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

import br.com.fiap.gs.pure.demo.model.Evento;
import br.com.fiap.gs.pure.demo.model.Inscricao;
import br.com.fiap.gs.pure.demo.model.Usuario;
import br.com.fiap.gs.pure.demo.repository.EventoRepository;
import br.com.fiap.gs.pure.demo.repository.InscricaoRepository;
import br.com.fiap.gs.pure.demo.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("inscricao")
@Slf4j
@CacheConfig(cacheNames = "inscricao")
@Tag(name = "Inscrição", description = "Endpoint relacionados com as inscrições")
public class InscricaoController {

    @Autowired
    InscricaoRepository inscricaoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    EventoRepository eventoRepository;

    @GetMapping
    @Cacheable
    @Operation(summary = "Lista todas as inscrições cadastradas no sistema.",
            description = "Endpoint que retorna um array de objetos do tipo inscrições com todas as inscrições do usuário atual")
    public List<Inscricao> index() {
        return inscricaoRepository.findAll();
    }

@PostMapping
@ResponseStatus(CREATED)
@CacheEvict(allEntries = true)
@Operation(summary = "Cadastra uma inscrição no sistema.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Erro de validação da inscrição"),
        @ApiResponse(responseCode = "201", description = "Inscrição efetuada com sucesso")
})
public Inscricao create(@RequestBody @Valid Inscricao inscricao) {
    log.info("Cadastrando inscrição: {}", inscricao);

    Long userId = inscricao.getUsuario().getId();
    Long eventId = inscricao.getEvento().getId();

    // Busca o usuário e o evento pelo ID no banco de dados
    Usuario usuario = usuarioRepository.findById(userId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    Evento evento = eventoRepository.findById(eventId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));

    // Atribui o usuário e o evento encontrados à inscrição
    inscricao.setUsuario(usuario);
    inscricao.setEvento(evento);

    int pontosUsuario = usuario.getPontos() + evento.getPontos();
    usuario.setPontos(pontosUsuario);
    usuarioRepository.save(usuario);

    // Salva a inscrição no banco de dados com os dados de usuário e evento corretos
    return inscricaoRepository.save(inscricao);
}

    @GetMapping("{id}")
    @Operation(summary = "Busca uma inscrição pelo id.",
            description = "Endpoint que retorna um inscrição com base em seu id.")
    public ResponseEntity<Inscricao> get(@PathVariable Long id) {
        log.info("Buscar por id: {}", id);

        return inscricaoRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Apaga uma inscrição do sistema.")
    public void destroy(@PathVariable Long id) {
        log.info("apagando inscrição {}", id);

        verificarSeExisteInscricao(id);
        inscricaoRepository.deleteById(id);
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(summary = "Atualiza os dados de uma inscrição no sistema com base no id.")
    public Inscricao update(@PathVariable Long id, @RequestBody Inscricao inscricao) {
        log.info("atualizando inscrição id {} para {}", id, inscricao);

        verificarSeExisteInscricao(id);

        inscricao.setId(id);
        return inscricaoRepository.save(inscricao);

    }

    private void verificarSeExisteInscricao(Long id) {
        inscricaoRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "inscrição não encontrada"));
    }

}

