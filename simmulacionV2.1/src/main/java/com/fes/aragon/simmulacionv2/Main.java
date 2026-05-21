package com.fes.aragon.simmulacionv2;

import IA.AgenteIA;
import controlador.ControladorJugador;
import motor.Motor;
import motor.GestorColisiones;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelo.Vehiculo;
import modelo.Constantes; // Importamos la clase de constantes
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

        // 1. Inicializar los Modelos usando las dimensiones de las Constantes
        Vehiculo miAuto = new Vehiculo(1000, 670, Constantes.ANCHO_CARRO, Constantes.ALTO_CARRO);
        Vehiculo contrincante_IA = new Vehiculo(1000, 710, Constantes.ANCHO_CARRO, Constantes.ALTO_CARRO);

        // 2. Activar gestores y controladores (Polimorfismo limpio)
        GestorColisiones gestorFisicas = new GestorColisiones();
        ControladorJugador controlHumano = new ControladorJugador();
        AgenteIA controladorIA = new AgenteIA(gestorFisicas, "modelo_entrenado_V1.zip"); // Inyección de dependencia

        // 3. Inicializar la Vista por Capas (Doble Canvas)
        VistaJuego vistaJuego = new VistaJuego(Constantes.ANCHO_VENTANA, Constantes.ALTO_VENTANA);

        // 4. Inicializar el Motor
        // ¡Ahora recibe tanto el controladorHumano como el controladorIA!
        Motor motor = new Motor(miAuto, contrincante_IA, controlHumano, controladorIA, vistaJuego, gestorFisicas, MODO_ENTRENAMIENTO);

        // 5. Configurar la escena principal de JavaFX usando Constantes
        Scene escenaJuego = new Scene(vistaJuego, Constantes.ANCHO_VENTANA, Constantes.ALTO_VENTANA);

        // 6. Conectar los eventos del teclado con el controlador del jugador
        escenaJuego.setOnKeyPressed(event -> controlHumano.agregarTecla(event.getCode().toString()));
        escenaJuego.setOnKeyReleased(event -> controlHumano.quitarTecla(event.getCode().toString()));

        // 7. Configuración del menú de inicio y transición de escenas
        VistaMenu vistaMenu = new VistaMenu(() -> {
            primaryStage.setScene(escenaJuego);
            motor.iniciar();
        });

        Scene escenaMenu = new Scene(vistaMenu, Constantes.ANCHO_VENTANA, Constantes.ALTO_VENTANA);

        // 8. Despliegue de la ventana gráfica
        primaryStage.setTitle("Simulador Autónomo con IA");
        primaryStage.setScene(escenaMenu);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}