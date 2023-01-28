package com.mitocode.service;

import com.mitocode.model.Bill;
import reactor.core.publisher.Mono;

public interface IBillService extends ICRUD <Bill, Integer>{

    Mono<Bill> saveTransactional(Bill bill);
}
