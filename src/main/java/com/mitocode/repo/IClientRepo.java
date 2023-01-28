package com.mitocode.repo;

import com.mitocode.model.Client;
import org.springframework.stereotype.Repository;

@Repository
public interface IClientRepo extends IGenericRepo<Client, Integer>{
}
