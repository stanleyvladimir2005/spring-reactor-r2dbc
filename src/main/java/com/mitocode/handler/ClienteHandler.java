package com.mitocode.handler;

import java.net.URI;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.mitocode.model.Cliente;
import com.mitocode.service.IClienteService;
import com.mitocode.validators.RequestValidator;

import reactor.core.publisher.Mono;

@Component
public class ClienteHandler {

	@Autowired
	private IClienteService service;
	
	@Autowired
	private Validator validador;
	
	@Autowired
	private RequestValidator validadorGeneral;
		
	public Mono<ServerResponse> listar(ServerRequest req){
		return ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.listar(), Cliente.class);
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
	
		Mono<Cliente> monoPlato = req.bodyToMono(Cliente.class);

		return monoPlato				
				.flatMap(validadorGeneral::validate)
				.flatMap(service::registrar)
				.flatMap(p -> ServerResponse.created(URI.create(req.uri().toString().concat("/").concat(String.valueOf(p.getId()))))
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
				);
	}
	
	public Mono<ServerResponse> modificar(ServerRequest req) {
		
		Mono<Cliente> monoPlato = req.bodyToMono(Cliente.class);		
		Mono<Cliente> monoBD = service.listarPorId(Integer.parseInt(req.pathVariable("id")));
		
		return monoBD
				.zipWith(monoPlato, (bd, p) -> {				
					bd.setId(bd.getId());
					bd.setNombres(p.getNombres());
					bd.setApellidos(p.getApellidos());
					bd.setFechaNac(p.getFechaNac());
					bd.setUrlFoto(p.getUrlFoto());					
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