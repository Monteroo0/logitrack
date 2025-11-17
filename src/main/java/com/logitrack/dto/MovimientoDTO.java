package com.logitrack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {
    private LocalDateTime fecha;

    @NotBlank
    private String tipo;

    private Long usuarioId;

    private Long bodegaOrigenId;
    private Long bodegaDestinoId;

    @NotNull
    private List<Item> productos;

    @jakarta.validation.constraints.Size(max = 500)
    private String observaciones;

    private String usuarioNombre;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        @NotNull
        private Long productoId;
        @Min(1)
        private Integer cantidad;
    }
}