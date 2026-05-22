package com.fes.aragon.simmulacionv2;

import IA.AgenteIA;
import controlador.ControladorJugador;
import motor.Motor;
import motor.GestorColisiones;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelo.Vehiculo;
import modelo.Constantes;
import vista.VistaJuego;
import vista.VistaMenu;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // ====================================================================
        // INTERRUPTOR ENTRENAMIENTO
        // true  = ACTIVAR ENTRENAMIENTO
        // false = MODO COMPETICIÓN
        // ====================================================================
        boolean MODO_ENTRENAMIENTO = false;

        //Inicializar los Modelos usando las dimensiones de las Constantes
        Vehiculo miAuto = new Vehiculo(1000, 670, Constantes.ANCHO_CARRO, Constantes.ALTO_CARRO);
        Vehiculo contrincante_IA = new Vehiculo(1000, 710, Constantes.ANCHO_CARRO, Constantes.ALTO_CARRO);

        //Activar gestores y controladores (Polimorfismo)
        GestorColisiones gestorFisicas = new GestorColisiones();
        ControladorJugador controlHumano = new ControladorJugador();
        AgenteIA controladorIA = new AgenteIA(gestorFisicas, "modelo_entrenado_V1.zip"); //misma ruta que en motor

        //Inicializar la Vista por Capas
        VistaJuego vistaJuego = new VistaJuego(Constantes.ANCHO_VENTANA, Constantes.ALTO_VENTANA);

        //Inicializar el Motor
        Motor motor = new Motor(miAuto, contrincante_IA, controlHumano, controladorIA, vistaJuego, gestorFisicas, MODO_ENTRENAMIENTO);

        //Configurar la escena principal de JavaFX
        Scene escenaJuego = new Scene(vistaJuego, Constantes.ANCHO_VENTANA, Constantes.ALTO_VENTANA);

        // Conectar los eventos del teclado con el controlador del jugador
        escenaJuego.setOnKeyPressed(event -> controlHumano.agregarTecla(event.getCode().toString()));
        escenaJuego.setOnKeyReleased(event -> controlHumano.quitarTecla(event.getCode().toString()));

        //Configuración del menú y transición de escenas
        VistaMenu vistaMenu = new VistaMenu(() -> {
            primaryStage.setScene(escenaJuego);
            motor.iniciar();
        });

        Scene escenaMenu = new Scene(vistaMenu, Constantes.ANCHO_VENTANA, Constantes.ALTO_VENTANA);

        //Despliegue de la ventana gráfica
        primaryStage.setTitle("CARRERA CONTRA LA IA");
        primaryStage.setScene(escenaMenu);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}