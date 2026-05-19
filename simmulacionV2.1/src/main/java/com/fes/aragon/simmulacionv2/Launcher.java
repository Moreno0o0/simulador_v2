package com.fes.aragon.simmulacionv2;


import IA.EntrenadorIA;
import IA.EntornoPista;
import modelo.Vehiculo;
import motor.GestorColisiones;
// import modelo.Vehiculo;
// import controlador.GestorColisiones;

public class Launcher {


        public static void main(String[] args) {

            System.setProperty("org.bytedeco.javacpp.maxthreads", "24");//numero de hilos que usa del procesador

            System.setProperty("org.nd4j.cpu.num_threads", "24");
            System.out.println(">>> INICIANDO CLÚSTER HEADLESS <<<");

            //motor grafico
            Main.main(args);
        }

}