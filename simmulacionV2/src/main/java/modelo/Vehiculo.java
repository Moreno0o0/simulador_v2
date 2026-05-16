package modelo;
import IA.Sensores;

public class Vehiculo extends Entidad {
    private double velocidad;
    private double angulo; // 0-360 grados
    private double aceleracion ;
    private Sensores sensores;

    private String estado; // "En movimiento", "Chocado", "Meta"

    private final double VEL_MAX = 0.1;
    private static final int ANCHO_CARRO = 60;
    private static final int ALTO_CARRO = 30;

    public Vehiculo(double x, double y, double ancho, double alto) {
        super(x, y, ancho, alto);
        this.velocidad = 0;
        this.angulo = 180; // Mirando hacia la izquierda inicialmente
        this.aceleracion = 0.001;
        this.estado = "Detenido";
        this.sensores = new IA.Sensores();
    }
    public Vehiculo() {
        this(1000,690,ANCHO_CARRO,ALTO_CARRO);
    }//posicion inicial

    public Vehiculo(double aceleracion) {
        this();
        this.aceleracion = aceleracion;
    }
    // Agrégalo en tu clase Vehiculo, junto a tus otros métodos
    public void reiniciarPosicion() {
        this.setX(1000);       // Tu posición inicial en X
        this.setY(690);        // Tu posición inicial en Y
        this.angulo = 180;     // Mirando hacia la izquierda
        this.velocidad = 0;    // Lo detenemos por completo
        this.estado = "Detenido";
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

    }

    public void acelerar() {
        if (velocidad < VEL_MAX) {
            velocidad += aceleracion;
        }
    }

    public void frenar() {
        velocidad *= 0.999 ;
        //if (velocidad < 0) velocidad = 0;
    }

    public void reversa() {
        if ((-1*velocidad) < VEL_MAX) {
            velocidad -= aceleracion;
        }
    }

    public void girar(double deltaAngulo) {
        // solo se mmueve si esta avanzando el carro
        if (velocidad > 0.01) {
            this.angulo += deltaAngulo/20;
        }
    }
    public Sensores getSensores() {
        return sensores;
    }

    // Getters y Setters
    public int getAnchoCarro(){return ANCHO_CARRO;}
    public int getAltoCarro(){return ALTO_CARRO;}
    public double getVelocidad() { return velocidad; }
    public void setVelocidad(double velocidad) { this.velocidad = velocidad; }
    public double getAngulo() { return angulo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getAlto_carro() { return ALTO_CARRO; }
    public int getAncho_carro() { return ANCHO_CARRO; }
}
