package com.mitocode.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

//https://docs.spring.io/spring-data/r2dbc/docs/1.0.0.M1/reference/html/#mapping-usage
//https://stackoverflow.com/questions/54266347/orm-framework-for-reactive-applications/54290188#54290188
@Data
@Table("plato")
public class Plato {

	@Id
	private Integer id;

	@Column("nombre")
	private String nombre;

	@Column("precio")
	private Double precio;

	@Column("estado")
	private Boolean estado;
}
