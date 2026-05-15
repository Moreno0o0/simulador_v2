package motor; // o ingenieria

import javafx.animation.AnimationTimer;
import modelo.Vehiculo;
import controlador.Controlador;
import vista.Render; // Importamos la interfaz

public class Motor {

    private Vehiculo vehiculo;
    private Controlador controladorActual;
    private Render vista; // Agregamos la vista
    private AnimationTimer cicloJuego;

    // Actualizamos el constructor
    public Motor(Vehiculo vehiculo, Controlador controlador, Render vista) {
        this.vehiculo = vehiculo;
        this.controladorActual = controlador;
        this.vista = vista;
        configurarCiclo();
    }

    private void configurarCiclo() {
        cicloJuego = new AnimationTimer() {
            @Override
            public void handle(long tiempoActual) {
                // 1. Leer teclado/IA
                controladorActual.procesarAcciones(vehiculo);
                // 2. Mover el carro (Física)
                vehiculo.actualizar();
                // 3. ¡Dibujar en pantalla!
                vista.renderizar(vehiculo);
            }
        };
    }

    public void iniciar() { cicloJuego.start(); }
    public void detener() { cicloJuego.stop(); }
    public void cambiarControlador(Controlador nuevoControlador) { this.controladorActual = nuevoControlador; }
}