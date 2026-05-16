package motor; // o ingenieria

import javafx.animation.AnimationTimer;
import modelo.Vehiculo;
import controlador.Controlador;
import vista.Render; // Importamos la interfaz

public class Motor {

    private Vehiculo vehiculo;
    private Vehiculo vehiculo_IA;
    private Controlador controladorActual;
    private Render vista; // Agregamos la vista
    private AnimationTimer cicloJuego;
    private GestorColisiones gestorFisicas;
    // Actualizamos el constructor
    public Motor(Vehiculo vehiculo, Controlador controlador, Render vista) {
        this.vehiculo = new Vehiculo(1000,670,68,38);
        this.vehiculo_IA = new Vehiculo(1000,710,68,38);
        this.controladorActual = controlador;
        this.vista = vista;
        this.gestorFisicas = new GestorColisiones();
        configurarCiclo();
    }

    private void configurarCiclo() {
        cicloJuego = new AnimationTimer() {
            @Override
            public void handle(long tiempoActual) {
                // 1. Leer teclado/IA
                controladorActual.procesarAcciones(vehiculo);
                // 2. FÍSICAS Y COLISIONES: Preguntamos antes de mover
                // Le preguntamos al gestor si el vehículo va a chocar
                if (gestorFisicas.cruzoLaMeta(vehiculo)) {

                    vehiculo.setEstado("Meta");
                    vehiculo.setVelocidad(0);

                } else if(gestorFisicas.chocaConBorde(vehiculo)) {
                    vehiculo.reiniciarPosicion();
                    }else{
                    vehiculo.setEstado("En movimiento");
                    vehiculo.actualizar();
                }

                vista.renderizar(vehiculo,vehiculo_IA, gestorFisicas);
            }
        };
    }

    public void iniciar() { cicloJuego.start(); }
    public void detener() { cicloJuego.stop(); }
    public void cambiarControlador(Controlador nuevoControlador) { this.controladorActual = nuevoControlador; }
}