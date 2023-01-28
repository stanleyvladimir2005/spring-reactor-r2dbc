package com.mitocode.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("client")
public class Client {

    @Id
    private Integer Id;

    @NotEmpty
    @Column("first_name")
    private String firstName;

    @NotEmpty
    @Column("last_name")
    private String lastName;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column("birthday")
    private LocalDate birthday;

    @Column("url_photo")
    private String urlPhoto;
}
