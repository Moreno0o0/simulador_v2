package motor; // O el paquete donde decidas ponerlo

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import modelo.Vehiculo;

public class GestorColisiones {

    private Image mascara_img;
    private PixelReader lectorPixeles;

    private static final double META_X = 1050;
    private static final double META_Y = 640;
    private static final double META_ANCHO = 50;
    private static final double META_ALTO = 90;

    public GestorColisiones() {
        // El gestor carga su propia pista al nacer
        try {
            mascara_img = new Image(getClass().getResourceAsStream("/assets/track_B&W.png"));
            lectorPixeles = mascara_img.getPixelReader();
        } catch (Exception e) {
            System.out.println("Error al cargar la máscara: " + e.getMessage());
        }
    }

    public boolean chocaConBorde(Vehiculo vehiculo) {
        if (lectorPixeles == null) return false; // Prevención de errores

        // Calculamos la trompa del carro usando las medidas reales del Vehiculo
        double anguloRadianes = Math.toRadians(vehiculo.getAngulo());
        double trompaX = vehiculo.getX() + Math.cos(anguloRadianes) * (vehiculo.getAncho_carro() / 4.0);
        double trompaY = vehiculo.getY() + Math.sin(anguloRadianes) * (vehiculo.getAncho_carro() / 4.0);

        //Verificamos si la trompa sale de la imagen
        if (trompaX < 0 || trompaX >= mascara_img.getWidth() ||
                trompaY < 0 || trompaY >= mascara_img.getHeight()) {
            return true;
        }

        // Leemos el color y determinamos el choque
        Color colorPixel = lectorPixeles.getColor((int) trompaX, (int) trompaY);
        return colorPixel.getBrightness() < 0.5;
    }

    public boolean cruzoLaMeta(Vehiculo vehiculo) {

        double carroX = vehiculo.getX();
        double carroY = vehiculo.getY();

        return carroX >= META_X && carroX <= (META_X + META_ANCHO) &&
                carroY >= META_Y && carroY <= (META_Y + META_ALTO);
    }

    public boolean esBorde(double x, double y) {
        if (lectorPixeles == null) return true;

        // Si el rayo sale de la pantalla, lo contamos como borde
        if (x < 0 || x >= mascara_img.getWidth() || y < 0 || y >= mascara_img.getHeight()) {
            return true;
        }

        // Leemos el color si es negro fuera de pista
        return lectorPixeles.getColor((int) x, (int) y).getBrightness() < 0.5;
    }

    public double getMetaX() { return META_X; }
    public double getMetaY() { return META_Y; }
    public double getMetaAncho() { return META_ANCHO; }
    public double getMetaAlto() { return META_ALTO; }

}