package com.mitocode.handler;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.google.gson.Gson;
import com.mitocode.model.Factura;
import com.mitocode.model.FacturaItem;
import com.mitocode.service.IFacturaService;
import com.mitocode.validators.RequestValidator;

import reactor.core.publisher.Mono;

@Component
public class FacturaHandler {

	@Autowired
	private IFacturaService service;
	
	@Autowired
	private Validator validador;
	
	@Autowired
	private RequestValidator validadorGeneral;
		
	public Mono<ServerResponse> listar(ServerRequest req){
		return ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.listar(), Factura.class);
	}
	
	
	public Mono<ServerResponse> listarPorId(ServerRequest req){		
		Integer id = Integer.parseInt(req.pathVariable("id"));
		return service.listarPorId(id)
				.flatMap(p -> ServerResponse
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
				)
				.switchIfEmpty(ServerResponse.notFound().build());			
	}
	
	public Mono<ServerResponse> registrar(ServerRequest req) {
	
		Mono<Factura> monoFactura = req.bodyToMono(Factura.class);

		return monoFactura
				.map(f-> {
					//AQUI USAR GSON, extraer el objeto y adicionarlo a 'p'					
					Gson gson = new Gson();
					FacturaItem[] itemsArray = gson.fromJson(f.getItems(), FacturaItem[].class);
					List<FacturaItem> items = Arrays.asList(itemsArray);
					f.setItemsList(items);					
					return f;
				})
				.flatMap(validadorGeneral::validate)
				//quizás este registrar deba cambiar a una implementacion propia que llame a dos insert
				.flatMap(service::registrarTransaccional)
				.flatMap(f -> ServerResponse.created(URI.create(req.uri().toString().concat("/").concat(String.valueOf(f.getId()))))
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(f))
				);
	}
	
	public Mono<ServerResponse> modificar(ServerRequest req) {
		
		Mono<Factura> monoPlato = req.bodyToMono(Factura.class);		
		Mono<Factura> monoBD = service.listarPorId(Integer.parseInt(req.pathVariable("id")));
		
		return monoBD
				.zipWith(monoPlato, (bd, p) -> {				
					bd.setId(p.getId());
					//bd.setCliente(p.getCliente());
					bd.setIdCliente(p.getIdCliente());
					bd.setDescripcion(p.getDescripcion());
					bd.setItems(p.getItems());
					bd.setObservacion(p.getObservacion());					
					return bd;
				})
				.flatMap(validadorGeneral::validate)								
				.flatMap(service::modificar)
				.flatMap(p -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
				)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> eliminar(ServerRequest req){		
		Integer id = Integer.parseInt(req.pathVariable("id"));
			
		return service.listarPorId(id)
				.flatMap(p -> service.eliminar(p.getId())
						.then(ServerResponse.noContent().build())
				)
				.switchIfEmpty(ServerResponse.notFound().build());			
	}
}