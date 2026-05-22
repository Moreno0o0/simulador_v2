package modelo;
import IA.Sensores;

public class Vehiculo extends Entidad {
    private double velocidad;
    private double angulo;
    private double aceleracion;
    private Sensores sensores;

    private long tiempoInicioEpisodio;
    private double tiempoVivoActual;
    private double tiempoVictoria;
    private boolean haTerminado;

    private double startX;
    private double startY;
    private double startAngulo;

    private String estado;
    private final double VEL_MAX = 3.3;

    // Constructores usando las Constantes y enviando al padre (super)
    public Vehiculo(double x, double y, double ancho, double alto) {
        super(x, y, ancho, alto); // <-- Herencia pura
        this.velocidad = 0;
        this.angulo = 180;
        this.aceleracion = 0.033;
        this.estado = "Detenido";
        this.sensores = new Sensores();
        this.startX = x;
        this.startY = y;
        this.startAngulo = 180;
    }

    public Vehiculo() {
        this(1000, 690, Constantes.ANCHO_CARRO, Constantes.ALTO_CARRO);
    }

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
        velocidad *= 0.98 ;
    }

    public void reversa() {
        if ((-1*velocidad) < VEL_MAX) {
            velocidad -= aceleracion;
        }
    }

    public void girar(double deltaAngulo) {
        // solo se mmueve si esta avanzando el carro
        if (velocidad > 0) {
            this.angulo += (deltaAngulo * 0.8); // Antes se dividía entre 20
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
    public void ejecutarAccionIA(int accion) {
        if (accion == 1) { // Izquierda
            this.girar(-3.0);
            this.acelerar();
            this.setVelocidad(this.getVelocidad() * 0.98); // Fricción reducida por tick
        } else if (accion == 2) { // Derecha
            this.girar(3.0);
            this.acelerar();
            this.setVelocidad(this.getVelocidad() * 0.98); // Fricción reducida por tick
        } else if (accion == 0) { // Recto
            this.acelerar();
        }
    }

    // Getters y Setters
    public double getVelocidad() { return velocidad; }
    public void setVelocidad(double velocidad) { this.velocidad = velocidad; }
    public double getAngulo() { return angulo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }


}
