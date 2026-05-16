package controlador;

import modelo.Vehiculo;
import java.util.HashSet;
import java.util.Set;

public class ControladorJugador implements Controlador {
    // Aquí guardaremos las teclas que el jugador está presionando actualmente
    private Set<String> teclasActivas = new HashSet<>();

    // Método para que la vista (JavaFX) nos avise qué tecla se presionó
    public void agregarTecla(String codigoTecla) {
        teclasActivas.add(codigoTecla);
    }

    // Método para que la vista nos avise qué tecla se soltó
    public void quitarTecla(String codigoTecla) {
        teclasActivas.remove(codigoTecla);
    }

    @Override
    public void procesarAcciones(Vehiculo vehiculo) {
        // Evaluamos qué teclas están presionadas y controlamos el vehículo
        if (teclasActivas.contains("UP") || teclasActivas.contains("W")) {
            vehiculo.acelerar();
        }
        if (!teclasActivas.contains("DOWN") & !teclasActivas.contains("S") & !teclasActivas.contains("UP") & !teclasActivas.contains("W")){
            vehiculo.frenar();
        }

        if (teclasActivas.contains("DOWN") || teclasActivas.contains("S")) {
            vehiculo.reversa();
        }
        if (teclasActivas.contains("LEFT") || teclasActivas.contains("A")) {
            vehiculo.girar(-3.0); // Gira a la izquierda

        }
        if (teclasActivas.contains("RIGHT") || teclasActivas.contains("D")) {
            vehiculo.girar(3.0);  // Gira a la derecha

        }
    }
}

