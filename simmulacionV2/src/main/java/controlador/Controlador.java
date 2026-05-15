package controlador;

import modelo.Vehiculo; // Importamos el vehículo que acabamos de crear

public interface Controlador {
    /**
     * Este método será llamado en cada ciclo del juego (60 veces por segundo).
     * @param vehiculo El vehículo sobre el que el controlador aplicará la aceleración o giro.
     */
    void procesarAcciones(Vehiculo vehiculo);
}
