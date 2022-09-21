package com.mitocode.service;

import com.mitocode.model.Factura;

import reactor.core.publisher.Mono;

public interface IFacturaService extends ICRUD<Factura, Integer>{

	Mono<Factura> registrarTransaccional(Factura factura);
}
