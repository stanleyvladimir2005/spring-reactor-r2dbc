package com.mitocode.model;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("bill")
public class Bill {

	@Id
	private Integer id;

	@Size(min = 3)
	@Column("description")
	private String description;

	@Column("observation")
	private String observation;

	@Column("id_client")
	private Integer idClient;
	
	@Transient
	private String items;

	@JsonIgnore
	@Transient
	private List<BillItem> itemsList;

	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column("create_in")
	private LocalDateTime createIn = LocalDateTime.now();
}