package com.mitocode.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("dish")
public class Dish {

	@Id
	private Integer id;

	@Column("name")
	private String name;

	@Column("price")
	private Double price;

	@Column("status")
	private Boolean status;
}