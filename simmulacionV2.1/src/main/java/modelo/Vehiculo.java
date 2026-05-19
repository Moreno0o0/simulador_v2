package modelo;
import IA.Sensores;

public class Vehiculo extends Entidad {
    private double velocidad;
    private double angulo; // 0-360 grados
    private double aceleracion ;
    private Sensores sensores;

    //atributos para el tiempo y recompensa de la IA
    private long tiempoInicioEpisodio;
    private double tiempoVivoActual; // En segundos
    private double tiempoVictoria;    // Guardará el tiempo final al ganar
    private boolean haTerminado;

    private double startX;
    private double startY;
    private double startAngulo;

    private String estado; // "En movimiento", "Chocado", "Meta"

    private final double VEL_MAX = 0.2;// 0.1;
    private static final int ANCHO_CARRO = 60;
    private static final int ALTO_CARRO = 30;

    public Vehiculo(double x, double y, double ancho, double alto) {
        super(x, y, ancho, alto);
        this.velocidad = 0;
        this.angulo = 180; // Mirando hacia la izquierda inicialmente
        this.aceleracion = 0.002;//0.001;
        this.estado = "Detenido";
        this.sensores = new IA.Sensores();
        this.startX = x;
        this.startY = y;
        this.startAngulo = 180; // Guardamos angulo base
        this.angulo = 180;
    }
    public Vehiculo() {
        this(1000,690,ANCHO_CARRO,ALTO_CARRO);
    }//posicion inicial

    public Vehiculo(double aceleracion) {
        this();
        this.aceleracion = aceleracion;
    }

    public void reiniciarPosicion() {
        this.setX(this.startX);       // Tu posición inicial en X
        this.setY(this.startY);        // Tu posición inicial en Y
        this.angulo = this.startAngulo;     // Mirando hacia la izquierda
        this.velocidad = 0;
        this.estado = "En movimiento";
        this.iniciarCronometro();
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
    }

    public void reversa() {
        if ((-1*velocidad) < VEL_MAX) {
            velocidad -= aceleracion;
        }
    }

    public void girar(double deltaAngulo) {
        // solo se mmueve si esta avanzando el carro
        if (velocidad > 0) {
            this.angulo += deltaAngulo/20.0;
        }
    }
    public Sensores getSensores() {
        return sensores;
    }

    public void iniciarCronometro() {
        this.tiempoInicioEpisodio = System.currentTimeMillis();
        this.tiempoVivoActual = 0.0;
        this.tiempoVictoria = 0.0;
        this.haTerminado = false;
    }
    public void actualizarCronometro() {
        if (!haTerminado && "En movimiento".equals(this.getEstado())) {
            long tiempoActual = System.currentTimeMillis();
            // Convertimos la diferencia de milisegundos a segundos con decimales
            this.tiempoVivoActual = (tiempoActual - this.tiempoInicioEpisodio) / 1000.0;
        }
    }
    public void detenerCronometroEnMeta() {
        this.haTerminado = true;
        this.tiempoVictoria = this.tiempoVivoActual;
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
    public double getTiempoVivoActual() { return tiempoVivoActual; }
    public double getTiempoVictoria() { return tiempoVictoria; }
    public boolean isHaTerminado() { return haTerminado; }

    public void setAngulo(double v) {this.angulo = v;}

}
