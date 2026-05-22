package motor;

import IA.AgenteIA;
import IA.EntornoPista;
import IA.EntrenadorIA;
import javafx.animation.AnimationTimer;
import modelo.Vehiculo;
import controlador.Controlador;
import vista.Render;

public class Motor {

    private Vehiculo vehiculoJugador;
    private Vehiculo vehiculo_IA;
    private Controlador controladorJugador;
    private AgenteIA controladorIA; // Usamos el agente como controlador
    private Render vista;
    private AnimationTimer cicloJuego;
    private GestorColisiones gestorFisicas;
    private double cuentaRegresiva = 3.0;

    private long tiempoAnterior = 0;
    private double deltaAcumulado = 0;
    private final double TICK_RATE = 1_000_000_000.0 / 60.0;
    private boolean esModoEntrenamiento;

    //
    public Motor(Vehiculo jugador, Vehiculo ia, Controlador cJugador, AgenteIA cIA, Render vista, GestorColisiones gestor, boolean modoEntrenamiento) {
        this.vehiculoJugador = jugador;
        this.vehiculo_IA = ia;
        this.controladorJugador = cJugador;
        this.controladorIA = cIA;
        this.vista = vista;
        this.gestorFisicas = gestor;
        this.esModoEntrenamiento = modoEntrenamiento;

        vehiculoJugador.iniciarCronometro();
        vehiculo_IA.iniciarCronometro();

        if (esModoEntrenamiento) {
            System.out.println(">>> [MODO ENTRENAMIENTO ACTIVADO] Iniciando simulación...");
            EntornoPista entorno = new EntornoPista(this.vehiculo_IA, this.gestorFisicas);
            Thread hiloEntrenamiento = new Thread(() -> {
                EntrenadorIA.entrenar(entorno, "_V1");//misma ruta que en main
            });
            hiloEntrenamiento.setDaemon(true);
            hiloEntrenamiento.start();
        } else {
            System.out.println(">>> [MODO COMPETENCIA ACTIVADO] Motor listo.");
        }
        configurarCiclo();
    }

    private void configurarCiclo() {
        cicloJuego = new AnimationTimer() {
            @Override
            public void handle(long tiempoActual) {
                // Inicializamos el reloj en el primer fotograma

                if (tiempoAnterior == 0) {
                    tiempoAnterior = tiempoActual;
                    return;
                }
                double dtSegundos = (tiempoActual - tiempoAnterior) / 1_000_000_000.0;//calcula el tiempo para el semaforo de cuanta regresiva

                if (!esModoEntrenamiento && cuentaRegresiva > -1.0) {
                    cuentaRegresiva -= dtSegundos;
                }

                // Calculamos cuántos "Ticks" (fracciones de 1/60 de segundo) han pasado
                deltaAcumulado += (tiempoActual - tiempoAnterior) / TICK_RATE;
                tiempoAnterior = tiempoActual;


                if (!esModoEntrenamiento && cuentaRegresiva > 0) {
                    deltaAcumulado = 0; // Evitamos que el juego acelere de golpe al terminar de contar
                    vista.renderizar(vehiculoJugador, vehiculo_IA, gestorFisicas, cuentaRegresiva);
                    return; // Abortamos el resto del ciclo para que los carros no se muevan
                }
                // FÍSICAS INDEPENDIENTES DE LA PANTALLA
                // Si la pantalla de la laptop da un tirón, el while se ejecuta varias veces para "ponerse al día"
                while (deltaAcumulado >= 1.0) {
                    actualizarFisicas(); // Aquí sucede toda la magia matemática
                    deltaAcumulado--;
                }

                // RENDERIZADO (Se dibuja lo que sea que haya resultado de las físicas)
                vista.renderizar(vehiculoJugador, vehiculo_IA, gestorFisicas,cuentaRegresiva);
            }
        };
    }

    private void actualizarFisicas() {
        // 1. TÚ AL VOLANTE (Jugador Humano)
        controladorJugador.procesarAcciones(vehiculoJugador);

        if (gestorFisicas.cruzoLaMeta(vehiculoJugador)) {
            vehiculoJugador.setEstado("Meta");
            vehiculoJugador.setVelocidad(0);
        } else if (gestorFisicas.chocaConBorde(vehiculoJugador)) {
            vehiculoJugador.reiniciarPosicion();
        } else {
            vehiculoJugador.setEstado("En movimiento");
            vehiculoJugador.actualizar();
        }

        // 2. IA AL VOLANTE (Arquitectura limpia por Polimorfismo)
        if (esModoEntrenamiento) {
            vehiculo_IA.actualizarCronometro();
        }
        else if (controladorIA != null && controladorIA.isCargado()) {

            // Delegamos la lógica al controlador, el motor ya no procesa matrices
            controladorIA.procesarAcciones(vehiculo_IA);

            // Colisiones de la IA
            if (gestorFisicas.cruzoLaMeta(vehiculo_IA)) {
                vehiculo_IA.setEstado("Meta");
                vehiculo_IA.setVelocidad(0);
            } else if (gestorFisicas.chocaConBorde(vehiculo_IA)) {
                vehiculo_IA.reiniciarPosicion();
            } else {
                vehiculo_IA.setEstado("En movimiento");
                vehiculo_IA.actualizar();
            }
            vehiculo_IA.actualizarCronometro();
        }

    }
    public void iniciar() { cicloJuego.start(); }
    public void detener() { cicloJuego.stop(); }
}