package com.mitocode.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitocode.model.Plato;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.IPlatoRepo;
import com.mitocode.service.IPlatoService;

import reactor.core.publisher.Flux;

@Service
public class PlatoServiceImpl extends CRUDImpl<Plato, Integer> implements IPlatoService{

	@Autowired
	private IPlatoRepo repo;
	
	@Override
	protected IGenericRepo<Plato, Integer> getRepo() {		
		return repo; 
	}

	@Override
	public Flux<Plato> listarPlatosActivos() {
		return repo.listarPlatosActivos();
	}

}
