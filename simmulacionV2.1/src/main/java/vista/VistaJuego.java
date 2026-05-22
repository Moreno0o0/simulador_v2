//aquí se carga todo lo que se muestra en pantalla
package vista;

import IA.Sensores;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import modelo.Vehiculo;
import modelo.Constantes;
import javafx.scene.layout.StackPane;
import motor.GestorColisiones;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class VistaJuego extends StackPane implements Render {

    private static final double ANGULO_BASE_PNG = 180;

    //cargar pista y carros
    private final Image pista_img = new Image("/assets/track.png");
    //private final Image pista_img = new Image("/assets/track_B&W.png");// pista de colisicones en blanco y negro para mantenimiento de codigo
    private final Image carro_jugador_img = new Image("/assets/camaro.png");
    private final Image carro_contrincante = new Image("/assets/mustang.png");

    private final Font fuenteVictoriaTitulo = new Font("Arial", 80);
    private final Font fuenteVictoriaSub = new Font("Arial", 30);

    private Canvas canvasFondo;
    private Canvas canvasDinamico;

    public VistaJuego(double ancho, double alto) {

        this.setPrefSize(ancho,alto);

        canvasFondo = new Canvas(ancho,alto);
        canvasDinamico = new Canvas(ancho,alto);

        GraphicsContext gcFondo = canvasFondo.getGraphicsContext2D();
        gcFondo.drawImage(pista_img,0,0);

        this.getChildren().addAll(canvasFondo,canvasDinamico);
    }
    public VistaJuego() {
        this(Constantes.ANCHO_VENTANA, Constantes.ALTO_VENTANA);//sobrecarga

    }

    @Override
    public void renderizar(Vehiculo jugador, Vehiculo agenteIA, GestorColisiones gestorColisiones,double cuentaRegresiva) {

        GraphicsContext gc = canvasDinamico.getGraphicsContext2D();

        gc.clearRect(0, 0, getWidth(), getHeight()); //Limpiar la pantalla del frame anterior

        gc.setTextAlign(TextAlignment.CENTER);

        // Obtenemos las coordenadas reales del gestor
        //double mx = gestorColisiones.getMetaX();
        //double my = gestorColisiones.getMetaY();
        //double mw = gestorColisiones.getMetaAncho();
        //double mh = gestorColisiones.getMetaAlto();

        // Dibuja un rectangulo justo en la meta
        //gc.setFill(Color.rgb(0, 255, 0, 0.4));
        //gc.fillRect(mx, my, mw, mh);
        //gc.setStroke(Color.GREEN);
        //gc.setLineWidth(2);
        //gc.strokeRect(mx, my, mw, mh);

        if ("Meta".equals(jugador.getEstado())) {
            gc.setFill(Color.LIME);
            gc.setFont(fuenteVictoriaTitulo);
            gc.fillText("¡VICTORIA!", Constantes.ANCHO_VENTANA/ 2, 500);

            gc.setFill(Color.WHITE);
            gc.setFont(fuenteVictoriaSub);
            gc.fillText("Felicidades por terminar la carrera", Constantes.ANCHO_VENTANA / 2, 560);
        }
        else if ("Meta".equals(agenteIA.getEstado())) {
            gc.setFill(Color.RED);
            gc.setFont(fuenteVictoriaTitulo);
            gc.fillText("¡DERROTA!", modelo.Constantes.ANCHO_VENTANA / 2.0, 500);

            gc.setFill(Color.WHITE);
            gc.setFont(fuenteVictoriaSub);
            gc.fillText("La Inteligencia Artificial ha dominado el circuito", modelo.Constantes.ANCHO_VENTANA / 2.0, 560);
        }
        else if (cuentaRegresiva > 0) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Impact", 150));
            // Imprime 3, 2, 1 redondeando hacia arriba
            gc.fillText(String.valueOf((int) Math.ceil(cuentaRegresiva)), modelo.Constantes.ANCHO_VENTANA / 2.0, modelo.Constantes.ALTO_VENTANA / 2.0);
        }
        else if (cuentaRegresiva > -1.0) { // Se mantiene en pantalla por 1 segundo extra
            gc.setFill(Color.LIME);
            gc.setFont(new Font("Impact", 150));
            gc.fillText("¡GO!", modelo.Constantes.ANCHO_VENTANA / 2.0, modelo.Constantes.ALTO_VENTANA / 2.0);
        }
        gc.setTextAlign(TextAlignment.LEFT);

        Sensores sens = agenteIA.getSensores();

        if (sens != null) {
            double[] distanciasActuales = sens.getUltimasDistancias();
            //double[] distanciasActuales = sens.obtenerDistancias(agenteIA, gestorColisiones);
            double apertura = sens.getAnguloApertura();
            double numRayos = sens.getNumRayos();
            double rangoMax = sens.getRangoMaximo();

            // Obtenemos el ángulo real hacia donde se mueve el carro en la física
            double anguloFisico = agenteIA.getAngulo();
            double radCarro = Math.toRadians(anguloFisico);

            // Calculamos la coordenada exacta de la trompa en la pista
            double trompaX = agenteIA.getX() + Math.cos(radCarro) * (Constantes.ANCHO_CARRO / 2.0);
            double trompaY = agenteIA.getY() + Math.sin(radCarro) * (Constantes.ALTO_CARRO / 2.0);

            // Direcciones de los rayos
            double anguloInicial = anguloFisico - (apertura / 2.0);
            double incremento = apertura / (numRayos - 1);

            gc.setLineWidth(1.5);
            for (int i = 0; i < numRayos; i++) {
                double dist = distanciasActuales[i];
                double radRayo = Math.toRadians(anguloInicial + (i * incremento));

                // Destino final del rayo en la pista
                double finalX = trompaX + (Math.cos(radRayo) * dist);
                double finalY = trompaY + (Math.sin(radRayo) * dist);

                // Colores
                if (dist >= rangoMax * 0.98) {
                    gc.setStroke(Color.LIME);
                } else {
                    double factorPeligro = 1.0 - (dist / rangoMax);if (factorPeligro < 0.3) gc.setStroke(Color.YELLOW);
                    else if (factorPeligro < 0.7) gc.setStroke(Color.ORANGE);
                    else gc.setStroke(Color.RED);
                }

                // Dibujar línea desde la trompa real hasta el impacto
                gc.strokeLine(trompaX, trompaY, finalX, finalY);

                // Punto de impacto
                if (dist < rangoMax * 0.98) {
                    gc.setFill(Color.RED);
                    gc.fillOval(finalX - 2, finalY - 2, 4, 4);
                }
            }
        }


        gc.save();
        gc.translate(agenteIA.getX(), agenteIA.getY());
        gc.rotate(agenteIA.getAngulo() + ANGULO_BASE_PNG);
        gc.drawImage(
                carro_contrincante, // Usamos la imagen de la IA
                -Constantes.ANCHO_CARRO / 2.0,
                -Constantes.ALTO_CARRO / 2.0,
                Constantes.ANCHO_CARRO,
                Constantes.ALTO_CARRO
        );
        gc.restore();

        // Dibujar el vehículo
        gc.save();
        gc.translate(jugador.getX(), jugador.getY());
        double anguloTotal = jugador.getAngulo() + ANGULO_BASE_PNG;
        gc.rotate(anguloTotal);
        gc.drawImage(
                carro_jugador_img,
                -Constantes.ANCHO_CARRO/ 2.0,  // Restamos mitad del ancho para centrar
                -Constantes.ALTO_CARRO / 2.0,   // Restamos mitad del alto
                Constantes.ANCHO_CARRO,
                Constantes.ALTO_CARRO
        );
        gc.restore();
    }
}