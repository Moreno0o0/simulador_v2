package com.fes.aragon.simmulacionv2;

import IA.EntornoPista;
import IA.EntrenadorIA;
import controlador.ControladorJugador;
import motor.Motor;
import motor.GestorColisiones;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelo.Vehiculo;
import vista.VistaJuego;
import vista.VistaMenu;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // ====================================================================
        // INTERRUPTOR MAESTRO DE LA IA
        // true  = Activa la incubadora de entrenamiento (segundo plano).
        // false = Carga el archivo .zip para competir a 60 FPS fluidos.
        // ====================================================================
        boolean MODO_ENTRENAMIENTO = false;

        // 1. Inicializar los Modelos y el Gestor de Físicas Compartido
        Vehiculo miAuto = new Vehiculo(1000, 670, 68, 38);
        Vehiculo contrincante_IA = new Vehiculo(1000, 710, 68, 38);

        // 2. Activar colisiones y controladores
        GestorColisiones gestorFisicas = new GestorColisiones();
        ControladorJugador controlHumano = new ControladorJugador();

        // 3. Inicializar la Vista por Capas (Doble Canvas)
        VistaJuego vistaJuego = new VistaJuego();

        // 4. Inicializar el Motor
        // Se envía el flag para delegar el control de los hilos de ejecución
        Motor motor = new Motor(miAuto, contrincante_IA, controlHumano, vistaJuego, gestorFisicas, MODO_ENTRENAMIENTO);

        // ====================================================================
        // ELIMINADO: El bloque manual de "hiloEntrenamiento" se removió de aquí
        // para evitar ejecuciones duplicadas en la memoria.
        // ====================================================================

        // 5. Configurar la escena principal de JavaFX
        Scene escenaJuego = new Scene(vistaJuego, 1492, 1054);

        // 6. Conectar los eventos del teclado con el controlador del jugador
        escenaJuego.setOnKeyPressed(event -> controlHumano.agregarTecla(event.getCode().toString()));
        escenaJuego.setOnKeyReleased(event -> controlHumano.quitarTecla(event.getCode().toString()));

        // 7. Configuración del menú de inicio y transición de escenas
        VistaMenu vistaMenu = new VistaMenu(() -> {
            primaryStage.setScene(escenaJuego);
            motor.iniciar();
        });

        Scene escenaMenu = new Scene(vistaMenu, 1492, 1054);

        // 8. Despliegue de la ventana gráfica
        primaryStage.setTitle("Carrera de la muerte");
        primaryStage.setScene(escenaMenu);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}