package com.mitocode.util;

import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

//https://stackoverflow.com/questions/47352280/rest-request-argument-validation-in-router-function
@Component
public class RequestValidator {

  @Autowired
  private Validator validador;

  public <T> Mono<T> validate(T obj) {

    if (obj == null) //Esto evita devolver siempre un error 500, sino que devolvemos adecuadamente 400 :)
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));

    Set<ConstraintViolation<T>> violations = this.validador.validate(obj);
    if (violations == null || violations.isEmpty())
      return Mono.just(obj);

    //return Mono.error(new ConstraintViolationException(violations)); Esto evita devolver siempre un error 500, sino que devolvemos adecuadamente 400 :)
    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }
}