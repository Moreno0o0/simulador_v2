package motor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;

import modelo.Constantes;
import modelo.Vehiculo;

public class GestorColisiones {

    private BufferedImage mascara_img;

    private static final double META_X = 1050;
    private static final double META_Y = 640;
    private static final double META_ANCHO = 50;
    private static final double META_ALTO = 90;

    public GestorColisiones() {
        // Usamos ImageIO nativo de Java, independiente de JavaFX
        try {
            InputStream is = getClass().getResourceAsStream("/assets/track_B&W.png");
            if (is != null) {
                mascara_img = ImageIO.read(is);
            } else {
                System.err.println("Error: No se encontró track_B&W.png en los recursos.");
            }
        } catch (Exception e) {
            System.out.println("Error al cargar la máscara de físicas: " + e.getMessage());
        }
    }

    public boolean chocaConBorde(Vehiculo vehiculo) {
        if (mascara_img == null) return false;

        // Calculamos la trompa del carro usando las medidas reales
        double anguloRadianes = Math.toRadians(vehiculo.getAngulo());
        double trompaX = vehiculo.getX() + Math.cos(anguloRadianes) * (Constantes.ANCHO_CARRO / 4.0);
        double trompaY = vehiculo.getY() + Math.sin(anguloRadianes) * (Constantes.ANCHO_CARRO / 4.0);

        return esBorde(trompaX, trompaY);
    }

    public boolean cruzoLaMeta(Vehiculo vehiculo) {
        double carroX = vehiculo.getX();
        double carroY = vehiculo.getY();

        return carroX >= META_X && carroX <= (META_X + META_ANCHO) &&
                carroY >= META_Y && carroY <= (META_Y + META_ALTO);
    }

    public boolean esBorde(double x, double y) {
        if (mascara_img == null) return true;

        // Si el rayo sale de la pantalla, es muro
        if (x < 0 || x >= mascara_img.getWidth() || y < 0 || y >= mascara_img.getHeight()) {
            return true;
        }

        // Leemos el RGB crudo del pixel
        int rgb = mascara_img.getRGB((int) x, (int) y);
        Color colorPixel = new Color(rgb);

        // Calculamos la luminancia (brillo) matemáticamente
        double brillo = (0.2126 * colorPixel.getRed() + 0.7152 * colorPixel.getGreen() + 0.0722 * colorPixel.getBlue()) / 255.0;

        return brillo < 0.5; // Si el pixel es negro/oscuro, hay colisión
    }

    public double getMetaX() { return META_X; }
    public double getMetaY() { return META_Y; }
    public double getMetaAncho() { return META_ANCHO; }
    public double getMetaAlto() { return META_ALTO; }
}