package com.mitocode.repo;

import com.mitocode.model.Dish;
import org.springframework.stereotype.Repository;

@Repository
public interface IDishRepo extends IGenericRepo<Dish, Integer> {
}
