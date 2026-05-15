//aqui se carga todo lo que se muestra en pantalla
package vista;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import modelo.Vehiculo;

public class VistaJuego extends Canvas implements Render {

    private static final int ANCHO_JUEGO = 1492;//mismo que la pista
    private static final int ALTO_JUEGO = 1054;//mismo que la pista
    private static final int ANCHO_CARRO = 68;
    private static final int ALTO_CARRO = 38;
    private static final double ANGULO_BASE_PNG = 180;

    //cargar pista y carros
    private final Image pista_img = new Image("/assets/track.png");
    private final Image carro_jugador_img = new Image("/assets/camaro.png");
    private final Image carro_contrincante = new Image("/assets/mustang.png");

    public VistaJuego(double ancho, double alto) {
        super(ancho, alto); // Define el tamaño de la ventana
    }
    public VistaJuego() {
        this(ANCHO_JUEGO, ALTO_JUEGO);//sobrecarga
    }

    @Override
    public void renderizar(Vehiculo vehiculo) {
        GraphicsContext gc = getGraphicsContext2D();

        gc.clearRect(0, 0, getWidth(), getHeight()); //Limpiar la pantalla del frame anterior

        // 2. Dibujar pista provisional
        gc.drawImage(pista_img, 0, 0);

        gc.save(); // Guardamos el estado normal de la pantalla

        // Dibujar el vehículo

        gc.translate(vehiculo.getX(), vehiculo.getY());
        double anguloTotal = vehiculo.getAngulo() + ANGULO_BASE_PNG;
        gc.rotate(anguloTotal);
        gc.drawImage(
                carro_jugador_img,
                -ANCHO_CARRO/ 2.0,  // Restamos mitad del ancho para centrar
                -ALTO_CARRO / 2.0,   // Restamos mitad del alto
                ANCHO_CARRO,
                ALTO_CARRO
        );
        gc.restore();
    }
}