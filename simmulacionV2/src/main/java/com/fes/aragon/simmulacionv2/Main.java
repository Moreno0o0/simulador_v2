package com.fes.aragon.simmulacionv2;

import IA.EntornoPista;
import IA.EntrenadorIA;
import controlador.ControladorJugador;
import motor.Motor;
import motor.GestorColisiones;
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

        // 1. Inicializar los Modelos y el Gestor de Físicas Compartido
        Vehiculo miAuto = new Vehiculo(1000, 670, 68, 38);
        Vehiculo contrincante_IA = new Vehiculo(1000, 710, 68, 38);

        // Creamos el gestor aquí para que tanto el motor gráfico como el entrenamiento usen las mismas colisiones
        GestorColisiones gestorFisicas = new GestorColisiones();

        // 2. Inicializar el Controlador (Humano)
        ControladorJugador controlHumano = new ControladorJugador();

        // 3. Inicializar la Vista
        VistaJuego vistaJuego = new VistaJuego();

        // 4. Inicializar el Motor (¡PASAMOS LA IA Y EL GESTOR DE FÍSICAS!)
        // *Nota: Mira abajo cómo debes adaptar el constructor de tu clase Motor*
        Motor motor = new Motor(miAuto, contrincante_IA, controlHumano, vistaJuego, gestorFisicas);

        // ====================================================================
        // CONEXIÓN DEL CEREBRO EN TIEMPO REAL (Misma dimensión de memoria)
        // ====================================================================
        // Le pasamos al entorno el MISMO carro y el MISMO gestor de colisiones que JavaFX va a renderizar
        EntornoPista entornoEnVivo = new EntornoPista(contrincante_IA, gestorFisicas);

        Thread hiloEntrenamiento = new Thread(() -> {
            System.out.println(">>> [SISTEMA] Incubando cerebro de la IA con el vehículo visible...");
            // Esto ejecutará el step() directamente sobre el contrincante_IA de la pantalla
            EntrenadorIA.entrenar(entornoEnVivo, "_EnVivo");
        });

        hiloEntrenamiento.setDaemon(true);
        hiloEntrenamiento.start();
        // ====================================================================

        // 5. Configurar la escena de JavaFX
        Scene escenaJuego = new Scene(vistaJuego, 1492, 1054);

        // 6. Conectar el teclado con el ControladorJugador
        escenaJuego.setOnKeyPressed(event -> controlHumano.agregarTecla(event.getCode().toString()));
        escenaJuego.setOnKeyReleased(event -> controlHumano.quitarTecla(event.getCode().toString()));

        VistaMenu vistaMenu = new VistaMenu(() -> {
            primaryStage.setScene(escenaJuego);
            motor.iniciar();
        });

        // Creamos la escena para el menú con las mismas medidas
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