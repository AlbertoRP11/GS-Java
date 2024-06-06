package br.com.fiap.gs.pure.demo.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.gs.pure.demo.model.Evento;
import br.com.fiap.gs.pure.demo.repository.EventoRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("evento")
@Slf4j
@Tag(name = "Eventos", description = "Endpoint relacionado com os eventos")
public class EventoController {

    @Autowired
    EventoRepository repository;

    @Autowired
    PagedResourcesAssembler<Evento> pageAssembler;
    @Autowired
    private EventoRepository eventoRepository;

    @GetMapping
    @Operation(summary = "Lista todos os eventos cadastrados no sistema.",
            description = "Endpoint que retorna um array de objetos do tipo evento")
    public PagedModel<EntityModel<Evento>> index(
        @RequestParam(required = false) String cidade,
        @RequestParam(required = false) Integer mes,
        @ParameterObject @PageableDefault(sort = "dataEvento", direction = Direction.DESC) Pageable pageable
    ){
        Page<Evento> page = null;

        if (mes != null && cidade != null){
            page = repository.findByCidadeAndMes(cidade, mes, pageable);
        } else if (mes != null){
            page = repository.findByMes(mes, pageable);
        } else if (cidade != null){
            page = repository.findByCidadeIgnoreCase(cidade, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        return pageAssembler.toModel(page, Evento::toEntityModel);
    }

    @GetMapping("maior")
    @Operation(summary = "Mostra o evento que dá mais pontos ao usuário que participar",
                description = "Retorna um objeto do tipo evento")
    public Evento getMaior(@PageableDefault(size = 1, sort = "pontos", direction = Direction.DESC) Pageable pageable){
        return repository.findAll(pageable).getContent().get(0);
    }

    @GetMapping("ultima")
    @Operation(summary = "Mostra o último evento cadastrado",
            description = "Retorna um objeto do tipo evento")
    public Evento getUltima(){
        var pageable = PageRequest.of(0, 1, Direction.DESC, "dataEvento");
        return repository.findAll(pageable).getContent().get(0);
    }

    @GetMapping("menor")
    @Operation(summary = "Mostra o evento que dá menos pontos ao usuário que participar",
            description = "Retorna um objeto do tipo evento")
    public Evento getMenor(){
        return repository.findFirstByOrderByPontos();
    }

    @GetMapping("total-por-cidade")
    @Operation(summary = "Mostra os eventos disponíveis com base na cidade",
            description = "Retorna uma lista de objetos do tipo evento")
    public List<TotalPorCidade> getTotalPorCidade(){
        var eventos = repository.findAll();

        Map<String, BigDecimal> collect = eventos.stream()
            .collect(
                Collectors.groupingBy(
                    Evento::getCidade,
                    Collectors.reducing(BigDecimal.ZERO, e -> BigDecimal.valueOf(e.getPontos()), BigDecimal::add)
                )
            );

        return collect.entrySet().stream()
            .map(e -> new TotalPorCidade(e.getKey(), e.getValue()))
            .toList();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Cadastra um evento no sistema")
    public ResponseEntity<Evento> create(@RequestBody @Valid Evento evento){
        repository.save(evento);
        return ResponseEntity.created(
                    evento.toEntityModel().getLink("self").get().toUri()
                ).body(evento);
    }

    @GetMapping("{id}")
    @Operation(summary = "Busca um evento pelo id",
            description = "Retorna um objeto do tipo evento")
    public EntityModel<Evento> get(@PathVariable Long id){
        var evento = repository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Evento não encontrado")
        );

        return evento.toEntityModel();
    }

    @DeleteMapping("{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Apaga um evento do sistema")
    public ResponseEntity<Object> destroy(@PathVariable Long id){
        repository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Evento não encontrado")
        );

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualiza os dados de um evento no sistema com base no id.")
    public Evento update(@PathVariable Long id, @RequestBody Evento evento){
        log.info("atualizando usuario id {} para {}", id, evento);

        verificarSeExisteEvento(id);

        evento.setId(id);
        return eventoRepository.save(evento);
    }

    private void verificarSeExisteEvento(Long id) {
        eventoRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado" )
                );
    }

    private record TotalPorCidade(String cidade, BigDecimal pontos){}
}