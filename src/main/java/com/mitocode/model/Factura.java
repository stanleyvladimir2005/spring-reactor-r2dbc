package com.mitocode.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Table("factura")
public class Factura {

	@Id
	private Integer id;

	@Size(min = 3)
	@Column("descripcion")
	private String descripcion;

	@Column("observacion")
	private String observacion;

	// "Nested entities are not supported",
	// private Cliente cliente;
	@Column("id_cliente")
	private Integer idCliente;
	
	// https://stackoverflow.com/questions/53742800/mapping-complex-field-of-an-object-to-a-text-field-serialized-as-json-before-s
	@Transient
	private String items;

	@JsonIgnore
	@Transient
	private List<FacturaItem> itemsList;

	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column("creado_en")
	private LocalDateTime creadoEn = LocalDateTime.now();
}