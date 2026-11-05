package cl.elprofecarreno.app.dao.entity;

public class AudioLibro extends Material {

    private int duracionMinutos;

    public AudioLibro() {
        super();
    }

    public AudioLibro(String titulo, int anioPublicacion, int copiasDisponibles, int duracionMinutos) {
        super(titulo, anioPublicacion, copiasDisponibles);
        setDuracionMinutos(duracionMinutos);
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        if (duracionMinutos <= 0) {
            throw new IllegalArgumentException("La duracion en minutos debe ser mayor a 0.");
        }
        this.duracionMinutos = duracionMinutos;
    }

    @Override
    public double calcularCostoPrestamo() {
        return 1000.0 + duracionMinutos * 5.0;
    }

    @Override
    public String toString() {
        return getTitulo() + " (" + getAnioPublicacion() + ")";
    }
}
