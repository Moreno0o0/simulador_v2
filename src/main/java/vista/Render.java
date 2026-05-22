package vista;

import modelo.Vehiculo;
import motor.GestorColisiones;


public interface Render {
    void renderizar(Vehiculo jugador, Vehiculo agenteIA, GestorColisiones gestorColisiones,double cuentaRegresiva);
}
