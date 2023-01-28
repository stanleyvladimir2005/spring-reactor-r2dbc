package com.mitocode.repo;

import com.mitocode.model.Bill;
import org.springframework.stereotype.Repository;

@Repository
public interface IBillRepo extends IGenericRepo<Bill,Integer>{
}
