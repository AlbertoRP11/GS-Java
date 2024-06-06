package br.com.fiap.gs.pure.demo.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.gs.pure.demo.model.Evento;
import br.com.fiap.gs.pure.demo.repository.EventoRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("evento")
@Slf4j
public class EventoController {

    @Autowired
    EventoRepository repository;

    @Autowired
    PagedResourcesAssembler<Evento> pageAssembler;

    @GetMapping
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
    public Evento getMaior(@PageableDefault(size = 1, sort = "pontos", direction = Direction.DESC) Pageable pageable){
        return repository.findAll(pageable).getContent().get(0);
    }

    @GetMapping("ultima")
    public Evento getUltima(){
        var pageable = PageRequest.of(0, 1, Direction.DESC, "dataEvento");
        return repository.findAll(pageable).getContent().get(0);
    }

    @GetMapping("menor")
    public Evento getMenor(){
        return repository.findFirstByOrderByPontos();
    }

    @GetMapping("total-por-cidade")
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
    public ResponseEntity<Evento> create(@RequestBody @Valid Evento evento){
        repository.save(evento);
        return ResponseEntity.created(
                    evento.toEntityModel().getLink("self").get().toUri()
                ).body(evento);
    }

    @GetMapping("{id}")
    public EntityModel<Evento> get(@PathVariable Long id){
        var evento = repository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Evento não encontrado")
        );

        return evento.toEntityModel();
    }

    @DeleteMapping("{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> destroy(@PathVariable Long id){
        repository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Evento não encontrado")
        );

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private record TotalPorCidade(String cidade, BigDecimal pontos){}
}