package IA;

import IA.EntornoPista;
import IA.EntrenadorIA;
import modelo.Constantes;
import motor.GestorColisiones;
import modelo.Vehiculo;

public class MainEntrenamiento {

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println(" INICIANDO MODO DE ENTRENAMIENTO EN CONSOLA");
        System.out.println("=====================================================");
        System.out.println(">>> Preparando el circuito sin gráficos...");

        //Optimizaciones de CPU
        System.setProperty("org.bytedeco.javacpp.maxthreads", "24");
        System.setProperty("org.nd4j.cpu.num_threads", "24");

        //Instanciar exclusivamente los modelos lógicos (Cero interfaces gráficas)
        Vehiculo contrincante_IA = new Vehiculo(1000, 710, Constantes.ANCHO_CARRO, Constantes.ALTO_CARRO);
        GestorColisiones gestorFisicas = new GestorColisiones();

        //Crear el entorno que controla las reglas del juego
        EntornoPista entornoVirtual = new EntornoPista(contrincante_IA, gestorFisicas);

        System.out.println(">>> ¡Acelerador a fondo! La IA está simulando miles de vidas en segundo plano...");
        long tiempoInicio = System.currentTimeMillis();

        //Lanzar el entrenamiento
        EntrenadorIA.entrenar(entornoVirtual, "_V1");// cambiar a mismo que el motor y main

        long tiempoFin = System.currentTimeMillis();
        long minutosTotales = ((tiempoFin - tiempoInicio) / 1000) / 60;

        System.out.println("=====================================================");
        System.out.println(" ENTRENAMIENTO COMPLETADO EN " + minutosTotales + " MINUTOS");
        System.out.println("=====================================================");
        System.out.println(">>> Tu archivo 'modelo_entrenado_VelocidadLuz.zip' está listo.");
    }
}