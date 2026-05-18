package IA;

import modelo.Vehiculo;
import motor.GestorColisiones;

public class Sensores {

    // Configuración de nuestro "LIDAR"
    private int numRayos = 5; // Lanzaremos 5 rayos (ej. Izquierda, diag-izq, frente, diag-der, derecha)
    private double rangoMaximo = 500.0; // Hasta cuántos píxeles puede "ver" a lo lejos
    private double anguloApertura = 130.0; // El cono de visión total (45° a la izq y 45° a la der)

    /**
     * Este método devuelve un arreglo de distancias NORMALIZADAS (de 0.0 a 1.0).
     * Las redes neuronales profundas (DDQN) funcionan mucho mejor con números entre 0 y 1.
     */
    public double[] obtenerDistancias(Vehiculo vehiculo, GestorColisiones gestor) {
        double[] distancias = new double[numRayos];

        // Empezamos a barrer desde la izquierda hacia la derecha
        double anguloInicial = vehiculo.getAngulo() - (anguloApertura / 2.0);
        double incrementoAngulo = anguloApertura / (numRayos - 1); // Separación entre cada rayo

        for (int i = 0; i < numRayos; i++) {
            double anguloRayo = anguloInicial + (i * incrementoAngulo);
            double rad = Math.toRadians(anguloRayo);

            double distanciaActual = 0;

            // Los rayos salen del centro del vehículo
            double rayoX = vehiculo.getX();
            double rayoY = vehiculo.getY();

            // El rayo avanza pixel por pixel hasta chocar o alcanzar su rango máximo
            while (distanciaActual < rangoMaximo) {
                rayoX += Math.cos(rad);
                rayoY += Math.sin(rad);
                distanciaActual++;

                // Si tocó el pasto negro, detenemos el rayo
                if (gestor.esBorde(rayoX, rayoY)) {
                    break;
                }
            }

            // Normalizamos: Si chocó a 50px de un máximo de 200, devuelve 0.25
            // Si el camino está libre, devuelve 1.0
            distancias[i] = distanciaActual ;
        }

        return distancias;
    }

    // Getters por si la vista necesita dibujarlos después
    public int getNumRayos() { return numRayos; }
    public double getRangoMaximo() { return rangoMaximo; }
    public double getAnguloApertura() { return anguloApertura; }


}