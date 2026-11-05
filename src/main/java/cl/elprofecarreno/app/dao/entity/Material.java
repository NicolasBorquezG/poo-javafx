package cl.elprofecarreno.app.dao.entity;

public abstract class Material {

    private String titulo;
    private int anioPublicacion;
    private int copiasDisponibles;

    protected Material() {
    }

    protected Material(String titulo, int anioPublicacion, int copiasDisponibles) {
        setTitulo(titulo);
        setAnioPublicacion(anioPublicacion);
        setCopiasDisponibles(copiasDisponibles);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El titulo es obligatorio.");
        }
        this.titulo = titulo.trim();
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        if (anioPublicacion <= 0) {
            throw new IllegalArgumentException("El anio de publicacion debe ser mayor a 0.");
        }
        this.anioPublicacion = anioPublicacion;
    }

    public int getCopiasDisponibles() {
        return copiasDisponibles;
    }

    public void setCopiasDisponibles(int copiasDisponibles) {
        if (copiasDisponibles < 0) {
            throw new IllegalArgumentException("Las copias disponibles no pueden ser negativas.");
        }
        this.copiasDisponibles = copiasDisponibles;
    }

    public abstract double calcularCostoPrestamo();
}