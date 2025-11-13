package com.logitrack.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    @NotBlank
    private String nombre;

    @NotBlank
    private String categoria;

    @Min(0)
    private int stock;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal precio;
}