package com.fes.aragon.simmulacionv2;

import controlador.ControladorJugador;
import motor.Motor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import modelo.Vehiculo;
import vista.VistaJuego;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Inicializar el Modelo (Vehículo posicionado en el centro)
        Vehiculo miAuto = new Vehiculo();

        // 2. Inicializar el Controlador (Humano)
        ControladorJugador controlHumano = new ControladorJugador();

        // 3. Inicializar la Vista (Lienzo de 800x600)
        VistaJuego vista = new VistaJuego();

        // 4. Inicializar el Motor y conectar las piezas (Inyección de dependencias)
        Motor motor = new Motor(miAuto, controlHumano, vista);

        // 5. Configurar la escena de JavaFX
        StackPane root = new StackPane(vista);
        Scene scene = new Scene(root, 1600, 1000);

        // 6. Conectar el teclado con el ControladorJugador
        scene.setOnKeyPressed(event -> controlHumano.agregarTecla(event.getCode().toString()));
        scene.setOnKeyReleased(event -> controlHumano.quitarTecla(event.getCode().toString()));

        // 7. Configurar y mostrar la ventana
        primaryStage.setTitle("Carrera de la muerte");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // 8. ¡Arrancar el ciclo de simulación!
        motor.iniciar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}