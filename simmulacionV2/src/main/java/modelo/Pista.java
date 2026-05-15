package modelo;

public class Pista extends Entidad {

    // El "grosor" del asfalto donde el carro puede conducir
    private double anchoCarril;

    public Pista(double x, double y, double ancho, double alto) {
        super(x, y, ancho, alto);
        this.anchoCarril = 150; // 150 píxeles de espacio para conducir
    }

    @Override
    public void actualizar() {
        // La pista es un objeto estático, no se mueve en cada fotograma.
        // Cumplimos con el polimorfismo dejándolo vacío o añadiendo lógica de desgaste a futuro.
    }

    public double getAnchoCarril() {
        return anchoCarril;
    }
}
