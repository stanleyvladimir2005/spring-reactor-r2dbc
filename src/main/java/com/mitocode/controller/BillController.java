package com.mitocode.controller;

import com.google.gson.Gson;
import com.mitocode.model.Bill;
import com.mitocode.model.BillItem;
import com.mitocode.service.IBillService;
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
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static reactor.function.TupleUtils.function;

@RestController
@RequestMapping({"/v1/bills"})
public class BillController {

    @Autowired
    private IBillService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Bill>>> findAll() {
        Flux<Bill> fxBilles = service.findAll();
        return Mono.just(ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fxBilles)
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Bill>> findById(@PathVariable("id") Integer id){
        return service.findById(id) //Mono<Bill>
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                ) //Mono<ResponseEntity<Bill>>
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ServerResponse> save(@Valid @RequestBody Bill bill,  final ServerRequest req){
        Mono<Bill> monoBill = req.bodyToMono(Bill.class);
        return monoBill.
                map( bill1 -> {
                    Gson gsonn = new Gson();
                    BillItem[] itemsArray = gsonn.fromJson(bill.getItems(), BillItem[].class);
                    List<BillItem> items = Arrays.asList(itemsArray);
                    bill.setItemsList(items);
                    return bill1;
                })
                .flatMap(service::saveTransactional)
                .flatMap(bl -> ServerResponse.created(URI.create(req.uri().toString().concat("/").concat(String.valueOf(bl.getId()))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(bl))
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Bill>> update(@Valid @RequestBody Bill bill, @PathVariable("id") Integer id){
        Mono<Bill> monoBill = Mono.just(bill);
        Mono<Bill> monoBD = service.findById(id);

        return monoBD
                .zipWith(monoBill, (bd, pl) -> {
                    bd.setId(id);
                    bd.setDescription(pl.getDescription());
                    bd.setObservation(pl.getObservation());
                    bd.setItems(pl.getItems());
                    bd.setCreateIn(pl.getCreateIn());
                    bd.setIdClient(pl.getIdClient());
                    bd.setItemsList(pl.getItemsList());
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
    public Mono<EntityModel<Bill>> listByHateoas(@PathVariable("id") Integer id){
        //localhost:8080/Billes/60779cc08e37a27164468033
        Mono<Link> link1 =linkTo(methodOn(BillController.class).findById(id)).withSelfRel().toMono();
        Mono<Link> link2 =linkTo(methodOn(BillController.class).findById(id)).withSelfRel().toMono();
        return link1.zipWith(link2)
                .map(function((left, right) -> Links.of(left, right)))
                .zipWith(service.findById(id), (lk, p) -> EntityModel.of(p, lk));
    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<Bill>>> listPagebale(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size	){
        Pageable pageRequest = PageRequest.of(page, size);
        return service.listPage(pageRequest)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                )
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }
}
