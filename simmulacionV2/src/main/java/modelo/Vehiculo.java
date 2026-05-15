package modelo;

public class Vehiculo extends Entidad {
    private double velocidad;
    private double angulo; // 0-360 grados
    private double aceleracion;
    private String estado; // "En movimiento", "Chocado", "Meta"

    private static final int ancho_carro = 68;
    private static final int alto_carro = 38;

    public Vehiculo(double x, double y, double ancho, double alto) {
        super(x, y, ancho, alto);
        this.velocidad = 0;
        this.angulo = 180; // Mirando hacia la izquierda inicialmente
        this.aceleracion = 0.005;
        this.estado = "Detenido";
    }
    public Vehiculo() {
        this(1100,690,ancho_carro,alto_carro);
    }


    @Override
    public void actualizar() {

        double radianes = Math.toRadians(angulo);


        double nuevoX = getX() + Math.cos(radianes) * velocidad;
        double nuevoY = getY() + Math.sin(radianes) * velocidad;

        setX(nuevoX);
        setY(nuevoY);


        if (velocidad > 0) {
            estado = "En movimiento";
        } else {
            estado = "Detenido";
        }

        velocidad *= 0.98;
    }


    public void acelerar() {
        velocidad += aceleracion;
    }

    public void frenar() {
        velocidad -= aceleracion * 2;
        if (velocidad < 0) velocidad = 0;
    }

    public void girar(double deltaAngulo) {
        // solo se mmueve si esta avanzando el carro
        if (velocidad > 0.1) {
            this.angulo += deltaAngulo/20;
        }
    }

    // Getters y Setters
    public double getVelocidad() { return velocidad; }
    public double getAngulo() { return angulo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getAlto_carro() { return alto_carro; }
    public int getAncho_carro() { return ancho_carro; }

}
