package com.mitocode.controller;

import com.mitocode.model.Client;
import com.mitocode.service.IClientService;
import com.mitocode.util.PageSupport;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;
import static reactor.function.TupleUtils.function;

@RestController
@RequestMapping({"/v1/clients"})
public class ClientController {

    @Autowired
    private IClientService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Client>>> findAll() {
        Flux<Client> fxclientes = service.findAll();
        return Mono.just(ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fxclientes)
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Client>> findById(@PathVariable("id") Integer id){
        return service.findById(id) //Mono<client>
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                ) //Mono<ResponseEntity<client>>
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Client>> save(@Valid @RequestBody Client client){
        return service.save(client)
                .map(d -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(d)
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Client>> update(@Valid @RequestBody Client client, @PathVariable("id") Integer id){
        Mono<Client> monoclient = Mono.just(client);
        Mono<Client> monoBD = service.findById(id);
        return monoBD
                .zipWith(monoclient, (bd, pl) -> {
                    bd.setId(id);
                    bd.setFirstName(pl.getFirstName());
                    bd.setLastName(pl.getLastName());
                    bd.setBirthday(pl.getBirthday());
                    bd.setUrlPhoto(pl.getUrlPhoto());
                    return bd;
                })
                .flatMap(service::update)
                .map(pl -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(pl))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") Integer id){
        return service.findById(id)
                .flatMap(p -> service.delete(p.getId()) // Mono<Void>  return service.eliminar(p.getId())
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<Client>> listByHateoas(@PathVariable("id") Integer id){
        //localhost:8080/clientes/60779cc08e37a27164468033
        Mono<Link> link1 =linkTo(methodOn(ClientController.class).findById(id)).withSelfRel().toMono();
        Mono<Link> link2 =linkTo(methodOn(ClientController.class).findById(id)).withSelfRel().toMono();
        return link1.zipWith(link2)
                .map(function((left, right) -> Links.of(left, right)))
                .zipWith(service.findById(id), (lk, p) -> EntityModel.of(p, lk));
    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<Client>>> listPagebale
            (@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size	){
        Pageable pageRequest = PageRequest.of(page, size);
        return service.listPage(pageRequest)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                )
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }
}
