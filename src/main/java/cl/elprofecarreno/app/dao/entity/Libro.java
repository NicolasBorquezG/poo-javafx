package cl.elprofecarreno.app.dao.entity;

public class Libro extends Material implements Reservable {

    public static final String ESTADO_BUENO = "Bueno";
    public static final String ESTADO_REGULAR = "Regular";
    public static final String ESTADO_MALO = "Malo";

    private String autor;
    private String estadoConservacion;
    private boolean disponibleParaReserva;

    public Libro() {
        super();
    }

    public Libro(String titulo, int anioPublicacion, int copiasDisponibles,
                 String autor, String estadoConservacion, boolean disponibleParaReserva) {
        super(titulo, anioPublicacion, copiasDisponibles);
        setAutor(autor);
        setEstadoConservacion(estadoConservacion);
        setDisponibleParaReserva(disponibleParaReserva);
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("El autor es obligatorio.");
        }
        this.autor = autor.trim();
    }

    public String getEstadoConservacion() {
        return estadoConservacion;
    }

    public void setEstadoConservacion(String estadoConservacion) {
        if (estadoConservacion == null
                || !(estadoConservacion.equalsIgnoreCase(ESTADO_BUENO)
                || estadoConservacion.equalsIgnoreCase(ESTADO_REGULAR)
                || estadoConservacion.equalsIgnoreCase(ESTADO_MALO))) {
            throw new IllegalArgumentException("Estado de conservacion invalido (Bueno/Regular/Malo).");
        }
        this.estadoConservacion = estadoConservacion;
    }

    @Override
    public boolean isDisponibleParaReserva() {
        return disponibleParaReserva;
    }

    @Override
    public void setDisponibleParaReserva(boolean disponibleParaReserva) {
        this.disponibleParaReserva = disponibleParaReserva;
    }

    @Override
    public double calcularCostoPrestamo() {
        double base = 2000.0;
        if (ESTADO_REGULAR.equalsIgnoreCase(estadoConservacion)) {
            base += 500.0;
        } else if (ESTADO_MALO.equalsIgnoreCase(estadoConservacion)) {
            base += 1500.0;
        }
        if (disponibleParaReserva) {
            base += calcularCostoReserva();
        }
        return base;
    }

    @Override
    public double calcularCostoReserva() {
        return disponibleParaReserva ? 500.0 : 0.0;
    }

    @Override
    public String toString() {
        return getTitulo() + " (" + getAnioPublicacion() + ")";
    }
}