package com.mitocode.model;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
@Table("cliente")
public class Cliente {

	@Id
	private Integer id;

	@NotEmpty
	@Column("nombres")
	private String nombres;

	@NotEmpty
	@Column("apellidos")
	private String apellidos;

	@NotNull
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Column("fecha_nac")
	private LocalDate fechaNac;

	@Column("url_foto")
	private String urlFoto;
}