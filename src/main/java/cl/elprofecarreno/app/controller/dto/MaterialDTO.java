package cl.elprofecarreno.app.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDTO {

    private String titulo;
    private int anioPublicacion;
    private int copiasDisponibles;
    private double costoPrestamo;
}