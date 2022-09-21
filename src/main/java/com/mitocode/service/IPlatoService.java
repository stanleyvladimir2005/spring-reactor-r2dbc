package com.mitocode.service;

import com.mitocode.model.Plato;

import reactor.core.publisher.Flux;

public interface IPlatoService extends ICRUD<Plato, Integer>{
	
	Flux<Plato> listarPlatosActivos();

}
