package com.mitocode.repo;

import org.springframework.data.r2dbc.repository.Query;

import com.mitocode.model.Plato;

import reactor.core.publisher.Flux;

public interface IPlatoRepo extends IGenericRepo<Plato, Integer>{

	//SQL
	@Query("SELECT * FROM plato WHERE estado = true")
	Flux<Plato> listarPlatosActivos();
}
