package com.fes.aragon.simmulacionv2;

// Asegúrate de importar tus clases de IA y Modelos aquí
import IA.EntrenadorIA;
import IA.EntornoPista;
import modelo.Vehiculo;
import motor.GestorColisiones;
// import modelo.Vehiculo;
// import controlador.GestorColisiones;

public class Launcher {


        public static void main(String[] args) {
            // Le damos permiso a ND4J de usar todos los hilos lógicos
            System.setProperty("org.bytedeco.javacpp.maxthreads", "24");
            System.out.println(">>> INICIANDO CLÚSTER HEADLESS <<<");

            // Arrancamos el Motor Gráfico
            Main.main(args);
        }

}