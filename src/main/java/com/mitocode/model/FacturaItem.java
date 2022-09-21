package com.mitocode.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("factura_item")
public class FacturaItem {

	@Id
	private Integer id;

	@Column("cantidad")
	private Integer cantidad;

	private Plato plato;
	// @Column("id_plato")
	// private Integer idPlato;

	public FacturaItem() {
	}

	public FacturaItem(int cantidad, Plato plato) {
		this.cantidad = cantidad;
		// this.plato = plato;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	/*
	 * public Integer getIdPlato() { return idPlato; }
	 * 
	 * public void setIdPlato(Integer idPlato) { this.idPlato = idPlato; }
	 */

	public Plato getPlato() {
		return plato;
	}

	public void setPlato(Plato plato) {
		this.plato = plato;
	}

}
