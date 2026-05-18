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

    private org.deeplearning4j.nn.multilayer.MultiLayerNetwork cerebroIA;
    private boolean iaCargada = false;
    private int cooldownIA = 0;      // Para sincronizar los reflejos (FrameSkip)
    private int accionActualIA = 0;  // Memoria muscular

    // 1. EL CONSTRUCTOR AHORA RECIBE LOS OBJETOS CREADOS EN EL MAIN
    public Motor(Vehiculo jugador, Vehiculo ia, Controlador controlador, Render vista, GestorColisiones gestor) {
        // Enlazamos directamente los objetos de la interfaz
        this.vehiculoJugador = jugador;
        this.vehiculo_IA = ia;
        this.controladorActual = controlador;
        this.vista = vista;
        this.gestorFisicas = gestor;

        // Arrancamos cronómetros
        vehiculoJugador.iniciarCronometro();
        vehiculo_IA.iniciarCronometro();


        // 1. CARGAMOS EL CEREBRO (.ZIP)
        try {
            // ¡PON EL NOMBRE EXACTO DE TU ARCHIVO AQUÍ!
            java.io.File archivoCerebro = new java.io.File("modelo_entrenado_EnVivo.zip");

            if (archivoCerebro.exists()) {
                // 'false' significa modo Inferencia (sin optimizadores, ahorra RAM)
                cerebroIA = org.deeplearning4j.nn.multilayer.MultiLayerNetwork.load(archivoCerebro, false);
                iaCargada = true;
                System.out.println(">>> [ÉXITO] El piloto de la IA ha subido al vehículo.");
            } else {
                System.out.println(">>> [ERROR] No se encontró el archivo .zip.");
            }
        } catch (Exception e) {
            System.out.println(">>> Error crítico al leer el archivo .zip");
            e.printStackTrace();
        }


        // ====================================================================
        // 2. MODO ENTRENAMIENTO EN VIVO (La Incubadora) //comentar para competir
        // ====================================================================
//        EntornoPista entorno = new EntornoPista(this.vehiculo_IA, this.gestorFisicas);
//
//        Thread hiloEntrenamiento = new Thread(() -> {
//            System.out.println(">>> [SISTEMA] Iniciando simulación de aprendizaje en vivo...");
//            // ¡DESCOMENTADO! Y le pasamos el nombre del hilo como requiere tu clase EntrenadorIA
//            EntrenadorIA.entrenar(entorno, "_EnVivo");
//        });
//
//        hiloEntrenamiento.setDaemon(true);
//        hiloEntrenamiento.start();
        // ====================================================================

        configurarCiclo();
    }

    private void configurarCiclo() {
        cicloJuego = new AnimationTimer() {
            @Override
            public void handle(long tiempoActual) {
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

                /// ---------------------------------------------------------
                // 2. IA AL VOLANTE (Modo Competencia / Inferencia) comentar para entrenar
                // ---------------------------------------------------------
                if (iaCargada && cerebroIA != null) {

                    // La IA piensa una vez cada 5 fotogramas (Sincronización FrameSkip)
                    if (cooldownIA <= 0) {
                        double[] distancias = vehiculo_IA.getSensores().obtenerDistancias(vehiculo_IA, gestorFisicas);
                        double[] normalizados = new double[5];
                        for (int i = 0; i < 5; i++) {
                            normalizados[i] = distancias[i] / 500.0;
                        }

                        // Convertimos a Tensor y obtenemos la predicción
                        org.nd4j.linalg.api.ndarray.INDArray entrada = org.nd4j.linalg.factory.Nd4j.create(new double[][]{normalizados});
                        org.nd4j.linalg.api.ndarray.INDArray predicciones = cerebroIA.output(entrada);

                        // Guardamos su decisión
                        accionActualIA = org.nd4j.linalg.factory.Nd4j.argMax(predicciones, 1).getInt(0);
                        cooldownIA = 5; // Reseteamos el reloj
                    }
                    cooldownIA--;

                    // Ejecutamos la acción en las físicas reales
                    if (accionActualIA == 1) {
                        vehiculo_IA.girar(-3.0);
                        vehiculo_IA.acelerar();
                        vehiculo_IA.setVelocidad(vehiculo_IA.getVelocidad() * 0.90); // Fricción curva
                    } else if (accionActualIA == 2) {
                        vehiculo_IA.girar(3.0);
                        vehiculo_IA.acelerar();
                        vehiculo_IA.setVelocidad(vehiculo_IA.getVelocidad() * 0.90); // Fricción curva
                    } else {
                        vehiculo_IA.acelerar();
                    }

                    // Físicas y colisiones de la IA
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

                //vehiculo_IA.actualizarCronometro();//comentar para competir

                // ---------------------------------------------------------
                // 3. RENDERIZADO GENERAL
                // ---------------------------------------------------------
                vista.renderizar(vehiculoJugador, vehiculo_IA, gestorFisicas);
            }
        };
    }

    public void iniciar() { cicloJuego.start(); }
    public void detener() { cicloJuego.stop(); }
    public void cambiarControlador(Controlador nuevoControlador) { this.controladorActual = nuevoControlador; }
}