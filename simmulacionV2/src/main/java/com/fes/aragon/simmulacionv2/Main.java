package com.fes.aragon.simmulacionv2;

import controlador.ControladorJugador;
import motor.Motor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import modelo.Vehiculo;
import vista.VistaJuego;
import vista.VistaMenu;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Inicializar el Modelo (Vehículo posicionado en el centro)
        Vehiculo miAuto = new Vehiculo(1000,670,68,38);
        Vehiculo contrincante_IA = new Vehiculo(1000,710,68,38);

        // 2. Inicializar el Controlador (Humano)
        ControladorJugador controlHumano = new ControladorJugador();

        // 3. Inicializar la Vista (Lienzo de 800x600)
        VistaJuego vistaJuego = new VistaJuego();

        // 4. Inicializar el Motor y conectar las piezas (Inyección de dependencias)
        Motor motor = new Motor(miAuto, controlHumano, vistaJuego);

        // 5. Configurar la escena de JavaFX
       // StackPane root = new StackPane(vista);
        Scene escenaJuego = new Scene(vistaJuego, 1492, 1054);

        // 6. Conectar el teclado con el ControladorJugador
        escenaJuego.setOnKeyPressed(event -> controlHumano.agregarTecla(event.getCode().toString()));
        escenaJuego.setOnKeyReleased(event -> controlHumano.quitarTecla(event.getCode().toString()));

        VistaMenu vistaMenu = new VistaMenu(() -> {
            primaryStage.setScene(escenaJuego);

            motor.iniciar();
        });

                // Creamos la escena para el menú con las mismas medida
        Scene escenaMenu = new Scene(vistaMenu, 1492, 1054);


        // 7. Configurar y mostrar la ventana
        primaryStage.setTitle("Carrera de la muerte");
        primaryStage.setScene(escenaMenu);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}