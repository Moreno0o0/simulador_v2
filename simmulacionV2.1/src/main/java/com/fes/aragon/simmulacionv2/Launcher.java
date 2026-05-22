package com.fes.aragon.simmulacionv2;

public class Launcher {
        public static void main(String[] args) {

            System.setProperty("org.bytedeco.javacpp.maxthreads", "24");//numero de hilos que usa del procesador
            System.setProperty("org.nd4j.cpu.num_threads", "24");
            System.out.println(">>> INICIANDO JUEGO<<<");
            Main.main(args);
        }
}