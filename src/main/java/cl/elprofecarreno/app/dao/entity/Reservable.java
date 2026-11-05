package cl.elprofecarreno.app.dao.entity;

public interface Reservable {

    boolean isDisponibleParaReserva();

    void setDisponibleParaReserva(boolean disponibleParaReserva);

    double calcularCostoReserva();
}