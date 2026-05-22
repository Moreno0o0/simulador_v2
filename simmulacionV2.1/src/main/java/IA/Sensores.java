package IA;

import modelo.Vehiculo;
import motor.GestorColisiones;

public class Sensores {

    // Configuración de nuestro "LIDAR"
    private int numRayos = 5;
    private double rangoMaximo = 200.0; // Hasta cuántos píxeles puede ver
    private double anguloApertura = 130.0; // El cono de visión total
    double[] distancias = new double[numRayos];
    int distanciaActual;

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

            int distanciaActual = 0;

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


    public int getNumRayos() { return numRayos; }
    public double getRangoMaximo() { return rangoMaximo; }
    public double getAnguloApertura() { return anguloApertura; }
    public double[] getUltimasDistancias() {
        return this.distancias;
    }

}