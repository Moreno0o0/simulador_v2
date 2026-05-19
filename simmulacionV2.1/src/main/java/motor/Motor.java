package motor;

import IA.EntornoPista;
import IA.EntrenadorIA;
import javafx.animation.AnimationTimer;
import modelo.Vehiculo;
import controlador.Controlador;
import vista.Render;

public class Motor {

    private Vehiculo vehiculoJugador;
    private Vehiculo vehiculo_IA;
    private Controlador controladorActual;
    private Render vista;
    private AnimationTimer cicloJuego;
    private GestorColisiones gestorFisicas;

    // Variables para independizar el tiempo
    private long tiempoAnterior = 0;
    private double deltaAcumulado = 0;
    // Forzamos 60 actualizaciones lógicas por segundo (16.6 millones de nanosegundos)
    private final double TICK_RATE = 1_000_000_000.0 / 1000.0;

    // Variables de la IA
    private org.deeplearning4j.nn.multilayer.MultiLayerNetwork cerebroIA;
    private boolean iaCargada = false;
    private boolean esModoEntrenamiento; // Guardamos el estado del switch

    private int cooldownIA = 0;
    private int accionActualIA = 0;
    private boolean iaPensando = false;

    // 1. EL CONSTRUCTOR AHORA RECIBE EL SWITCH MAESTRO
    public Motor(Vehiculo jugador, Vehiculo ia, Controlador controlador, Render vista, GestorColisiones gestor, boolean modoEntrenamiento) {
        this.vehiculoJugador = jugador;
        this.vehiculo_IA = ia;
        this.controladorActual = controlador;
        this.vista = vista;
        this.gestorFisicas = gestor;
        this.esModoEntrenamiento = modoEntrenamiento;

        vehiculoJugador.iniciarCronometro();
        vehiculo_IA.iniciarCronometro();

        // ====================================================================
        // DISTRIBUIDOR LÓGICO: ¿ENTRENAR O COMPETIR?
        // ====================================================================
        if (esModoEntrenamiento) {

            // MODO 1: INCUBADORA (Multihilo)
            System.out.println(">>> [MODO ENTRENAMIENTO ACTIVADO] Iniciando simulación...");
            EntornoPista entorno = new EntornoPista(this.vehiculo_IA, this.gestorFisicas);
            Thread hiloEntrenamiento = new Thread(() -> {
                EntrenadorIA.entrenar(entorno, "_EnVivo");
            });
            hiloEntrenamiento.setDaemon(true);
            hiloEntrenamiento.start();

        } else {

            // MODO 2: COMPETENCIA (Inferencia)
            System.out.println(">>> [MODO COMPETENCIA ACTIVADO] Cargando cerebro...");
            try {
                java.io.File archivoCerebro = new java.io.File("modelo_entrenado_VelocidadLuz_v2.zip");
                if (archivoCerebro.exists()) {
                    cerebroIA = org.deeplearning4j.nn.multilayer.MultiLayerNetwork.load(archivoCerebro, false);
                    iaCargada = true;
                    System.out.println(">>> [ÉXITO] El piloto de la IA está listo para correr.");
                } else {
                    System.out.println(">>> [ERROR] No hay .zip para correr. Cambia el modo a true para entrenarlo.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

                // Calculamos cuántos "Ticks" (fracciones de 1/60 de segundo) han pasado
                deltaAcumulado += (tiempoActual - tiempoAnterior) / TICK_RATE;
                tiempoAnterior = tiempoActual;

                // FÍSICAS INDEPENDIENTES DE LA PANTALLA
                // Si la pantalla de la laptop da un tirón, el while se ejecuta varias veces para "ponerse al día"
                while (deltaAcumulado >= 1.0) {
                    actualizarFisicas(); // Aquí sucede toda la magia matemática
                    deltaAcumulado--;
                }

                // RENDERIZADO (Se dibuja lo que sea que haya resultado de las físicas)
                vista.renderizar(vehiculoJugador, vehiculo_IA, gestorFisicas);
            }
        };
    }

    private void actualizarFisicas() {
        // ---------------------------------------------------------
        // 1. TÚ AL VOLANTE (Jugador Humano)
        // ---------------------------------------------------------
        controladorActual.procesarAcciones(vehiculoJugador);

        if (gestorFisicas.cruzoLaMeta(vehiculoJugador)) {
            vehiculoJugador.setEstado("Meta");
            vehiculoJugador.setVelocidad(0);
        } else if (gestorFisicas.chocaConBorde(vehiculoJugador)) {
            vehiculoJugador.reiniciarPosicion();
        } else {
            vehiculoJugador.setEstado("En movimiento");
            vehiculoJugador.actualizar();
        }

        // ---------------------------------------------------------
        // 2. IA AL VOLANTE
        // ---------------------------------------------------------
        if (esModoEntrenamiento) {
            vehiculo_IA.actualizarCronometro();
        }
        else if (iaCargada && cerebroIA != null) {

            // Lógica de pensamiento de la IA
            if (cooldownIA <= 0 && !iaPensando) {

                iaPensando = true; // Bloqueamos la puerta para que no piense dos cosas a la vez

                // 1. Tomamos la foto de los sensores (Esto es instantáneo)
                double[] distancias = vehiculo_IA.getSensores().obtenerDistancias(vehiculo_IA, gestorFisicas);

                // 2. Mandamos la matemática pesada a un núcleo secundario
                java.util.concurrent.CompletableFuture.runAsync(() -> {
                    try {
                        org.nd4j.linalg.api.ndarray.INDArray entradaIA = org.nd4j.linalg.factory.Nd4j.create(new double[][]{distancias}).divi(500.0);
                        org.nd4j.linalg.api.ndarray.INDArray predicciones = cerebroIA.output(entradaIA);
                        accionActualIA = org.nd4j.linalg.factory.Nd4j.argMax(predicciones, 1).getInt(0);
                    } finally {
                        iaPensando = false; // Abrimos la puerta cuando termine
                    }
                });
                cooldownIA = 5;
            }
            cooldownIA--;

            // Físicas del coche IA
            if (accionActualIA == 1) {
                vehiculo_IA.girar(-3.0);
                vehiculo_IA.acelerar();
                vehiculo_IA.setVelocidad(vehiculo_IA.getVelocidad() * 0.90);
            } else if (accionActualIA == 2) {
                vehiculo_IA.girar(3.0);
                vehiculo_IA.acelerar();
                vehiculo_IA.setVelocidad(vehiculo_IA.getVelocidad() * 0.90);
            } else {
                vehiculo_IA.acelerar();
            }

            // Colisiones de la IA
            if (gestorFisicas.cruzoLaMeta(vehiculo_IA)) {
                vehiculo_IA.setEstado("Meta");
                vehiculo_IA.setVelocidad(0);
            } else if (gestorFisicas.chocaConBorde(vehiculo_IA)) {
                vehiculo_IA.reiniciarPosicion();
                accionActualIA = 0;
            } else {
                vehiculo_IA.setEstado("En movimiento");
                vehiculo_IA.actualizar();
            }
            vehiculo_IA.actualizarCronometro();
        }
    }
    public void iniciar() { cicloJuego.start(); }
    public void detener() { cicloJuego.stop(); }
    public void cambiarControlador(Controlador nuevoControlador) { this.controladorActual = nuevoControlador; }
}