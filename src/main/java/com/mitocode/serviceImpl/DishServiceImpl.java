package com.mitocode.serviceImpl;

import com.mitocode.model.Dish;
import com.mitocode.repo.IDishRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends CRUDImpl<Dish, Integer> implements IDishService {

    @Autowired
    private IDishRepo repo;

    @Override
    protected IGenericRepo<Dish, Integer> getRepo() {
        return repo;
    }
}
