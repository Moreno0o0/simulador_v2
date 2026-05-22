package IA;

import modelo.Vehiculo;
import motor.GestorColisiones;

public class Sensores {

    // Configuración de nuestro "LIDAR"
    private int numRayos = 5; // Lanzaremos 5 rayos (ej. Izquierda, diag-izq, frente, diag-der, derecha)
    private double rangoMaximo = 200.0; // Hasta cuántos píxeles puede "ver" a lo lejos
    private double anguloApertura = 130.0; // El cono de visión total (45° a la izq y 45° a la der)
    double[] distancias = new double[numRayos];
    int distanciaActual;
    /**
     * Este método devuelve un arreglo de distancias NORMALIZADAS (de 0.0 a 1.0).
     * Las redes neuronales profundas (DDQN) funcionan mucho mejor con números entre 0 y 1.
     */
    public double[] obtenerDistancias(Vehiculo vehiculo, GestorColisiones gestor) {
        double anguloInicial = vehiculo.getAngulo() - (anguloApertura / 2.0);
        double incrementoAngulo = anguloApertura / (numRayos - 1);

        // El arreglo sí se limpia en cada llamada (los double inician en 0.0)
        double[] distanciasCalculadas = new double[numRayos];

        for (int i = 0; i < numRayos; i++) {
            double anguloRayo = anguloInicial + (i * incrementoAngulo);
            double rad = Math.toRadians(anguloRayo);
            double cosRayo = Math.cos(rad);
            double sinRayo = Math.sin(rad);

            double rayoX = vehiculo.getX();
            double rayoY = vehiculo.getY();

            int distanciaActual = 0; // <--- ¡CRÍTICO! Debe reiniciarse por cada rayo

            while (distanciaActual < rangoMaximo) {
                rayoX += cosRayo;
                rayoY += sinRayo;
                distanciaActual++;
                if (gestor.esBorde(rayoX, rayoY)) break;
            }
            distanciasCalculadas[i] = distanciaActual;
        }
        this.distancias = distanciasCalculadas;
        return distanciasCalculadas;
    }

    // Getters por si la vista necesita dibujarlos después
    public int getNumRayos() { return numRayos; }
    public double getRangoMaximo() { return rangoMaximo; }
    public double getAnguloApertura() { return anguloApertura; }

    // ¡Asegúrate de que esto esté aquí!
    public double[] getUltimasDistancias() {
        return this.distancias;
    }

}