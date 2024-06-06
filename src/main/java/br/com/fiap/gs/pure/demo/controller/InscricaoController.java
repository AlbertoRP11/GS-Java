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

import br.com.fiap.gs.pure.demo.model.Inscricao;
import br.com.fiap.gs.pure.demo.repository.InscricaoRepository;
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
@Tag(name = "inscricao", description = "Endpoint relacionados com inscricoões de eventos")
public class InscricaoController {

    @Autowired
    InscricaoRepository inscricaoRepository;

    @GetMapping
    @Cacheable
    @Operation(summary = "Lista todas as inscrições cadastradas no sistema.", description = "Endpoint que retorna um array de objetos do tipo inscrições com todas as inscrições do usuário atual")
    public List<Inscricao> index() {
        return inscricaoRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @CacheEvict(allEntries = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Erro de validação da inscrição"),
            @ApiResponse(responseCode = "201", description = "inscrição efetuada com sucesso")
    })
    public Inscricao create(@RequestBody @Valid Inscricao inscricao) {
        log.info("cadastrando inscricao: {}", inscricao);
        return inscricaoRepository.save(inscricao);
    }

    @GetMapping("{id}")
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
    public void destroy(@PathVariable Long id) {
        log.info("apagando inscrição {}", id);

        verificarSeExisteInscricao(id);
        inscricaoRepository.deleteById(id);
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
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

